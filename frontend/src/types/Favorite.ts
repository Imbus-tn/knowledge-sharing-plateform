export interface FavoriteItem {
  id: number;
  title: string;
  description: string;
  coverImage?: string;
  author: {
    name: string;
    initials: string;
    role?: string;
    avatarUrl?: string;
  };
  createdAt: string;
  likes: number;
  comments: number;
  shares: number;
  isFavorite?: boolean;
}

export interface FavoritesState {
  items: FavoriteItem[];
  loading: boolean;
  error: string | null;
}

export interface FavoriteResponse {
  success: boolean;
  message?: string;
  data?: FavoriteItem | FavoriteItem[];
}

export interface ToggleFavoritePayload {
  postId: string;
  isFavorite: boolean;
}