export interface FavoriteItem {
  id: number;
  type: string;
   title: string;    
  description: string;
   category?: string;   
  imageUrl?: string;       
  author: {
    name: string;
    initials: string;
    role?: string;
    avatarUrl?: string;
  };
  createdAt: string;
  likeCount: number;        
  commentCount: number;     
  shareCount: number;       
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