import type { User } from './user'
import type { Post } from './post'

export interface Reaction {
  id?: string;
  emoji: string; // Change this from 'type' to 'emoji'
  post?: Post;
  comment?: Comment;
  user?: User;
  createdAt?: string;
  count?: number;
  users?: string[];
}