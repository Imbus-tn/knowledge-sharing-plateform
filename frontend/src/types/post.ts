import type { User } from './user';
import  { UserRole } from './UserRole';
import type{Reaction} from './reaction';



// Main Post Interface
export interface Post {
  id: number; // Changed to number to match backend Long
  content: string;
  imageUrl?: string;
  author: PostAuthor;
  createdAt: string;
  updatedAt?: string;
  title?: string;
  description?: string;
  likes?: number;
  isFavorite?: boolean;
  tags: string[];
  reactions: Reaction[];
  favorites: Favorite[];
  comments: Comment[];
  shares: Share[];
    category?: string;
  viewCount: number;
  likeCount: number;
  shareCount: number;
  
}

// Simplified Author interface for API responses
export interface PostAuthor {
  id: number; // Changed to number
  name: string;
  email: string;
  role: UserRole;
  avatarUrl?: string;
  initials?: string;
}

// Comment Interface
export interface Comment {
  id: number; // Changed to number
  text: string;
  post?: Post; // Make optional!: Post;
  author: User;
  parent?: Comment;
  createdAt: string;
  replies?: Comment[];
  reactions?: Reaction[];
}

// Favorite Interface
export interface Favorite {
  id: number; // Changed to number
  post: Post;
  user: User;
  createdAt: string;
}

// Share Interface
export interface Share {
  id: number; // Changed to number
  user: User;
  post: Post;
  sharedAt: string;
}

// Report Interface
export interface ReportedPost {
  id: number; // Changed to number
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

