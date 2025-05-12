import os
import numpy as np
import pandas as pd
from sklearn.model_selection import train_test_split
from tensorflow.keras.layers import Input, Embedding, Flatten, Concatenate, Dense
from tensorflow.keras.models import Model
from tensorflow.keras.optimizers import Adam
from tensorflow.keras.callbacks import EarlyStopping, ReduceLROnPlateau
import logging

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

def save_model(model, model_path="ncf_model.h5"):
    """Save the trained model to disk."""
    try:
        model.save(model_path, save_format='h5')
        logger.info(f"Model saved successfully to {model_path}")
        if os.path.exists(model_path):
            logger.info(f"Model file size: {os.path.getsize(model_path) / 1024:.2f} KB")
        else:
            logger.error("Model file was not created!")
    except Exception as e:
        logger.error(f"Failed to save model: {str(e)}")
        raise

def prepare_ncf_data(interactions_df, content_df):
    """Prepare data for NCF model training."""
    try:
        logger.info("Preparing NCF data...")
        
        # Process interactions
        interactions_full_df = interactions_df.groupby(['user_id', 'content_id'])['engagement_weight'].sum().reset_index()
        interactions_full_df['interaction'] = (interactions_full_df['engagement_weight'] > 0).astype(int)
        
        # Create mappings
        user_ids = interactions_full_df['user_id'].unique()
        content_ids = interactions_full_df['content_id'].unique()
        
        user_to_index = {user_id: idx for idx, user_id in enumerate(user_ids)}
        content_to_index = {content_id: idx for idx, content_id in enumerate(content_ids)}
        
        logger.info(f"Found {len(user_to_index)} users and {len(content_to_index)} content items")
        
        return (
            interactions_full_df['user_id'].map(user_to_index).values,
            interactions_full_df['content_id'].map(content_to_index).values,
            interactions_full_df['interaction'].values,
            len(user_to_index),
            len(content_to_index)
        )
    except Exception as e:
        logger.error(f"Error preparing NCF data: {str(e)}")
        raise

def build_ncf_model(num_users, num_items, embedding_size=64):
    """Build the Neural Collaborative Filtering model."""
    try:
        logger.info(f"Building NCF model for {num_users} users and {num_items} items")
        
        # User pathway
        user_input = Input(shape=(1,), name='user_input', dtype='int32')
        user_embedding = Embedding(
            input_dim=num_users, 
            output_dim=embedding_size,
            name='user_embedding',
            embeddings_initializer='glorot_uniform'
        )(user_input)
        user_vec = Flatten()(user_embedding)

        # Item pathway
        item_input = Input(shape=(1,), name='item_input', dtype='int32')
        item_embedding = Embedding(
            input_dim=num_items,
            output_dim=embedding_size,
            name='item_embedding',
            embeddings_initializer='glorot_uniform'
        )(item_input)
        item_vec = Flatten()(item_embedding)

        # Combined network
        concat = Concatenate()([user_vec, item_vec])
        fc_1 = Dense(128, activation='relu')(concat)
        fc_2 = Dense(64, activation='relu')(fc_1)
        output = Dense(1, activation='sigmoid', name='output')(fc_2)

        model = Model(inputs=[user_input, item_input], outputs=output)
        model.compile(
            optimizer=Adam(learning_rate=0.001),
            loss='binary_crossentropy',
            metrics=['accuracy']
        )
        
        logger.info("Model built successfully")
        return model
    except Exception as e:
        logger.error(f"Error building model: {str(e)}")
        raise

def load_and_preprocess_data(data_dir):
    """Load and preprocess data from CSV files."""
    try:
        logger.info(f"Loading data from {data_dir}")
        
        # Required files
        required_files = ["content.csv", "Engagement.csv", "Favorites.csv", "Vote.csv"]
        for file in required_files:
            if not os.path.exists(os.path.join(data_dir, file)):
                raise FileNotFoundError(f"Missing required file: {file}")
        
        # Load data
        content_df = pd.read_csv(os.path.join(data_dir, "content.csv"))
        engagement_df = pd.read_csv(os.path.join(data_dir, "Engagement.csv"))
        favorites_df = pd.read_csv(os.path.join(data_dir, "Favorites.csv"))
        votes_df = pd.read_csv(os.path.join(data_dir, "Vote.csv"))
        
        logger.info("Data loaded successfully")
        
        # Process engagement data
        engagement_df['engagement_weight'] = engagement_df['type_engagement'].map({
            'view': 1.0, 'like': 2.0, 'bookmark': 3.0,
            'comment': 4.0, 'share': 5.0, 'report': -2.0
        })
        
        # Process favorites
        favorites_df['engagement_weight'] = 4.0
        favorites_df['type_engagement'] = 'favorite'
        
        # Process votes
        votes_df['engagement_weight'] = votes_df['vote_type'].map({'Like': 2.0, 'Dislike': -1.0})
        votes_df['type_engagement'] = 'vote'
        
        # Combine interactions
        interactions_df = pd.concat([
            engagement_df[['user_id', 'content_id', 'engagement_weight']],
            favorites_df[['user_id', 'content_id', 'engagement_weight']],
            votes_df[['user_id', 'content_id', 'engagement_weight']]
        ])
        
        logger.info(f"Total interactions: {len(interactions_df)}")
        return content_df, interactions_df
        
    except Exception as e:
        logger.error(f"Error loading data: {str(e)}")
        raise

def main():
    try:
        logger.info("Starting NCF model training pipeline")
        
        # 1. Load data
        data_dir = "data"
        content_df, interactions_df = load_and_preprocess_data(data_dir)
        
        # 2. Prepare NCF data
        user_indices, item_indices, labels, num_users, num_items = prepare_ncf_data(interactions_df, content_df)
        
        # 3. Split data
        (X_train_user, X_val_user, 
         X_train_item, X_val_item, 
         y_train, y_val) = train_test_split(
            user_indices, item_indices, labels, 
            test_size=0.2, random_state=42
        )
        logger.info(f"Train samples: {len(X_train_user)}, Validation samples: {len(X_val_user)}")
        
        # 4. Build and train model
        model = build_ncf_model(num_users, num_items)
        
        logger.info("Starting model training...")
        history = model.fit(
            [X_train_user, X_train_item],
            y_train,
            validation_data=([X_val_user, X_val_item], y_val),
            epochs=10,
            batch_size=256,
            callbacks=[
                EarlyStopping(monitor='val_loss', patience=3, restore_best_weights=True),
                ReduceLROnPlateau(monitor='val_loss', factor=0.5, patience=2, min_lr=1e-5)
            ],
            verbose=1
        )
        
        # 5. Save model
        save_model(model)
        
        # Log results
        val_accuracy = history.history['val_accuracy'][-1]
        logger.info(f"Training complete! Final validation accuracy: {val_accuracy:.4f}")
        
    except Exception as e:
        logger.error(f"Fatal error in training pipeline: {str(e)}")
        raise

if __name__ == "__main__":
    os.environ['TF_CPP_MIN_LOG_LEVEL'] = '0'  # Show all TensorFlow logs
    main()