import axios from 'axios';
// In api/chat.ts and stores/chat.ts:
import { socketService } from '../services/socket.service'
import type {
  Chat,
  Message,
  Reaction,
  CreateChatRequest,
  SendMessageRequest,
  ReactionRequest,
  FileUploadResponse
} from '../types/chat';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'

export const chatApi = {
  // CRUD for Chats
  async getAllChats(): Promise<Chat[]> {
    const response = await axios.get(`${API_BASE_URL}/chats`);
    return response.data;
  },

  async getChatById(chatId: number): Promise<Chat> {
    const response = await axios.get(`${API_BASE_URL}/chats/${chatId}`);
    return response.data;
  },

  async createGroupChat(request: CreateChatRequest): Promise<Chat> {
    const response = await axios.post(`${API_BASE_URL}/chats/group`, request);
    return response.data;
  },

  async deleteChat(chatId: number): Promise<void> {
    await axios.delete(`${API_BASE_URL}/chats/${chatId}`);
  },

  // Messages
  async getChatMessages(chatId: number): Promise<Message[]> {
    const response = await axios.get(`${API_BASE_URL}/chats/${chatId}/messages`);
    return response.data;
  },

  async sendMessage(chatId: number, request: SendMessageRequest): Promise<void> {
    // For real-time, we use WebSocket but fallback to HTTP if needed
    try {
      await socketService.send(`/app/chat/${chatId}/send`, request);
    } catch (e) {
      // Fallback to HTTP if WebSocket fails
      await axios.post(`${API_BASE_URL}/chats/${chatId}/messages`, request);
    }
  },

  async deleteMessage(chatId: number, messageId: number): Promise<void> {
    await axios.delete(`${API_BASE_URL}/chats/${chatId}/messages/${messageId}`);
  },

  // Reactions
  async toggleReaction(chatId: number, request: ReactionRequest): Promise<void> {
    try {
      await socketService.send(`/app/chat/${chatId}/react`, request);
    } catch (e) {
      await axios.post(`${API_BASE_URL}/chats/${chatId}/reactions`, request);
    }
  },

  // Files
  async uploadFile(chatId: number, file: File): Promise<FileUploadResponse> {
    const formData = new FormData();
    formData.append('file', file);
    
    const response = await axios.post(`${API_BASE_URL}/files`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });
    return response.data;
  },

  // Mark messages as read
  async markAsRead(chatId: number): Promise<void> {
    await axios.post(`${API_BASE_URL}/chats/${chatId}/read`);
  }
};