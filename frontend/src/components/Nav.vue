<template>
  <nav class="bg-slate-800/50 backdrop-blur-sm border-b border-slate-700 sticky top-0 z-50">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex items-center h-16">
        <!-- Left Section - Logo -->
        <div class="flex-shrink-0">
          <router-link to="/" class="flex items-center">
            <BookOpen class="w-8 h-8 text-emerald-500" />
            <span class="ml-2 text-xl font-bold text-white">Imbus Knowledge</span>
          </router-link>
        </div>

        <!-- Center Section - Search -->
        <div class="flex-1 flex items-center justify-center px-8">
          <div class="relative w-full max-w-lg">
            <Search class="absolute left-3 top-1/2 transform -translate-y-1/2 text-slate-400" />
            <input
              type="text"
              placeholder="Search..."
              class="w-full pl-10 pr-4 py-2 bg-slate-700/50 border border-slate-600 rounded-lg text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-emerald-500"
            />
          </div>
        </div>

        <!-- Right Section -->
        <div class="flex items-center space-x-2">
          <!-- Feed Icon -->
          <router-link
            to="/feed"
            class="relative p-2 text-slate-300 hover:text-white hover:bg-slate-700/50 rounded-lg transition-colors"
            :class="{ 'text-white bg-slate-700/50': isActivePage('feed') }"
          >
            <Home class="w-6 h-6" />
          </router-link>

          <!-- Notifications with Badge -->
          <router-link
            to="/notifications"
            class="relative p-2 text-slate-300 hover:text-white hover:bg-slate-700/50 rounded-lg transition-colors"
          >
            <Bell class="w-6 h-6" />
            <div v-if="unreadNotifications" class="absolute -top-1 -right-1">
              <span class="animate-ping absolute h-4 w-4 rounded-full bg-red-400 opacity-75"></span>
              <span class="relative bg-red-500 text-white text-xs w-4 h-4 flex items-center justify-center rounded-full">
                {{ unreadNotifications > 9 ? '9+' : unreadNotifications }}
              </span>
            </div>
          </router-link>

          <!-- Discussions with Badge -->
          <router-link
            to="/discussions"
            class="relative p-2 text-slate-300 hover:text-white hover:bg-slate-700/50 rounded-lg transition-colors"
          >
            <MessageSquare class="w-6 h-6" />
            <div v-if="unreadMessages" class="absolute -top-1 -right-1">
              <span class="animate-ping absolute h-4 w-4 rounded-full bg-emerald-400 opacity-75"></span>
              <span class="relative bg-emerald-500 text-white text-xs w-4 h-4 flex items-center justify-center rounded-full">
                {{ unreadMessages > 9 ? '9+' : unreadMessages }}
              </span>
            </div>
          </router-link>

          <div class="w-px h-8 bg-slate-700 mx-2"></div>

          <!-- Original Profile Dropdown -->
          <div class="relative profile-menu">
            <button @click.stop="toggleProfileMenu" class="flex items-center space-x-3">
              <div v-if="avatarUrl" class="w-8 h-8 rounded-full overflow-hidden">
                <img :src="avatarUrl" alt="User Avatar" class="w-full h-full object-cover">
              </div>
              <div v-else class="w-8 h-8 rounded-full bg-emerald-500 flex items-center justify-center">
                <span class="text-white font-medium">{{ userInitials }}</span>
              </div>
            </button>

            <div v-if="showProfileMenu" 
              ref="profileDropdown"
              class="absolute right-0 mt-2 w-64 rounded-lg shadow-lg bg-slate-800 ring-1 ring-black ring-opacity-5 z-50 overflow-hidden">
              
              <div class="p-4 border-b border-slate-700 flex flex-col items-center">
                <div v-if="avatarUrl" class="w-16 h-16 rounded-full overflow-hidden mb-2">
                  <img :src="avatarUrl" alt="User Avatar" class="w-full h-full object-cover">
                </div>
                <div v-else class="w-16 h-16 rounded-full bg-emerald-500 flex items-center justify-center mb-2">
                  <span class="text-white text-xl font-medium">{{ userInitials }}</span>
                </div>
                <div class="text-center">
                  <h3 class="text-white font-medium">{{ authStore.user?.name }}</h3>
                  <p class="text-slate-400 text-sm">{{ authStore.user?.email }}</p>
                  <div v-if="user" class="mt-1 inline-flex items-center px-2 py-0.5 rounded-full text-xs"
                    :class="roleStyle(user?.role)">
                    {{ user?.role.toLowerCase() }}
                  </div>
                </div>
              </div>

              <div class="py-2">
                <router-link to="/dashboard" 
                  class="flex items-center px-4 py-3 text-sm text-slate-300 hover:bg-slate-700 hover:text-white transition-colors"
                  @click="showProfileMenu = false">
                  <LayoutDashboard class="w-5 h-5 mr-3 text-slate-400" />
                  Your dashboard
                </router-link>
                <router-link to="/profile" 
                  class="flex items-center px-4 py-3 text-sm text-slate-300 hover:bg-slate-700 hover:text-white transition-colors"
                  @click="showProfileMenu = false">
                  <User class="w-5 h-5 mr-3 text-slate-400" />
                  Your profile
                </router-link>
                <router-link 
                  v-if="authStore.isAdmin"
                  to="/invite-user" 
                  class="flex items-center px-4 py-3 text-sm text-slate-300 hover:bg-slate-700 hover:text-white transition-colors"
                  @click="showProfileMenu = false">
                  <UserPlus class="w-5 h-5 mr-3 text-slate-400" />
                  Invite User
                </router-link>
                <router-link 
                  v-if="authStore.isAdmin"
                  to="/manage-users" 
                  class="flex items-center px-4 py-3 text-sm text-slate-300 hover:bg-slate-700 hover:text-white transition-colors"
                  @click="showProfileMenu = false"
                >
                  <Users class="w-5 h-5 mr-3 text-slate-400" /> 
                  Manage Users
                </router-link>
              </div>

              <div class="border-t border-slate-700 py-2">
                <button @click="handleLogout" 
                  class="flex items-center w-full px-4 py-3 text-sm text-slate-300 hover:bg-slate-700 hover:text-white transition-colors">
                  <LogOut class="w-5 h-5 mr-3 text-slate-400" />
                  Sign out
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </nav>
</template>

