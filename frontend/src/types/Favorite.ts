// src/types/favorite.ts

export interface FavoriteItem {
  id: string;
  title: string;
  description: string;
  coverImage?: string;
  authorId: string;
  createdAt: string;
  likes: number;
  comments: number;
  shares: number;
  isFavorite?: boolean;
  // Add any other properties you need for favorite items
}

export interface FavoritesState {
  items: FavoriteItem[];
  loading: boolean;
  error: string | null;
}

// If you need additional types for the API responses:
export interface FavoriteResponse {
  success: boolean;
  message?: string;
  data?: FavoriteItem | FavoriteItem[];
}

// For the toggle favorite action payload
export interface ToggleFavoritePayload {
  postId: string;
  isFavorite: boolean;
}