# model.py

import numpy as np
import pandas as pd
import faiss
from sentence_transformers import SentenceTransformer
from rapidfuzz import fuzz, process
import logging
import os
from typing import Union, Optional, List, Dict
from sklearn.metrics.pairwise import cosine_similarity
from sqlalchemy import create_engine
from sklearn.metrics.pairwise import cosine_similarity
# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

import requests

class ContentRecommender:
    def __init__(self, use_faiss: bool = True, model_name: str = 'all-MiniLM-L6-v2'):
        self.use_faiss = use_faiss
        self.model_name = model_name
        self.model = None
        self.index = None
        self.content_df = None
        self._data_loaded = False
        self.popularity_weights = {'views': 0.5, 'likes': 0.3, 'shares': 0.2}

        # 🔁 Add this: Spring Boot API URL
        self.backend_api_url = "http://localhost:8080/api/content/posts/all"  # Adjust endpoint

    def load_and_preprocess_data(self):
        """Load data from Spring Boot API instead of MySQL."""
        try:
            print("Fetching posts from Spring Boot API...")
            response = requests.get(self.backend_api_url)
            if response.status_code != 200:
                raise Exception(f"Failed to fetch data: {response.status_code}")

            posts = response.json()

            # Convert to DataFrame
        # Convert to DataFrame
            self.content_df = pd.DataFrame([
                {
                    "id": post["id"],
                    "title": post.get("title", ""),
                    "description": post.get("description", ""),
                    "content": post.get("content", ""),
                    "category": post.get("category", ""),
                    "tags": ", ".join(post.get("tags", [])),
                    #  Fix: Use camelCase to match Spring Boot JSON
                    "view_count": post.get("viewCount", 0),
                    "likes": post.get("likeCount", 0),
                    "shares": post.get("shareCount", 0)
                }
                for post in posts
            ])
            # After creating DataFrame
            self.content_df = self.content_df.rename(columns={
                'view_count': 'views',
                'likeCount': 'likes',
                'shareCount': 'shares'
            })

            # Clean text fields
            for col in ['title', 'description', 'content', 'category', 'tags']:
                self.content_df[col] = self.content_df[col].fillna('').astype(str)

            # Create combined features
            self.content_df['combined_features'] = (
                self.content_df['title'] + " " +
                self.content_df['description'] + " " +
                self.content_df['content'] + " " +
                self.content_df['category'] + " " +
                self.content_df['tags']
            )

            # Initialize model
            self.model = SentenceTransformer(self.model_name, device='cpu')
            embeddings = self.model.encode(self.content_df['combined_features'].tolist(), show_progress_bar=False)
            faiss.normalize_L2(embeddings)

            if self.use_faiss:
                self.index = faiss.IndexFlatIP(embeddings.shape[1])
                self.index.add(embeddings)
            else:
                self.embeddings = embeddings

            self._data_loaded = True
            print("✅ Data loaded from Spring Boot API")
        except Exception as e:
            print(f"❌ Failed to load data: {str(e)}")
            self._data_loaded = False
            raise

    def find_best_match(self, title: str, threshold: float = 70) -> Optional[int]:
        """Find best matching title using fuzzy matching."""
        titles = self.content_df['title'].astype(str).tolist()
        result = process.extractOne(title, titles, scorer=fuzz.token_set_ratio, score_cutoff=threshold)
        if result:
            matched_title, score, idx = result
            logger.info(f"Fuzzy match found: '{title}' -> '{matched_title}' (score: {score})")
            return idx
        return None
        
    def recommend_content(
        self,
        content_title: str,
        top_n: int = 5,
        similarity_weight: float = 0.7,
        popularity_weight: float = 0.3
    ) -> Union[pd.DataFrame, str]:
        """Generate recommendations based on input title."""
        try:
            # Validate input
            if not content_title or not isinstance(content_title, str):
                return pd.DataFrame({"Message": ["Please provide a valid content title"]})
            content_title = content_title.strip().lower()
            if not content_title:
                return pd.DataFrame({"Message": ["Please provide a non-empty content title"]})

            # Find potential matches
            potential_matches = self.content_df[
                self.content_df['title'].str.contains(content_title, case=False, na=False)
            ]

            if len(potential_matches) == 0:
                idx = self.find_best_match(content_title)
                if idx is None:
                    return pd.DataFrame({
                        "Message": [f"No content found matching '{content_title}'"]
                    })
                reference_idx = idx
            else:
                reference_idx = potential_matches.index[0]

            # Get reference embedding
            reference_features = self.content_df.loc[reference_idx, 'combined_features']
            query_embedding = self.model.encode([reference_features])
            faiss.normalize_L2(query_embedding)

            # Search similar items
            if self.use_faiss:
                distances, indices = self.index.search(query_embedding, top_n + 1)
                top_indices = [i for i in indices[0] if i != reference_idx][:top_n]
                similarity_scores = distances[0][:len(top_indices)]
            else:
                similarity_scores = cosine_similarity(query_embedding, self.embeddings)[0]
                similar_indices = np.argsort(-similarity_scores)
                top_indices = [i for i in similar_indices if i != reference_idx][:top_n]
                similarity_scores = similarity_scores[top_indices]

            recommendations = self.content_df.iloc[top_indices].copy()
            recommendations['similarity_score'] = similarity_scores

            # Calculate popularity score
            if all(m in recommendations.columns for m in self.popularity_weights):
                recommendations['popularity_score'] = recommendations.apply(
                    lambda x: sum(
                        self.popularity_weights[metric] * x[metric]
                        for metric in self.popularity_weights
                        if metric in x
                    ),
                    axis=1
                )
                recommendations['combined_score'] = (
                    similarity_weight * recommendations['similarity_score'] +
                    popularity_weight * recommendations['popularity_score']
                )
                sort_column = 'combined_score'
            else:
                sort_column = 'similarity_score'

            result_cols = ['id', 'title', 'description', 'category', sort_column]
            if 'url' in self.content_df.columns:
                result_cols.append('image_url')

            return recommendations.sort_values(sort_column, ascending=False)[result_cols].rename(columns={sort_column: 'score'})
        except Exception as e:
            logger.error(f"Error in recommendation: {str(e)}")
            return f"Error generating recommendations: {str(e)}"

    def recommend_as_dict(self, content_title: str, top_n: int = 5,
                      similarity_weight: float = 0.7, popularity_weight: float = 0.3) -> dict:
        """Return recommendations as dictionary for API response, including closest matched title."""

        if not self.is_ready:
            return {"error": "Model not initialized"}

        # Exact matches first
        potential_matches = self.content_df[
            self.content_df['title'].str.contains(content_title, case=False, na=False)
        ]

        matched_title = None
        if len(potential_matches) == 0:
            # Try fuzzy matching
            idx = self.find_best_match(content_title)
            if idx is None:
                return {"error": f"No content found matching '{content_title}'"}
            matched_title = self.content_df.iloc[idx]['title']
        else:
            matched_title = potential_matches.iloc[0]['title']

        # Get recommendations based on matched title
        result = self.recommend_content(
            content_title=matched_title,
            top_n=top_n,
            similarity_weight=similarity_weight,
            popularity_weight=popularity_weight
        )

        if isinstance(result, str):
            return {"error": result}
        elif 'Message' in result:
            return {"error": result['Message'].iloc[0]}

        recommendations = []
        for _, row in result.iterrows():
            item = {
                "id": int(row['id']),
                "title": row['title'],
                "description": row['description'],
                "category": row['category'],
                "score": float(row['score'])
            }
            if 'image_url' in row:
                item['image_url'] = row['image_url']
            recommendations.append(item)

        return {
            "recommendations": recommendations,
            "query": content_title,
            "matched_title": matched_title,
            "count": len(recommendations)
        }

    @property
    def is_ready(self):
        """Return True if the recommender data is loaded."""
        return self._data_loaded
