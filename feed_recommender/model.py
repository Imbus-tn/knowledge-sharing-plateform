import os
import re
import numpy as np
import pandas as pd
from collections import defaultdict
from sklearn.metrics.pairwise import cosine_similarity
from sklearn.preprocessing import normalize
import faiss
import nltk
from nltk.corpus import stopwords
from nltk.stem import SnowballStemmer
from sentence_transformers import SentenceTransformer
from langdetect import detect
import logging
import tensorflow as tf
from typing import List, Dict, Optional, Union

# ========== CRITICAL FIXES ==========
# Disable GPU usage completely
os.environ['CUDA_VISIBLE_DEVICES'] = '-1'
# Reduce TensorFlow logging
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '2'  
# Force PyTorch to CPU
os.environ['TORCH_DEVICE'] = 'cpu'

# Verify environment
print(f"PyTorch device set to: {os.getenv('TORCH_DEVICE', 'Not set')}")
print(f"CUDA visible devices: {os.getenv('CUDA_VISIBLE_DEVICES', 'Not set')}")

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

class MultilingualTextProcessor:
    def __init__(self):
        nltk.download('punkt')
        nltk.download('stopwords')
        self.english_stemmer = SnowballStemmer('english')
        self.french_stemmer = SnowballStemmer('french')
        self.english_stopwords = set(stopwords.words('english'))
        self.french_stopwords = set(stopwords.words('french'))

    def detect_language(self, text: str) -> str:
        try:
            return detect(text)
        except:
            return 'en'

    def process_text(self, text: str) -> str:
        text = re.sub(r'<[^>]+>|[\U00010000-\U0010ffff]', '', str(text))
        lang = self.detect_language(text)
        tokens = nltk.word_tokenize(text.lower())
        if lang == 'fr':
            tokens = [self.french_stemmer.stem(token) for token in tokens
                      if token not in self.french_stopwords and token.isalpha()]
        else:
            tokens = [self.english_stemmer.stem(token) for token in tokens
                      if token not in self.english_stopwords and token.isalpha()]
        return ' '.join(tokens)

