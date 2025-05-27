<template>
  <div
    :class="[
      'min-h-screen transition-colors duration-300',
      isExcludedRoute
        ? 'bg-gradient-to-br from-slate-900 to-slate-800'
        : isDark
        ? 'bg-slate-900 text-white'
        : 'bg-slate-50 text-slate-900'
    ]"
  >
    <!-- Auth Navigation for auth pages -->
    <AuthNav v-if="isAuthPage" />

    <!-- Main Navigation for landing page -->
    <nav
      v-if="$route.path === '/'"
      class="bg-slate-800/50 backdrop-blur-sm border-b border-slate-700 sticky top-0 z-50"
    >
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex items-center justify-between h-16">
          <!-- Logo Section -->
          <router-link to="/" class="flex items-center">
            <img 
              :src="logoDark" 
              alt="Imbus Knowledge Logo" 
              class="h-14 w-auto"
            />
          </router-link>

          <!-- Sign In Button -->
          <router-link
            to="/login"
            class="inline-flex items-center px-4 py-2 bg-emerald-500 text-white rounded-lg font-medium hover:bg-emerald-600 transition-colors"
          >
            Sign In
          </router-link>
        </div>
      </div>
    </nav>

    <!-- Authenticated Pages Navigation -->
    <Nav v-else-if="isAuthenticated" />

    <!-- Main Content -->
    <router-view />

    <!-- Notification Panel -->
    <NotificationPanel />
  </div>
</template>

<script setup lang="ts">
import { computed, watch, onMounted } from 'vue';
import logoDark from './assets/logo-dark.png';
import { useRoute } from 'vue-router';
import { useAuthStore } from './stores/auth';
import { useThemeStore } from './stores/theme';
import AuthNav from './components/AuthNav.vue';
import Nav from './components/Nav.vue';
import NotificationPanel from './components/NotificationPanel.vue';

// Get route and stores
const route = useRoute();
const authStore = useAuthStore();
const themeStore = useThemeStore();

// List of routes where dark mode should NOT be applied
const excludedRoutes = ['/', '/login', '/forgot-password', '/verify-otp', '/reset-password', '/register'];

// Computed properties
const isExcludedRoute = computed(() => {
  return excludedRoutes.includes(route.path);
});

const isAuthPage = computed(() => {
  return ['/login', '/forgot-password', '/verify-otp', '/reset-password', '/register'].includes(route.path);
});

const isAuthenticated = computed(() => authStore.isAuthenticated);

// Add this missing computed property
const isDark = computed(() => themeStore.isDark); // This was missing

// Watch route changes and conditionally apply dark mode
watch(
  () => route.path,
  (newPath) => {
    const applyDarkMode = !excludedRoutes.includes(newPath);
    themeStore.applyTheme(applyDarkMode);
  },
  { immediate: true }
);

// Initialize theme on mount
onMounted(() => {
  themeStore.initTheme();
});
</script>