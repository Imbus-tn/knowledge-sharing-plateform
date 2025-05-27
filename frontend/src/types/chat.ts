// User type (should match your backend User entity)
export interface User {
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  avatarUrl?: string;
  online: boolean;
  lastSeen?: string;
}

// Chat types
export interface Chat {
  id: number;
  name: string;
  isGroup: boolean;
  participants: User[];
  lastActivity: string;
  unreadCount: number;
  lastMessage?: Message;
}

export interface Message {
  id: number;
  text: string;
  sender: User;
  createdAt: string;
  isRead: boolean;
  isSent: boolean;
  isForwarded: boolean;
  replyTo?: Message;
  reactions: Reaction[];
  attachmentUrl?: string;
}

export interface Reaction {
  id: number;
  emoji: string;
  user: User;
  createdAt: string;
}

// DTOs for API requests
export interface CreateChatRequest {
  participantIds: number[];
  name?: string; // Required for group chats
}

export interface SendMessageRequest {
  text: string;
  replyToId?: number;
  attachmentUrl?: string;
}

export interface ReactionRequest {
  messageId: number;
  emoji: string;
}

export interface FileUploadResponse {
  url: string;
  fileName: string;
  fileType: string;
  size: number;
}