import type { User } from './user';
import type { UserRole } from './UserRole';
import type { Reaction } from './reaction';
// Main Post Interface
export interface Post {
  id: string;
  content: string;
  imageUrl?: string;
  author: User;
  createdAt: string; 
  updatedAt?: string; 
  title?: string; // Added for favorites compatibility
  description?: string; // Added for favorites compatibility
  likes?: number; // Added for favorites compatibility
  isFavorite?: boolean;
  
  // Relationships
  reactions: Reaction[]; 
  favorites: Favorite[]; 
  comments: Comment[]; 
  shares: Share[]; 
}

// Simplified Author interface for API responses
export interface PostAuthor {
  id: string;
  name: string;
  email: string;
  role: UserRole;
  avatarUrl?: string;
  initials?: string; // Added for UI display
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

// API Response Types
export interface PostsResponse {
  content: Post[];
  total: number;
  page: number;
  size: number;
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
  title?: string;
  description?: string;
}