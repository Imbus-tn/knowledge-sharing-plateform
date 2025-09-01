# app.py
from flask import Flask, request, jsonify
from content_model import ContentRecommender
import requests
import os
import logging
import threading
import time
from flask_cors import CORS

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = Flask(__name__)
CORS(app, origins=["http://localhost:5173"], supports_credentials=True)

# Initialize recommender
recommender = ContentRecommender(use_faiss=True)

def load_model_data():
    """Load model data from Spring Boot API and initialize recommender."""
    try:
        logger.info("Initializing recommender...")
        
        # Use the recommender's built-in method to load data
        recommender.load_and_preprocess_data()
        
        if not recommender.is_ready:
            logger.error("❌ Recommender failed to initialize")
            return False
            
        logger.info("✅ Recommender ready")
        return True
        
    except Exception as e:
        logger.error(f"❌ Failed to load model: {e}")
        return False

# Load data at startup
load_model_data()

# Background thread to reload data periodically
def reload_data_periodically():
    while True:
        try:
            logger.info("🔁 Reloading model data...")
            recommender.load_and_preprocess_data()
            logger.info("✅ Model data reloaded successfully.")
        except Exception as e:
            logger.error(f"❌ Error reloading model: {str(e)}")
        time.sleep(3600)  # Reload every hour

threading.Thread(target=reload_data_periodically, daemon=True).start()

@app.route('/recommend', methods=['GET'])
def recommend():
    title = request.args.get("title", "").strip()
    top_n = int(request.args.get("top_n", 5))

    if not title:
        return jsonify({"error": "Content title is required"}), 400

    if not recommender.is_ready:
        return jsonify({"error": "Recommender not initialized"}), 503

    result = recommender.recommend_as_dict(title, top_n=top_n)

    if "error" in result:
        return jsonify(result), 400

    return jsonify(result), 200


   

@app.route('/health', methods=['GET'])
def health_check():
    """Health check endpoint."""
    status = "healthy" if recommender.is_ready else "unhealthy"
    return jsonify({"status": status}), 200

if __name__ == '__main__':
    logger.info("🚀 Starting Flask app on http://localhost:5000")
    app.run(host='0.0.0.0', port=5000, debug=True)