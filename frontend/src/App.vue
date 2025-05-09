<template>
  <div class="min-h-screen bg-gradient-to-br from-slate-900 to-slate-800">
    
    <!-- Auth Navigation for auth pages -->
    <AuthNav v-if="isAuthPage"/>

    <!-- Main Navigation for landing page -->
    <nav v-if="$route.path === '/'" class="bg-slate-800/50 backdrop-blur-sm border-b border-slate-700 sticky top-0 z-50">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex items-center justify-between h-16">
          <router-link to="/" class="flex items-center space-x-3">
            <BookOpen class="w-8 h-8 text-emerald-500" />
            <span class="text-xl font-bold text-white">Imbus Knowledge</span>
          </router-link>
          
          <router-link 
            to="/login" 
            class="inline-flex items-center px-4 py-2 bg-emerald-500 text-white rounded-lg font-medium hover:bg-emerald-600 transition-colors">
            Sign In
          </router-link>
        </div>
      </div>
    </nav>

    <!-- Authenticated Pages Navigation -->
     <Nav v-else-if="isAuthenticated" />

    <!-- Main Content -->
    <router-view></router-view>

    <!-- Notification Panel -->
    <NotificationPanel />
    
  </div>
</template>
<script setup lang="ts">
  import { computed } from 'vue';
  import { BookOpen } from 'lucide-vue-next';
  import { useRouter } from 'vue-router';
  import { useAuthStore } from './stores/auth';
  import AuthNav from './components/AuthNav.vue';
  import Nav from './components/Nav.vue';
  import NotificationPanel from './components/NotificationPanel.vue';

  
  const router = useRouter();
  const authStore = useAuthStore();

  const isAuthPage = computed(() => {
    const authRoutes = ['/login', '/forgot-password', '/verify-otp', '/reset-password', '/register'];
    return authRoutes.includes(router.currentRoute.value.path);
  });

  const isAuthenticated = computed(() => authStore.isAuthenticated);

</script>