class FinalHybridRecommender:
    def __init__(self, data_dir: str = "data", model_path: str = "ncf_model.h5"):
        # Initialize components
        self.text_processor = MultilingualTextProcessor()
        self.data_dir = data_dir
        self.model_path = model_path
        
        # Load data and build models
        self._load_data()
        self._build_embeddings()
        self._load_ncf_model()
        self._build_user_profiles()

    def _load_data(self) -> None:
        """Load and preprocess all data files"""
        try:
            # Load all CSV files
            self.content_df = pd.read_csv(os.path.join(self.data_dir, "content.csv"))
            engagement_df = pd.read_csv(os.path.join(self.data_dir, "Engagement.csv"))
            favorites_df = pd.read_csv(os.path.join(self.data_dir, "Favorites.csv"))
            votes_df = pd.read_csv(os.path.join(self.data_dir, "Vote.csv"))
            self.categories_df = pd.read_csv(os.path.join(self.data_dir, "Category.csv"))

            # Process content data
            self.content_df['keywords'] = self.content_df['keywords'].fillna('').astype(str)
            
            # Language detection and text processing
            self.content_df['language'] = (self.content_df['title'] + ' ' + self.content_df['description']).apply(
                lambda x: self.text_processor.detect_language(x) if pd.notnull(x) else 'en'
            )
            self.content_df['processed_text'] = (self.content_df['title'] + ' ' + self.content_df['description']).apply(
                self.text_processor.process_text
            )

            # Combine all interactions
            engagement_df['engagement_weight'] = engagement_df['type_engagement'].map({
                'view': 1.0, 'like': 2.0, 'bookmark': 3.0,
                'comment': 4.0, 'share': 5.0, 'report': -2.0
            })

            favorites_df['engagement_weight'] = 4.0
            favorites_df['type_engagement'] = 'favorite'

            votes_df['engagement_weight'] = votes_df['vote_type'].map({'Like': 2.0, 'Dislike': -1.0})
            votes_df['type_engagement'] = 'vote'

            self.interactions_df = pd.concat([
                engagement_df[['user_id', 'content_id', 'engagement_weight']],
                favorites_df[['user_id', 'content_id', 'engagement_weight']],
                votes_df[['user_id', 'content_id', 'engagement_weight']]
            ])

        except Exception as e:
            logger.error(f"Error loading data: {str(e)}")
            raise

    def _build_embeddings(self) -> None:
        """Generate multilingual embeddings and FAISS index"""
        logger.info("Generating multilingual embeddings...")
        try:
            model = SentenceTransformer(
                'paraphrase-multilingual-MiniLM-L12-v2',
                device='cpu',  # Force CPU usage
                cache_folder='./model_cache'  # Local cache to prevent redownloads
            )
            
            # Process in batches to prevent memory issues
            batch_size = 32
            embeddings = []
            for i in range(0, len(self.content_df), batch_size):
                batch = self.content_df['processed_text'].iloc[i:i+batch_size].tolist()
                embeddings.append(model.encode(batch, show_progress_bar=False))
            
            self.embeddings = np.concatenate(embeddings).astype('float32')
            
            # Create FAISS index
            self.index = faiss.IndexFlatIP(self.embeddings.shape[1])
            faiss.normalize_L2(self.embeddings)
            self.index.add(self.embeddings)
            
            logger.info(f"Created embeddings with shape: {self.embeddings.shape}")
            
        except Exception as e:
            logger.error(f"Failed to build embeddings: {str(e)}")
            raise

    def _load_ncf_model(self) -> None:
        """Load the trained NCF model"""
        logger.info("Loading NCF model...")
        try:
            if not os.path.exists(self.model_path):
                raise FileNotFoundError(f"Model file not found at {self.model_path}")
                
            self.ncf_model = tf.keras.models.load_model(
                self.model_path,
                custom_objects={
                    'Adam': tf.keras.optimizers.legacy.Adam,  # Updated for TF 2.12
                    'Input': tf.keras.layers.Input
                }
            )
            logger.info("NCF model loaded successfully")
        except Exception as e:
            logger.error(f"Failed to load NCF model: {str(e)}")
            raise

    def _build_user_profiles(self) -> None:
        """Build user profiles based on interactions"""
        logger.info("Building user profiles...")
        self.user_profiles = {}
        content_id_to_idx = {cid: idx for idx, cid in enumerate(self.content_df['content_id'])}
        
        for user_id, group in self.interactions_df.groupby('user_id'):
            content_ids = group['content_id'].values
            strengths = group['engagement_weight'].values

            item_indices = [content_id_to_idx[cid] for cid in content_ids if cid in content_id_to_idx]
            if not item_indices:
                continue

            weights = strengths[:len(item_indices)]
            weights = weights / (np.sum(weights) + 1e-9)

            weighted_embeddings = self.embeddings[item_indices] * weights.reshape(-1, 1)
            user_embedding = np.sum(weighted_embeddings, axis=0)
            user_embedding = normalize(user_embedding.reshape(1, -1))[0]

            interacted_content = self.content_df[self.content_df['content_id'].isin(content_ids)]
            lang_dist = interacted_content['language'].value_counts(normalize=True).to_dict()

            self.user_profiles[user_id] = {
                'embedding': user_embedding.tolist(),
                'language_preference': lang_dist
            }

    def recommend(self, user_id: str, top_n: int = 10) -> List[Dict]:
        """Generate personalized recommendations"""
        try:
            if user_id not in self.user_profiles:
                return self._cold_start_recommendations(top_n)

            profile = self.user_profiles[user_id]
            user_embedding = np.array(profile['embedding'])
            lang_pref = profile.get('language_preference', {})

            # Get content-based recommendations
            user_embedding = user_embedding.astype('float32').reshape(1, -1)
            faiss.normalize_L2(user_embedding)
            distances, indices = self.index.search(user_embedding, top_n * 3)

            recommendations = []
            for idx, score in zip(indices[0], distances[0]):
                if idx < 0 or idx >= len(self.content_df):
                    continue

                content_id = self.content_df.iloc[idx]['content_id']
                content_info = self._get_content_info(content_id)
                if not content_info:
                    continue

                # Apply language preference weighting
                current_score = float(score)
                if content_info['language'] in lang_pref:
                    current_score *= (1 + 1.5 * lang_pref[content_info['language']])
                else:
                    current_score *= 0.7

                recommendations.append({
                    'content_id': content_id,
                    'title': content_info['title'],
                    'description': content_info['description'],
                    'language': content_info['language'],
                    'score': min(1.0, max(0.2, float(current_score)))
                })

            return sorted(recommendations, key=lambda x: -x['score'])[:top_n]

        except Exception as e:
            logger.error(f"Recommendation error: {str(e)}")
            return []

    def _get_content_info(self, content_id: str) -> Optional[Dict]:
        """Get content information by ID"""
        try:
            content = self.content_df[self.content_df['content_id'] == content_id].iloc[0]
            return {
                'title': content['title'],
                'description': content['description'],
                'language': content['language']
            }
        except:
            return None

    def _cold_start_recommendations(self, top_n: int) -> List[Dict]:
        """Generate recommendations for new users"""
        try:
            # Fallback to simple content sampling if no popularity data exists
            recommendations = []
            sampled_content = self.content_df.sample(min(top_n, len(self.content_df)))
            
            for _, row in sampled_content.iterrows():
                recommendations.append({
                    'content_id': row['content_id'],
                    'title': row['title'],
                    'description': row['description'],
                    'language': row.get('language', 'en'),
                    'score': 0.5
                })
            
            return recommendations
        except Exception as e:
            logger.error(f"Cold-start recommendation error: {str(e)}")
            return []

if __name__ == "__main__":
    # Test the recommender
    recommender = FinalHybridRecommender()
    print("Sample recommendations:", recommender.recommend("user1", 3))