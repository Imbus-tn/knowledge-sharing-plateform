import axios from "axios";

export const apiClient = axios.create({
    baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api',
    withCredentials: true,
    paramsSerializer: {
        encode: (param) => param // Preserve case sensitivity for enum values
      }
});