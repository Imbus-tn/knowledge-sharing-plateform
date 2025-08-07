import axios from 'axios';
import { getSocketService } from '../services/socket.service';
import { useAuthStore } from '../stores/auth'; // adjust path if needed
import { apiClient } from '../api';
import type {
  Chat,
  Message,
  Reaction,
  CreateChatRequest,
  SendMessageRequest,
  ReactionRequest,
  FileUploadResponse
} from '../types/chat';

const socketService = getSocketService();

// Use a single base URL for both REST and WebSocket fallback
const API_BASE_URL = import.meta.env.VITE_API_URL + '/api' || 'http://localhost:8080/api';



export const chatApi = {
  // Chats
async getAllChats(userId: number): Promise<Chat[]> {
  const response = await apiClient.get('/chats', {
    params: { userId }
  });
  return response.data;
},


  async getChatById(chatId: number): Promise<Chat> {
    const response = await apiClient.get(`/chats/${chatId}`);
    return response.data;
  },

  async createGroupChat(request: CreateChatRequest): Promise<Chat> {
    const response = await apiClient.post('/chats/group', request);
    return response.data;
  },

  async deleteChat(chatId: number): Promise<void> {
    await apiClient.delete(`/chats/${chatId}`);
  },

  // Messages
  async getChatMessages(chatId: number): Promise<Message[]> {
    const response = await apiClient.get(`/chats/${chatId}/messages`);
    return response.data;
  },

  async sendMessage(chatId: number, request: SendMessageRequest): Promise<void> {
    try {
      await socketService.send(`/app/chat/${chatId}/send`, request);
    } catch (error) {
      await apiClient.post(`/chats/${chatId}/messages`, request);
    }
  },

  async deleteMessage(chatId: number, messageId: number): Promise<void> {
    await apiClient.delete(`/chats/${chatId}/messages/${messageId}`);
  },

  // Reactions
  async toggleReaction(chatId: number, request: ReactionRequest): Promise<void> {
    try {
      await socketService.send(`/app/chat/${chatId}/react`, request);
    } catch (error) {
      await apiClient.post(`/chats/${chatId}/reactions`, request);
    }
  },

  // File Upload
  async uploadFile(chatId: number, file: File): Promise<FileUploadResponse> {
    const formData = new FormData();
    formData.append('file', file);

    const response = await apiClient.post(`/files`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });

    return response.data;
  },

  // Mark Messages as Read
  async markAsRead(chatId: number): Promise<void> {
    await apiClient.post(`/chats/${chatId}/read`);
  }
};
