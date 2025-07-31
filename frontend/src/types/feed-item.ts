// src/types/post-response.ts

import type { User } from './user'

export interface AuthorDto {
  name: string
  initials?: string
  avatarUrl?: string
}

export interface FeedItem {
  id: number;
  title?: string;
  description?: string;
  coverImage?: string;
  authorId?: string;
  createdAt?: string;
  likes?: number;
  comments?: number;
  shares?: number;
  isFavorite?: boolean;
}