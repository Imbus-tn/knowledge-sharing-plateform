<template>
    <div class="relative">
      <button 
        @click.stop="navigateToNotifications"
        class="relative p-1 rounded-full hover:bg-slate-700/50 transition-colors"
        :class="{ 'text-white': showPanel, 'text-slate-400 hover:text-white': !showPanel }"
      >
        <Bell class="w-6 h-6" />
        
        <!-- Notification Badge with Animation -->
        <span 
          v-if="unreadCount > 0"
          class="absolute -top-1 -right-1 flex items-center justify-center"
        >
          <span class="animate-ping absolute h-4 w-4 rounded-full bg-red-400 opacity-75"></span>
          <span class="relative bg-red-500 text-white text-xs w-4 h-4 flex items-center justify-center rounded-full">
            {{ unreadCount > 9 ? '9+' : unreadCount }}
          </span>
        </span>
      </button>
    </div>
  </template>
  
  <script setup lang="ts">
  import { computed } from 'vue';
  import { Bell } from 'lucide-vue-next';
  import { useNotificationStore } from '../stores/notification';
  import { useRouter } from 'vue-router';
  
  const router = useRouter();
  const notificationStore = useNotificationStore();
  
  const unreadCount = computed(() => notificationStore.unreadCount);
  const showPanel = computed(() => notificationStore.showNotificationPanel);
  
  const navigateToNotifications = () => {
    // Always navigate to the notifications page
    router.push('/notifications');
    
    // If there are unread notifications, mark them as read
    if (unreadCount.value > 0) {
      notificationStore.markAllAsRead();
    }
  };
  </script>