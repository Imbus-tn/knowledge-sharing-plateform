import axios from "axios";
import { useAuthStore } from '../stores/auth';
const API_BASE = import.meta.env.VITE_API_URL || 'http://localhost:8080';
export const apiClient = axios.create({
  baseURL: `${API_BASE}/api`,
  withCredentials: true,
  paramsSerializer: { encode: (param) => param }
});
apiClient.interceptors.request.use((config) => {
  const authStore = useAuthStore();
  const token = authStore.accessToken;

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});