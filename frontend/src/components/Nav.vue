<template>
  <nav :class="[
    'backdrop-blur-sm sticky top-0 z-50 border-b transition-colors duration-200',
    isDark 
      ? 'bg-slate-800/50 border-slate-700' 
      : 'bg-white/50 border-slate-200'
  ]">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex items-center h-16">
        <!-- Left Section - Logo -->
        <div class="flex-shrink-0">
          <router-link to="/" class="flex items-center">
            <BookOpen :class="isDark ? 'text-emerald-500' : 'text-emerald-600'" class="w-8 h-8" />
            <span :class="isDark ? 'text-white' : 'text-slate-900'" class="ml-2 text-xl font-bold">Imbus Knowledge</span>
          </router-link>
        </div>
        <!-- Center Section - Search -->
        <div class="flex-1 flex items-center justify-center px-8" :class="{ 'opacity-0 pointer-events-none': !isActivePage('feed') }">
          <div class="relative w-full max-w-lg">
            <Search :class="isDark ? 'text-slate-400' : 'text-slate-500'" class="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5" />
            <input
              type="text"
              placeholder="Search..."
              :class="[
                isDark 
                  ? 'bg-slate-700/50 border-slate-600 text-white placeholder-slate-400 focus:ring-emerald-500' 
                  : 'bg-slate-100/50 border-slate-200 text-slate-900 placeholder-slate-500 focus:ring-emerald-600',
                'w-full pl-10 pr-4 py-2 border rounded-lg focus:outline-none focus:ring-2 transition-colors'
              ]"
            />
          </div>
        </div>
        <!-- Right Section -->
        <div class="flex items-center space-x-2">
          <!-- Feed Icon -->
          <router-link
            to="/feed"
            :class="[
              'relative p-2 rounded-lg transition-colors',
              isDark 
                ? 'text-slate-300 hover:text-white hover:bg-slate-700/50' 
                : 'text-slate-600 hover:text-slate-900 hover:bg-slate-200/50',
              isActivePage('feed') && (isDark ? 'text-white bg-slate-700/50' : 'text-slate-900 bg-slate-200/50')
            ]"
          >
            <Home class="w-6 h-6" />
          </router-link>
          <!-- Notifications -->
          <router-link
            to="/notifications"
            :class="[
              'relative p-2 rounded-lg transition-colors',
              isDark 
                ? 'text-slate-300 hover:text-white hover:bg-slate-700/50' 
                : 'text-slate-600 hover:text-slate-900 hover:bg-slate-200/50',
              isActivePage('notifications') && (isDark ? 'text-white bg-slate-700/50' : 'text-slate-900 bg-slate-200/50')
            ]"
          >
            <Bell class="w-6 h-6" />
            <div v-if="unreadNotifications" class="absolute -top-1 -right-1">
              <span class="animate-ping absolute h-4 w-4 rounded-full bg-red-400 opacity-75"></span>
              <span class="relative bg-red-500 text-white text-xs w-4 h-4 flex items-center justify-center rounded-full">
                {{ unreadNotifications > 9 ? '9+' : unreadNotifications }}
              </span>
            </div>
          </router-link>
          <!-- Discussions -->
          <router-link
            to="/discussions"
            :class="[
              'relative p-2 rounded-lg transition-colors',
              isDark 
                ? 'text-slate-300 hover:text-white hover:bg-slate-700/50' 
                : 'text-slate-600 hover:text-slate-900 hover:bg-slate-200/50',
              isActivePage('discussions') && (isDark ? 'text-white bg-slate-700/50' : 'text-slate-900 bg-slate-200/50')
            ]"
          >
            <MessageSquare class="w-6 h-6" />
            <div v-if="unreadMessages" class="absolute -top-1 -right-1">
              <span class="animate-ping absolute h-4 w-4 rounded-full bg-emerald-400 opacity-75"></span>
              <span class="relative bg-emerald-500 text-white text-xs w-4 h-4 flex items-center justify-center rounded-full">
                {{ unreadMessages > 9 ? '9+' : unreadMessages }}
              </span>
            </div>
          </router-link>
          <div :class="isDark ? 'bg-slate-700' : 'bg-slate-200'" class="w-px h-8 mx-2"></div>
          <!-- Profile Dropdown -->
          <div class="relative profile-menu">
            <button @click.stop="toggleProfileMenu" class="flex items-center space-x-3">
              <div v-if="avatarUrl" class="w-8 h-8 rounded-full overflow-hidden">
                <img :src="avatarUrl" alt="User Avatar" class="w-full h-full object-cover" />
              </div>
              <div v-else class="w-8 h-8 rounded-full bg-emerald-500 flex items-center justify-center">
                <span class="text-white font-medium">{{ userInitials }}</span>
              </div>
            </button>
            <div v-if="showProfileMenu" ref="profileDropdown"
              :class="[
                'absolute right-0 mt-2 w-64 rounded-lg shadow-lg ring-1 ring-black ring-opacity-5 z-50 overflow-hidden',
                isDark ? 'bg-slate-800' : 'bg-white'
              ]">
              <div :class="isDark ? 'border-slate-700' : 'border-slate-200'" class="p-4 border-b flex flex-col items-center">
              <!-- Avatar -->
              <div class="relative mb-4 w-16 h-16 rounded-full overflow-hidden">
                <img 
                  v-if="avatarUrl" 
                  :src="avatarUrl" 
                  alt="User Avatar"
                  class="w-full h-full object-cover"
                />
                <div 
                  v-else 
                  class="absolute inset-0 bg-emerald-500 flex items-center justify-center"
                >
                  <span class="text-white text-xl font-bold">{{ userInitials }}</span>
                </div>
              </div>

              <!-- Name & Email -->
              <div class="text-center">
                <h3 class="text-white font-medium mb-1">{{ authStore.user?.name }}</h3>
                <p class="text-slate-400 text-sm mb-2">{{ authStore.user?.email }}</p>
              </div>

              <!-- Role Badge -->
              <div v-if="user" class="inline-flex items-center px-2 py-0.5 rounded-full text-xs"
                :class="roleStyle(user?.role)">
                {{ user?.role.toLowerCase() }}
              </div>
            </div>
              <div class="py-2">
                <router-link to="/dashboard" 
                  :class="[
                    'flex items-center px-4 py-3 text-sm transition-colors',
                    isDark 
                      ? 'text-slate-300 hover:bg-slate-700 hover:text-white' 
                      : 'text-slate-600 hover:bg-slate-100 hover:text-slate-900'
                  ]"
                  @click="showProfileMenu = false">
                  <LayoutDashboard :class="isDark ? 'text-slate-400' : 'text-slate-500'" class="w-5 h-5 mr-3" />
                  Your dashboard
                </router-link>
                <router-link to="/profile" 
                  :class="[
                    'flex items-center px-4 py-3 text-sm transition-colors',
                    isDark 
                      ? 'text-slate-300 hover:bg-slate-700 hover:text-white' 
                      : 'text-slate-600 hover:bg-slate-100 hover:text-slate-900'
                  ]"
                  @click="showProfileMenu = false">
                  <User :class="isDark ? 'text-slate-400' : 'text-slate-500'" class="w-5 h-5 mr-3" />
                  Your profile
                </router-link>

                <!-- Theme Toggle -->
                <button 
                  @click="toggleTheme"
                  class="flex items-center px-4 py-3 text-sm w-full text-left"
                  :class="[
                    isDark 
                      ? 'text-slate-300 hover:bg-slate-700 hover:text-white' 
                      : 'text-slate-600 hover:bg-slate-100 hover:text-slate-900'
                  ]"
                >
                  <component :is="isDark ? Sun : Moon" class="w-5 h-5 mr-3 text-slate-400" />
                  {{ isDark ? 'Light Mode' : 'Dark Mode' }}
                </button>

                <router-link v-if="authStore.isAdmin" to="/invite-user" 
                  :class="[
                    'flex items-center px-4 py-3 text-sm transition-colors',
                    isDark 
                      ? 'text-slate-300 hover:bg-slate-700 hover:text-white' 
                      : 'text-slate-600 hover:bg-slate-100 hover:text-slate-900'
                  ]"
                  @click="showProfileMenu = false">
                  <UserPlus :class="isDark ? 'text-slate-400' : 'text-slate-500'" class="w-5 h-5 mr-3" />
                  Invite User
                </router-link>
                <router-link v-if="authStore.isAdmin" to="/manage-users" 
                  :class="[
                    'flex items-center px-4 py-3 text-sm transition-colors',
                    isDark 
                      ? 'text-slate-300 hover:bg-slate-700 hover:text-white' 
                      : 'text-slate-600 hover:bg-slate-100 hover:text-slate-900'
                  ]"
                  @click="showProfileMenu = false">
                  <Users :class="isDark ? 'text-slate-400' : 'text-slate-500'" class="w-5 h-5 mr-3" />
                  Manage Users
                </router-link>
              </div>
              <div :class="isDark ? 'border-slate-700' : 'border-slate-200'" class="border-t py-2">
                <button @click="handleLogout" 
                  :class="[
                    'flex items-center w-full px-4 py-3 text-sm transition-colors',
                    isDark ? 'text-slate-300 hover:bg-slate-700 hover:text-white' : 'text-slate-600 hover:bg-slate-100 hover:text-slate-900'
                  ]">
                  <LogOut :class="isDark ? 'text-slate-400' : 'text-slate-500'" class="w-5 h-5 mr-3" />
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
  User, LayoutDashboard, UserPlus, Users,Sun, Moon, LogOut 
} from 'lucide-vue-next';
import { useAuthStore } from '../stores/auth';
import type { UserRole } from '../types/UserRole';
import { useThemeStore } from '../stores/theme';

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();
const themeStore = useThemeStore();
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
const isDark = computed(() => themeStore.isDark);
const toggleTheme = () => themeStore.toggleTheme();

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
