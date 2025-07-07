import type { User } from './user'
import type { Post } from './post'

export interface Reaction {
  id?: string
  emoji: string
  post?: Post
  comment?: Comment
   user?: User;
  createdAt?: string
  count?: number
  users?: string[]
}