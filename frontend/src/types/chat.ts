
export interface User {
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  avatarUrl?: string;
  online: boolean;
  lastSeen?: string;
  initials?: string;  // For avatar fallback
  status?: 'online' | 'offline' | 'away' | 'busy';
}

export interface Chat {
  id: number;
  name: string;
  isGroup: boolean;
  participants: User[];
  lastActivity: string;
  unreadCount: number;
  lastMessage?: MessagePreview;
  messages: Message[];  // Add messages array
  createdAt: string;
  updatedAt: string;
}
export interface Message {
  id: number;
  text: string;
  sender: User;
  createdAt: string;
  isRead: boolean;
  isSent: boolean;
  isForwarded: boolean;
  chatId: number; // Add this property
  replyTo?: MessagePreview;
  reactions: Reaction[];
  attachment?: Attachment;
}

export interface Reaction {
  id: number;
  emoji: string;
  user: User;
  createdAt: string;
  messageId: number;
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
// For socket service and store types
export interface UserPresence {
  userId: number;
  isOnline: boolean;
  lastSeen?: string;
}

export interface MessagePreview {
  id: number;
  text: string;
  sender: User;
  createdAt: string;
}

export interface Attachment {
  type: 'IMAGE' | 'DOCUMENT' | 'AUDIO' | 'VIDEO';
  url: string;
  name: string;
  size: number;
}

// For the discussion store
export interface DiscussionState {
  chats: Chat[];
  activeChat: number | null;
  unreadCount: number;
  showDiscussionPanel: boolean;
  onlineUsers: Set<number>;
}

// For API responses
export interface ApiResponse<T> {
  data: T;
  message?: string;
  success: boolean;
}

// For paginated responses
export interface PaginatedResponse<T> {
  items: T[];
  total: number;
  page: number;
  limit: number;
}

// For file uploads
export interface FileUploadProgress {
  progress: number;
  loaded: number;
  total: number;
}
// Type guard for User
export function isUser(user: any): user is User {
  return user && typeof user.id === 'number' && typeof user.username === 'string';
}

// Type guard for Chat
export function isChat(chat: any): chat is Chat {
  return chat && typeof chat.id === 'number' && Array.isArray(chat.participants);
}

// Type guard for Message
export function isMessage(message: any): message is Message {
  return message && typeof message.id === 'number' && typeof message.text === 'string';
}
export enum ChatEventTypes {
  MESSAGE = 'MESSAGE',
  TYPING = 'TYPING',
  READ_RECEIPT = 'READ_RECEIPT',
  PRESENCE = 'PRESENCE'
}

export enum MessageStatus {
  SENT = 'SENT',
  DELIVERED = 'DELIVERED',
  READ = 'READ'
}