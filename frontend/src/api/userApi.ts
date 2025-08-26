// src/api/userApi.ts
import { apiClient } from './index';
import type { User } from '../types/user';

export const userApi = {
  async searchUsers(query: string): Promise<User[]> {
    if (query.length < 2) return [];

    try {
      const response = await apiClient.get('/users/search', {
        params: { q: query }
      });
      return response.data || [];
    } catch (error: any) {
      console.error('Search failed:', error.response?.data || error.message);
      return [];
    }
  }
};