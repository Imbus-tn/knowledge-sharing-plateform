from flask import Flask, request, jsonify
from model import ContentRecommender
import os
import logging

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = Flask(__name__)

# Initialize recommender
recommender = ContentRecommender(use_faiss=True)

# Load data during app startup
try:
    logger.info("Initializing recommender system...")
    data_dir = os.path.join(os.path.dirname(__file__), "data")
    recommender.load_and_preprocess_data(data_dir=data_dir)
    logger.info("Recommender system initialized successfully.")
except Exception as e:
    logger.error(f"Failed to initialize recommender system: {str(e)}")

@app.route('/recommend', methods=['GET', 'POST'])
def recommend():
    """Generate content recommendations based on user input."""
    try:
        if not recommender.is_ready():
            return jsonify({"error": "Recommender system not initialized"}), 503

        # Parse input parameters
        if request.method == 'POST':
            data = request.get_json()
            content_title = data.get('title', '').strip()
            top_n = int(data.get('top_n', 5))
            similarity_weight = float(data.get('similarity_weight', 0.7))
            popularity_weight = float(data.get('popularity_weight', 0.3))
        else:
            content_title = request.args.get('title', '').strip()
            top_n = int(request.args.get('top_n', 5))
            similarity_weight = float(request.args.get('similarity_weight', 0.7))
            popularity_weight = float(request.args.get('popularity_weight', 0.3))

        # Validate input
        if not content_title:
            return jsonify({"error": "Content title is required"}), 400
        if top_n <= 0:
            return jsonify({"error": "top_n must be a positive integer"}), 400
        if not (0 <= similarity_weight <= 1):
            return jsonify({"error": "similarity_weight must be between 0 and 1"}), 400
        if not (0 <= popularity_weight <= 1):
            return jsonify({"error": "popularity_weight must be between 0 and 1"}), 400

        # Generate recommendations
        recommendations = recommender.recommend_as_dict(
            content_title=content_title,
            top_n=top_n,
            similarity_weight=similarity_weight,
            popularity_weight=popularity_weight
        )

        if "error" in recommendations:
            return jsonify(recommendations), 400
            
        return jsonify(recommendations), 200

    except ValueError as ve:
        logger.error(f"ValueError in recommendation: {str(ve)}")
        return jsonify({"error": f"Invalid input: {str(ve)}"}), 400
    except Exception as e:
        logger.error(f"Error in recommendation: {str(e)}")
        return jsonify({"error": str(e)}), 500

@app.route('/health', methods=['GET'])
def health_check():
    """Health check endpoint."""
    status = "healthy" if recommender.is_ready() else "initializing"
    return jsonify({"status": status}), 200

if __name__ == '__main__':
    logger.info("Starting Flask app...")
    app.run(host='0.0.0.0', port=5000, debug=True)