<script setup lang="ts">
import { computed, ref, onMounted, onUnmounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { 
  BookOpen, Search, Home, Bell, MessageSquare, 
  User, LayoutDashboard, UserPlus, Users, LogOut 
} from 'lucide-vue-next';
import { useAuthStore } from '../stores/auth';
import type { UserRole } from '../types/UserRole';

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();
const showProfileMenu = ref(false);
const profileDropdown = ref<HTMLElement | null>(null);
const user = computed(() => authStore.user);

const roleStyle = (role: UserRole) => {
  return {
    'bg-emerald-500/10 text-emerald-500': role === 'ADMIN',
    'bg-orange-500/10 text-orange-500': role === 'CONTRIBUTOR',
    'bg-blue-500/10 text-blue-500': role === 'USER'
  };
};

const avatarUrl = computed(() => {
  const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080';
  return authStore.user?.avatarUrl 
    ? `${apiUrl}${authStore.user.avatarUrl}`
    : '';
});

const userInitials = computed(() => {
  return authStore.user?.name
    ? authStore.user.name.split(' ').map(n => n[0]).join('').toUpperCase()
    : '';
});

const unreadNotifications = computed(() => authStore.unreadNotifications);
const unreadMessages = computed(() => authStore.unreadMessages);

const isActivePage = (page: string) => {
  return route.path === `/${page}`;
};

const toggleProfileMenu = () => {
  showProfileMenu.value = !showProfileMenu.value;
};

const handleLogout = () => {
  authStore.logout();
  router.push('/login');
  showProfileMenu.value = false;
};

const handleClickOutside = (event: MouseEvent) => {
  if (
    profileDropdown.value &&
    !profileDropdown.value.contains(event.target as Node)
  ) {
    showProfileMenu.value = false;
  }
};

onMounted(() => document.addEventListener('click', handleClickOutside));
onUnmounted(() => document.removeEventListener('click', handleClickOutside));
</script>

<style scoped>
input[type="text"] {
  @apply focus:ring-2 focus:ring-emerald-500;
}
</style>