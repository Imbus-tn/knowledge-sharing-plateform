import numpy as np
import pandas as pd
import faiss
from sentence_transformers import SentenceTransformer
from rapidfuzz import fuzz, process
import logging
import os
from typing import Union, Optional
from sklearn.metrics.pairwise import cosine_similarity

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

class ContentRecommender:
    def __init__(self, use_faiss: bool = True, model_name: str = 'all-MiniLM-L6-v2'):
        self.use_faiss = use_faiss
        self.model_name = model_name
        self.model = None
        self.index = None
        self.content_df = None
        self._data_loaded = False
        self.popularity_weights = {
            'views': 0.6,
            'likes': 0.3,
            'shares': 0.1
        }

    def is_ready(self):
        return self._data_loaded and self.model is not None

    def load_and_preprocess_data(self, data_dir: str = "data") -> None:
        """Load and preprocess data from local files."""
        try:
            logger.info("Loading and preprocessing data from local files...")
            
            # Verify files exist
            category_path = os.path.join(data_dir, "Category.csv")
            content_path = os.path.join(data_dir, "content.csv")
            
            if not os.path.exists(category_path):
                raise FileNotFoundError(f"Category.csv not found in {data_dir}")
            if not os.path.exists(content_path):
                raise FileNotFoundError(f"content.csv not found in {data_dir}")

            # Load data
            category_df = pd.read_csv(category_path, encoding='latin1')
            self.content_df = pd.read_csv(content_path, encoding='latin1')

            # Clean column names
            self.content_df.columns = self.content_df.columns.str.strip()
            category_df.columns = category_df.columns.str.strip()

            # Find category column
            category_col = None
            for col in self.content_df.columns:
                if 'category' in col.lower() or 'cat' in col.lower():
                    category_col = col
                    break
            
            if not category_col:
                raise ValueError("No category column found in content.csv")

            # Find name column in category data
            name_col = 'name'
            if 'name' not in category_df.columns:
                for col in category_df.columns:
                    if 'name' in col.lower():
                        name_col = col
                        break
                else:
                    raise ValueError("No name column found in Category.csv")

            # Merge data
            self.content_df = self.content_df.merge(
                category_df,
                left_on=category_col,
                right_on='id',
                suffixes=('', '_category')
            ).rename(columns={name_col: 'category_name'})

            # Handle missing data
            text_columns = ['title', 'description', 'keywords']
            for col in text_columns:
                if col in self.content_df.columns:
                    self.content_df[col] = self.content_df[col].fillna('')

            # Convert engagement metrics
            engagement_metrics = ['views', 'likes', 'shares']
            for metric in engagement_metrics:
                if metric in self.content_df.columns:
                    self.content_df[metric] = pd.to_numeric(
                        self.content_df[metric], 
                        errors='coerce'
                    ).fillna(0).astype(float)

            # Create combined features
            self.content_df['combined_features'] = (
                self.content_df['title'].fillna('') + " " +
                self.content_df['description'].fillna('') + " " +
                self.content_df['category_name'].fillna('') + " " +
                self.content_df['keywords'].fillna('')
            )

            # Initialize model
            self.model = SentenceTransformer(self.model_name, device='cpu')
            embeddings = self.model.encode(
                self.content_df['combined_features'].tolist(),
                show_progress_bar=False
            )
            faiss.normalize_L2(embeddings)

            if self.use_faiss:
                self.index = faiss.IndexFlatIP(embeddings.shape[1])
                self.index.add(embeddings)
            else:
                self.embeddings = embeddings

            self._data_loaded = True
            logger.info("Data loaded and preprocessed successfully.")

        except Exception as e:
            self._data_loaded = False
            logger.error(f"Error in data preprocessing: {str(e)}")
            raise

    def find_best_match(self, title: str, threshold: float = 70) -> Optional[int]:
        """Find the best matching content title using fuzzy matching."""
        try:
            titles = self.content_df['title'].astype(str).tolist()
            result = process.extractOne(
                title,
                titles,
                scorer=fuzz.token_set_ratio,
                score_cutoff=threshold
            )
            if result:
                matched_title, score, idx = result
                logger.info(f"Fuzzy match found: '{title}' -> '{matched_title}' (score: {score})")
                return idx
            return None
        except Exception as e:
            logger.error(f"Error in fuzzy matching: {str(e)}")
            return None

    def recommend_content(
        self,
        content_title: str,
        top_n: int = 5,
        similarity_weight: float = 0.7,
        popularity_weight: float = 0.3
    ) -> Union[pd.DataFrame, str]:
        """Generate content recommendations."""
        try:
            # Validate input
            if not content_title or not isinstance(content_title, str):
                return pd.DataFrame({"Message": ["Please provide a valid content title"]})
            
            content_title = content_title.strip().lower()
            if not content_title:
                return pd.DataFrame({"Message": ["Please provide a non-empty content title"]})

            # Find potential matches
            potential_matches = self.content_df[
                self.content_df['title'].str.lower().str.contains(content_title, regex=False)
            ]

            # If no matches, try fuzzy matching
            if len(potential_matches) == 0:
                idx = self.find_best_match(content_title)
                if idx is None:
                    return pd.DataFrame({
                        "Message": [f"No content found matching '{content_title}'"]
                    })
                reference_idx = idx
            else:
                # Use most popular match
                if 'views' in potential_matches.columns:
                    reference_idx = potential_matches['views'].idxmax()
                else:
                    reference_idx = potential_matches.index[0]

            # Get reference features
            reference_features = self.content_df.loc[reference_idx, 'combined_features']
            query_embedding = self.model.encode([reference_features])
            faiss.normalize_L2(query_embedding)

            # Get similar items
            if self.use_faiss:
                distances, indices = self.index.search(query_embedding, top_n + 1)
                # Exclude the reference item itself
                top_indices = [i for i in indices[0] if i != reference_idx][:top_n]
                similarity_scores = distances[0][:len(top_indices)]
            else:
                similarity_scores = cosine_similarity(query_embedding, self.embeddings)[0]
                similar_indices = np.argsort(-similarity_scores)
                top_indices = [i for i in similar_indices if i != reference_idx][:top_n]
                similarity_scores = similarity_scores[top_indices]

            recommendations = self.content_df.iloc[top_indices].copy()
            recommendations['similarity_score'] = similarity_scores

            # Calculate combined score
            if all(m in recommendations.columns for m in self.popularity_weights):
                recommendations['popularity_score'] = recommendations.apply(
                    lambda x: sum(
                        self.popularity_weights[metric] * x[metric] 
                        for metric in self.popularity_weights 
                        if metric in x
                    ) / sum(self.popularity_weights.values()),
                    axis=1
                )
                recommendations['combined_score'] = (
                    similarity_weight * recommendations['similarity_score'] +
                    popularity_weight * recommendations['popularity_score']
                )
                sort_column = 'combined_score'
            else:
                sort_column = 'similarity_score'

            # Format results
            result_cols = ['title', 'description', 'category_name', sort_column]
            if 'url' in recommendations.columns:
                result_cols.append('url')

            return recommendations.sort_values(
                sort_column,
                ascending=False
            )[result_cols].rename(columns={sort_column: 'score'})

        except Exception as e:
            logger.error(f"Error in recommendation: {str(e)}")
            return f"Error generating recommendations: {str(e)}"

    def recommend_as_dict(self, content_title: str, top_n: int = 5,
                        similarity_weight: float = 0.7, popularity_weight: float = 0.3) -> dict:
        """Get recommendations as a dictionary for API response."""
        result = self.recommend_content(
            content_title=content_title,
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
                "title": row['title'],
                "description": row['description'],
                "category_name": row['category_name'],
                "score": float(row['score'])
            }
            if 'url' in row:
                item['url'] = row['url']
            recommendations.append(item)
        
        return {
            "recommendations": recommendations,
            "query": content_title,
            "count": len(recommendations)
        }