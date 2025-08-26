# feed_app.py
from flask import Flask, request, jsonify
from feed_model import FinalHybridRecommender
import logging
import datetime
import os

os.environ['TF_CPP_MIN_LOG_LEVEL'] = '2'
os.environ['CUDA_VISIBLE_DEVICES'] = '-1'
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = Flask(__name__)
DB_URL = "mysql+pymysql://root:08052001@127.0.0.1:3306/knowledge_platform"

try:
    logger.info("Initializing hybrid recommender...")
    recommender = FinalHybridRecommender(db_url=DB_URL)
    logger.info("Recommender initialized successfully.")
except Exception as e:
    logger.error(f"Failed to initialize recommender: {str(e)}")
    recommender = None

@app.route('/')
def home():
    return jsonify({
        "status": "running",
        "endpoints": {
            "/recommend": "GET/POST with user_id",
            "/health": "GET"
        }
    })

@app.route('/recommend', methods=['GET', 'POST'])
def recommend():
    if not recommender:
        return jsonify({"error": "Recommender not initialized"}), 503

    try:
        data = request.get_json() if request.method == 'POST' else request.args
        user_id = str(data.get('user_id', ''))
        num_recs = min(int(data.get('num_recommendations', 5)), 20)

        if not user_id:
            return jsonify({"error": "user_id is required"}), 400

        logger.info(f"Generating recommendations for user {user_id}")
        recommendations = recommender.recommend(user_id=user_id, top_n=num_recs)

        return jsonify({
            "status": "success",
            "user_id": user_id,
            "recommendations": recommendations,
            "count": len(recommendations),
            "timestamp": datetime.datetime.utcnow().isoformat()
        })

    except Exception as e:
        logger.error(f"Recommendation error: {str(e)}")
        return jsonify({"error": str(e)}), 500

@app.route('/health', methods=['GET'])
def health_check():
    return jsonify({
        "status": "healthy" if recommender else "unhealthy",
        "model_loaded": recommender is not None
    })

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5001, debug=True)