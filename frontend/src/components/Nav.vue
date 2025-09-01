<template>
  <nav :class="[
    'backdrop-blur-sm sticky top-0 z-50 border-b shadow-xl transition-colors duration-200',
    isDark 
      ? 'bg-slate-800/50 border-slate-700' 
      : 'bg-slate-100/80 border-slate-300'
  ]">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex items-center h-16">
        <!-- Left Section - Logo -->
        <div class="flex-shrink-0">
          <router-link to="/" class="flex items-center">
            <img 
              :src="isDark ? logoDark : logoLight" 
              alt="Imbus Knowledge Logo" 
              class="h-14 w-auto"
            />
          </router-link>
        </div>
        
        <!-- Center Section - Search -->
        <div class="flex-1 flex items-center justify-center px-8" :class="{ 'opacity-0 pointer-events-none': !isActivePage('feed') }">
          <SearchBar />
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
          </router-link>
          
          <div :class="isDark ? 'bg-slate-700' : 'bg-slate-200'" class="w-px h-8 mx-2"></div>
          
          <!-- Profile Dropdown -->
          <div class="relative profile-menu">
            <button @click.stop="toggleProfileMenu" class="flex items-center space-x-3">
              <div v-if="avatarUrl" class="w-8 h-8 rounded-full overflow-hidden">
                <img :src="avatarUrl" alt="User Avatar" class="w-full h-full object-cover" />
              </div>
              <div v-else 
                :class="isDark ? 'bg-emerald-500' : 'bg-emerald-600'" 
                class="w-8 h-8 rounded-full flex items-center justify-center"
              >
                <span class="text-white font-medium">{{ userInitials }}</span>
              </div>
            </button>
            
            <div v-if="showProfileMenu" ref="profileDropdown"
              :class="[
                'absolute right-0 mt-2 w-64 rounded-lg shadow-lg ring-1 ring-black ring-opacity-5 z-50 overflow-hidden',
                isDark ? 'bg-slate-800' : 'bg-white'
              ]"
            >
              <!-- Header Section -->
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
                    :class="isDark ? 'bg-emerald-500' : 'bg-emerald-600'"
                    class="absolute inset-0 flex items-center justify-center"
                  >
                    <span class="text-white text-xl font-bold">{{ userInitials }}</span>
                  </div>
                </div>

                <!-- Name & Email -->
                <div class="text-center">
                  <h3 :class="isDark ? 'text-white' : 'text-slate-900'" class="font-medium mb-1">
                    {{ authStore.user?.name }}
                  </h3>
                  <p :class="isDark ? 'text-slate-400' : 'text-slate-500'" class="text-sm mb-2">
                    {{ authStore.user?.email }}
                  </p>
                </div>

                <!-- Role Badge -->
                <div v-if="user" 
                  :class="roleStyle(user?.role)"
                  class="inline-flex items-center px-2 py-0.5 rounded-full text-xs"
                >
                  {{ user?.role.toLowerCase() }}
                </div>
              </div>

              <!-- Menu Items -->
              <div class="py-2">
                <!-- Dashboard -->
                <router-link to="/dashboard" 
                  :class="[
                    'flex items-center px-4 py-3 text-sm transition-colors',
                    isDark 
                      ? 'text-slate-300 hover:bg-slate-700 hover:text-white' 
                      : 'text-slate-600 hover:bg-slate-100 hover:text-slate-900'
                  ]"
                  @click="showProfileMenu = false"
                >
                  <LayoutDashboard 
                    :class="isDark ? 'text-slate-400' : 'text-slate-500'" 
                    class="w-5 h-5 mr-3" 
                  />
                  <span>Your dashboard</span>
                </router-link>

                <!-- Profile -->
                <router-link to="/profile" 
                  :class="[
                    'flex items-center px-4 py-3 text-sm transition-colors',
                    isDark 
                      ? 'text-slate-300 hover:bg-slate-700 hover:text-white' 
                      : 'text-slate-600 hover:bg-slate-100 hover:text-slate-900'
                  ]"
                  @click="showProfileMenu = false"
                >
                  <User 
                    :class="isDark ? 'text-slate-400' : 'text-slate-500'" 
                    class="w-5 h-5 mr-3" 
                  />
                  <span>Your profile</span>
                </router-link>

                <!-- Theme Toggle -->
                <button 
                  @click="toggleTheme"
                  class="flex items-center px-4 py-3 text-sm w-full text-left transition-colors"
                  :class="[
                    isDark 
                      ? 'text-slate-300 hover:bg-slate-700 hover:text-white' 
                      : 'text-slate-600 hover:bg-slate-100 hover:text-slate-900'
                  ]"
                >
                  <component 
                    :is="isDark ? Sun : Moon" 
                    :class="isDark ? 'text-slate-400' : 'text-slate-500'"
                    class="w-5 h-5 mr-3"
                  />
                  <span>{{ isDark ? 'Light Mode' : 'Dark Mode' }}</span>
                </button>

                <!-- Admin Links -->
                <router-link v-if="authStore.isAdmin" to="/invite-user" 
                  :class="[
                    'flex items-center px-4 py-3 text-sm transition-colors',
                    isDark 
                      ? 'text-slate-300 hover:bg-slate-700 hover:text-white' 
                      : 'text-slate-600 hover:bg-slate-100 hover:text-slate-900'
                  ]"
                  @click="showProfileMenu = false"
                >
                  <UserPlus 
                    :class="isDark ? 'text-slate-400' : 'text-slate-500'" 
                    class="w-5 h-5 mr-3" 
                  />
                  <span>Invite User</span>
                </router-link>

                <router-link v-if="authStore.isAdmin" to="/manage-users" 
                  :class="[
                    'flex items-center px-4 py-3 text-sm transition-colors',
                    isDark 
                      ? 'text-slate-300 hover:bg-slate-700 hover:text-white' 
                      : 'text-slate-600 hover:bg-slate-100 hover:text-slate-900'
                  ]"
                  @click="showProfileMenu = false"
                >
                  <Users 
                    :class="isDark ? 'text-slate-400' : 'text-slate-500'" 
                    class="w-5 h-5 mr-3" 
                  />
                  <span>Manage Users</span>
                </router-link>
                
                <router-link v-if="authStore.isAdmin" to="/reported-posts" 
                  :class="[
                    'flex items-center px-4 py-3 text-sm transition-colors',
                    isDark 
                      ? 'text-slate-300 hover:bg-slate-700 hover:text-white' 
                      : 'text-slate-600 hover:bg-slate-100 hover:text-slate-900'
                  ]"
                  @click="showProfileMenu = false"
                >
                  <Bell 
                    :class="isDark ? 'text-slate-400' : 'text-slate-500'" 
                    class="w-5 h-5 mr-3" 
                  />
                  <span>Reported Posts</span>
                </router-link>
              </div>

              <!-- Logout -->
              <div :class="isDark ? 'border-slate-700' : 'border-slate-200'" class="border-t py-2">
                <button 
                  @click="handleLogout"
                  :class="[
                    'flex items-center w-full px-4 py-3 text-sm transition-colors',
                    isDark ? 'text-red-400 hover:bg-slate-700' : 'text-red-600 hover:bg-slate-100'
                  ]"
                >
                  <LogOut 
                    :class="isDark ? 'text-red-400' : 'text-red-600'" 
                    class="w-5 h-5 mr-3" 
                  />
                  <span>Sign out</span>
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
  Search, Home, Bell, MessageSquare, 
  User, LayoutDashboard, UserPlus, Users, Sun, Moon, LogOut 
} from 'lucide-vue-next';
import logoDark from '../assets/logo-dark.png';
import logoLight from '../assets/logo-light.png';
import { useAuthStore } from '../stores/auth';
import type { UserRole } from '../types/UserRole';
import { useThemeStore } from '../stores/theme';
import SearchBar from '../components/SearchBar.vue';

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