import type { User } from './user';

// Main Post Interface
export interface Post {
  id: string;
  content: string;
  imageUrl?: string;
  author: User;
  createdAt: string; 
  updatedAt?: string; 
  
  // Relationships
  reactions: Reaction[]; 
  favorites: Favorite[]; 
  isFavorite?: boolean;
  comments: Comment[]; 
  shares: Share[]; 
}

// Reaction Interface
export interface Reaction {
  id?: string;
  type: string; 
  post?: Post; 
  comment?: Comment; 
  user: User; 
  createdAt: string; 
}

// Comment Interface
export interface Comment {
  id: string;
  text: string;
  post: Post;
  author: User;
  parent?: Comment; 
  createdAt: string; 
  replies?: Comment[];
  reactions?: Reaction[];
}

// Favorite Interface
export interface Favorite {
  id: string;
  post: Post;
  user: User;
  createdAt: string; 
}

// Share Interface
export interface Share {
  id: string;
  user: User;
  post: Post;
  sharedAt: string; 
}

// Report Interface
export interface ReportedPost {
  id: string;
  post: Post;
  reporter: User;
  reason: string;
  reportedAt: string; 
}

// Request Interfaces
export interface ReactionRequest {
  type: string; 
}

export interface CommentRequest {
  text: string;
}

export interface ReportRequest {
  reason: string;
}

export interface CreatePostRequest {
  content: string;
  imageUrl?: string;
}