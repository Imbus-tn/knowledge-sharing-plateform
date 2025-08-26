# feed_model.py
import os
import re
import numpy as np
import pandas as pd
from sklearn.metrics.pairwise import cosine_similarity
from sklearn.preprocessing import normalize
import faiss
import nltk
from nltk.corpus import stopwords
from nltk.stem import SnowballStemmer
from sentence_transformers import SentenceTransformer
from langdetect import detect
import logging
from sqlalchemy import create_engine, text
from typing import List, Dict

nltk.download('punkt')
nltk.download('stopwords')

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

class MultilingualTextProcessor:
    def __init__(self):
        self.english_stemmer = SnowballStemmer('english')
        self.french_stemmer = SnowballStemmer('french')
        self.english_stopwords = set(stopwords.words('english'))
        self.french_stopwords = set(stopwords.words('french'))

    def detect_language(self, text: str) -> str:
        try:
            return detect(text)
        except Exception:
            return 'en'

    def process_text(self, text: str) -> str:
        text = re.sub(r'<[^>]+>|[\U00010000-\U0010FFFF]', '', str(text))
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
    def __init__(self, db_url: str, use_faiss: bool = True,
                 model_name: str = 'paraphrase-multilingual-MiniLM-L12-v2'):
        self.text_processor = MultilingualTextProcessor()
        self.db_url = db_url
        self.use_faiss = use_faiss
        self.model_name = model_name
        self._data_loaded = False

        self._load_data_from_mysql()
        self._build_embeddings()
        self._build_user_profiles()

    def _load_data_from_mysql(self):
        try:
            logger.info("Loading data from MySQL...")
            engine = create_engine(self.db_url)

            with engine.connect() as conn:
                # ✅ Join posts with users to get author info
                self.content_df = pd.read_sql(text("""
                    SELECT 
                        p.id AS content_id,
                        p.title,
                        p.description,
                        p.content,
                        p.category,
                        p.image_url,
                        p.view_count,
                        p.like_count,
                        p.share_count,
                        p.author_id,
                        u.name AS author_name,
                        u.email AS author_email,
                        u.role AS author_role,
                        u.avatar_url AS author_avatar_url,
                        p.created_at,
                        p.updated_at
                    FROM posts p
                    JOIN users u ON p.author_id = u.id
                """), con=conn)

                # Load tags
                tags_df = pd.read_sql(text("SELECT post_id, tag FROM post_tags"), con=conn)
                tags_grouped = tags_df.groupby('post_id')['tag'].apply(lambda x: ' '.join(x)).reset_index()
                self.content_df = self.content_df.merge(tags_grouped, left_on='content_id', right_on='post_id', how='left')
                self.content_df['tag'] = self.content_df['tag'].fillna('')

                # Load interactions
                reactions_df = pd.read_sql(text("SELECT user_id, post_id AS content_id, emoji FROM post_reactions"), con=conn)
                favorites_df = pd.read_sql(text("SELECT user_id, post_id AS content_id FROM favorites"), con=conn)
                comments_df = pd.read_sql(text("SELECT author_id AS user_id, post_id AS content_id FROM comments"), con=conn)
                shares_df = pd.read_sql(text("SELECT user_id, post_id AS content_id FROM share"), con=conn)
                reported_df = pd.read_sql(text("SELECT reporter_id AS user_id, post_id AS content_id FROM reported_posts"), con=conn)

            self.content_df.columns = self.content_df.columns.str.strip()
            self.content_df.rename(columns={'tag': 'keywords'}, inplace=True)

            # Process text
            self.content_df['language'] = ''
            self.content_df['processed_text'] = ''
            for idx, row in self.content_df.iterrows():
                combined = f"{row['title']} {row['description']} {row['keywords']}"
                self.content_df.at[idx, 'language'] = self.text_processor.detect_language(combined)
                self.content_df.at[idx, 'processed_text'] = self.text_processor.process_text(combined)

            # Build interactions
            emoji_weights = {'like': 2.0, 'love': 3.0, 'wow': 1.5, 'haha': 1.0, 'sad': 0.5, 'angry': -1.0}
            reactions_df['engagement_weight'] = reactions_df['emoji'].map(emoji_weights).fillna(1.0)

            interactions_list = [
                reactions_df[['user_id', 'content_id', 'engagement_weight']],
                favorites_df.copy().assign(engagement_weight=4.0)[['user_id', 'content_id', 'engagement_weight']],
                comments_df.copy().assign(engagement_weight=4.0)[['user_id', 'content_id', 'engagement_weight']],
                shares_df.copy().assign(engagement_weight=5.0)[['user_id', 'content_id', 'engagement_weight']],
                reported_df.copy().assign(engagement_weight=-2.0)[['user_id', 'content_id', 'engagement_weight']]
            ]
            self.interactions_df = pd.concat(interactions_list, ignore_index=True)

            self._data_loaded = True
            logger.info("Data loaded successfully.")

        except Exception as e:
            logger.error(f"Error loading data: {str(e)}")
            self._data_loaded = False
            raise

    def _build_embeddings(self):
        try:
            logger.info("Generating embeddings...")
            self.model = SentenceTransformer(self.model_name, device='cpu')
            embeddings = []
            for i in range(0, len(self.content_df), 32):
                batch = self.content_df['processed_text'].iloc[i:i+32].tolist()
                embeddings.append(self.model.encode(batch, show_progress_bar=False))
            self.embeddings = np.concatenate(embeddings).astype('float32')
            faiss.normalize_L2(self.embeddings)

            if self.use_faiss:
                self.index = faiss.IndexFlatIP(self.embeddings.shape[1])
                self.index.add(self.embeddings)
            else:
                self.index = None

        except Exception as e:
            logger.error(f"Failed to build embeddings: {str(e)}")
            raise

    def _build_user_profiles(self):
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

            interacted = self.content_df[self.content_df['content_id'].isin(content_ids)]
            lang_dist = interacted['language'].value_counts(normalize=True).to_dict()

            self.user_profiles[user_id] = {
                'embedding': user_embedding.tolist(),
                'language_preference': lang_dist
            }

    # In feed_model.py
    def recommend(self, user_id: str, top_n: int = 10) -> List[Dict]:
        try:
            if not self._data_loaded or user_id not in self.user_profiles:
                return self._cold_start_recommendations(top_n)

            profile = self.user_profiles[user_id]
            user_embedding = np.array(profile['embedding']).astype('float32').reshape(1, -1)
            faiss.normalize_L2(user_embedding)

            if self.use_faiss and self.index is not None:
                distances, indices = self.index.search(user_embedding, top_n * 3)
                top_indices = indices[0]
                scores = distances[0]
            else:
                similarity_scores = cosine_similarity(user_embedding, self.embeddings)[0]
                top_indices = np.argsort(-similarity_scores)[:top_n * 3]
                scores = similarity_scores[top_indices]

            recommendations = []
            seen = set()

            for idx, score in zip(top_indices, scores):
                if idx < 0 or idx >= len(self.content_df):
                    continue
                content_row = self.content_df.iloc[idx]
                content_id = content_row['content_id']
                if content_id in seen:
                    continue
                seen.add(content_id)

                lang = content_row['language']
                current_score = float(score)
                lang_pref = profile.get('language_preference', {})
                if lang in lang_pref:
                    current_score *= (1 + 1.5 * lang_pref[lang])
                else:
                    current_score *= 0.7

                # ✅ Use real createdAt from database
                created_at = content_row.get('created_at') or content_row.get('createdAt')
                if isinstance(created_at, pd.Timestamp):
                    created_at_str = created_at.isoformat()
                elif isinstance(created_at, str):
                    created_at_str = created_at
                else:
                    created_at_str = "2025-01-01T00:00:00Z"

                # ✅ Get real author info
                author_id = content_row.get('author_id', 'unknown')
                author_name = content_row.get('author_name', 'Unknown')
                author_email = content_row.get('author_email', '')
                author_role = content_row.get('author_role', 'USER')
                author_avatar_url = content_row.get('author_avatar_url', None)

                recommendations.append({
                    "content_id": content_id,
                    "title": content_row['title'],
                    "description": content_row['description'],
                    "category": content_row.get('category', ''),
                    "language": lang,
                    "score": min(1.0, max(0.2, current_score)),
                    "createdAt": created_at_str,
                    "author": {
                        "id": author_id,
                        "name": author_name,
                        "email": author_email,
                        "role": author_role,
                        "avatarUrl": author_avatar_url
                    }
                })

            return sorted(recommendations, key=lambda x: -x['score'])[:top_n]

        except Exception as e:
            logger.error(f"Recommendation error: {str(e)}")
            return []
    def _cold_start_recommendations(self, top_n: int = 10) -> List[Dict]:
        try:
            sampled = self.content_df.sample(min(top_n, len(self.content_df)))
            return [{
                'content_id': row['content_id'],
                'title': row['title'],
                'description': row['description'],
                'language': row.get('language', 'en'),
                'score': 0.5,
                'author': {
                    'id': row.get('author_id'),
                    'name': row.get('author_name', 'Unknown'),
                    'avatarUrl': row.get('author_avatar_url')
                }
            } for _, row in sampled.iterrows()]
        except Exception as e:
            logger.error(f"Cold-start error: {str(e)}")
            return []