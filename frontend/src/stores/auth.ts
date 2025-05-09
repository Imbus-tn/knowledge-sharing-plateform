import { defineStore } from 'pinia';
import { apiClient } from '../api';
import type { User } from '../types/user';
import type { AuthResponse } from '../types/auth';
import { ref, onBeforeMount, computed } from 'vue';
import { useRouter } from 'vue-router';
import { UserRole } from '../types/UserRole';


export const useAuthStore = defineStore('auth', () => {
  const router = useRouter();
  const user =ref<User | null>(null);
  const accessToken = ref<string | null>(null);
  const refreshToken = ref<string | null>(null);
  const refreshTokenTimeout = ref<number | null>(null); 

  // Initialize from localStorage on app load
  const initAuth = async() => {
    let storedAccessToken = localStorage.getItem('access_token');
    let storedRefreshToken = localStorage.getItem('refresh_token');

    // If not found in localStorage, check sessionStorage
    if (!storedAccessToken || !storedRefreshToken) {
      storedAccessToken = sessionStorage.getItem('access_token');
      storedRefreshToken = sessionStorage.getItem('refresh_token');
    }

    if(storedAccessToken && storedRefreshToken){
      accessToken.value = storedAccessToken;
      refreshToken.value = storedRefreshToken;
      apiClient.defaults.headers.common.Authorization = `Bearer ${storedAccessToken}`;

      try{
        await fetchUser();
        scheduletokenRefresh();
      }catch {
        logout();
      }
    }    
  };


  // Login with remember me functionality
  const login = async (email: string, password: string, rememberMe: boolean): Promise<void> => {
    try{
      const { data } = await apiClient.post<AuthResponse>('/auth/login',{ email, password });

      // Choose storage based on rememberMe
      const storage = rememberMe ? localStorage : sessionStorage;
      storage.setItem('access_token',data.accessToken);
      storage.setItem('refresh_token', data.refreshToken);

      accessToken.value = data.accessToken;
      refreshToken.value = data.refreshToken;
      apiClient.defaults.headers.common.Authorization = `Bearer ${data.accessToken}`;

      // fetch user data after successful login
      await fetchUser();
      scheduletokenRefresh();
    }catch(error){
      throw error;
    }

  };

  const refreshAccessToken = async () => {
    try{
      const { data } = await apiClient.post<AuthResponse>('/auth/refresh',{
        refreshToken: refreshToken.value
      });

      accessToken.value = data.accessToken;
      refreshToken.value = data.refreshToken;
      apiClient.defaults.headers.common.Authorization = `Bearer ${data.accessToken}`;

      sessionStorage.setItem('access_token', data.accessToken);
      sessionStorage.setItem('refresh_token', data.refreshToken);

      scheduletokenRefresh();
    }catch(error){
      logout();
      throw error;
    }
  };

  // Schedule token refresh based on JWT expiration
  const scheduletokenRefresh = () => {
    if (!accessToken.value) return;

    const tokenPayload = JSON.parse(atob(accessToken.value.split('.')[1]));
    const expiresIn = tokenPayload.exp * 1000 - Date.now() - 60000; // 1 minute buffer

    if (expiresIn > 0){
      refreshTokenTimeout.value = setTimeout(() => {
        refreshAccessToken().catch(() => logout());
      }, expiresIn) as unknown as number;
    }
  };

    // Logout method
    const logout = async (): Promise<void> => {
      // Clear tokens
      sessionStorage.removeItem('access_token');
      sessionStorage.removeItem('refresh_token');
      localStorage.removeItem('access_token');
      localStorage.removeItem('refresh_token');

      accessToken.value = null;
      refreshToken.value = null;
      // Reset user state
      user.value = null;
  
      // Remove Authorization header from API client
      delete apiClient.defaults.headers.common.Authorization;
  
      // Clear any pending refresh timeouts
      if(refreshTokenTimeout.value){
        clearTimeout(refreshTokenTimeout.value);
      }
  
      router.push('/login');
    };


  // Fetch current user data
  const fetchUser = async (): Promise<void> => {
    try {
      const { data } = await apiClient.get<User>('/auth/me');
      user.value = data;
    }catch (error){
      logout();
      throw error;
    }
  };

  // check authentication status
  const checkAuth = async (): Promise<boolean> => {
    if(!accessToken.value) return false;

    try{
      await fetchUser();
      return true;
    }catch{
      return false;
    }
  };

   // Initialize authentication on store creation
   initAuth();

   // Cleanup on component unmount
   onBeforeMount(() => {
    if (refreshTokenTimeout.value){
      clearTimeout(refreshTokenTimeout.value);
    }
   });

   const isAdmin = computed(() => {
    return user.value?.role === UserRole.ADMIN;
   });

   const register = async(token: string, registerData:{
    name: string,
    password: string,
    repeatPassword: string,
   }):Promise<void> => {
    try{
      await apiClient.post<AuthResponse>('/auth/register' , registerData, {
        params: {token} // Send token as query parameter
      });
    }catch (error){
      throw error;
    }
   };

   const validateInvitation = async ( token: string) => {
    try {
      const response = await apiClient.get('/auth/invitation/validate', { params: { token } });
      return response.data;
    }catch(error) {
      throw error;
    }
   };

   const isAuthenticated = computed(() => !!user.value);

   const updateProfile = async (profileData: any): Promise<void> => {
    try {
      const userId = user.value?.id;
      if (!userId) throw new Error('User ID not found');
  
      // Step 1: Update profile data
      await apiClient.put(`/users/${userId}/profile`, profileData, {
        headers: { 'Content-Type': 'application/json' },
      });
  
      // Step 2: Fetch updated user data
      await fetchUser();
    } catch (error) {
      throw error;
    }
  };

  // Upload avatar
  const uploadAvatar = async (file: File): Promise<void> => {
    try {
      const userId = user.value?.id;
      if (!userId) throw new Error('User ID not found');

      const formData = new FormData();
      formData.append('file', file); // Key must match the backend's @RequestParam("file")

      await apiClient.post(`/users/${userId}/avatar`, formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      });

      // Fetch updated user data
      await fetchUser();
    } catch (error) {
      throw error;
    }
  };

  return {
    user,
    accessToken, 
    login,
    register, 
    validateInvitation,
    checkAuth, 
    logout,
    refreshAccessToken,
    updateProfile,
    uploadAvatar,
    fetchUser,
    isAdmin,
    isAuthenticated
  }
});