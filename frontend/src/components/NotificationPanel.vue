<template>
    <div class="fixed inset-0 z-50" v-if="showPanel" @click="closePanel">
      <!-- Backdrop -->
      <div class="absolute inset-0 bg-black/20 backdrop-blur-sm"></div>
      
      <!-- Notification Panel -->
      <div 
        class="absolute right-4 top-16 w-96 max-h-[80vh] bg-slate-800 rounded-lg shadow-xl border border-slate-700 overflow-hidden"
        @click.stop
      >
        <!-- Header -->
        <div class="flex items-center justify-between p-4 border-b border-slate-700">
          <h3 class="text-lg font-medium text-white">Notifications</h3>
          <div class="flex items-center space-x-2">
            <button 
              v-if="unreadCount > 0"
              @click="markAllAsRead" 
              class="text-xs text-emerald-500 hover:text-emerald-400"
            >
              Mark all as read
            </button>
            <button @click="closePanel" class="text-slate-400 hover:text-white">
              <X class="w-5 h-5" />
            </button>
          </div>
        </div>
        
        <!-- Notification List -->
        <div class="overflow-y-auto max-h-[calc(80vh-60px)]">
          <div v-if="notifications.length === 0" class="p-6 text-center">
            <Bell class="w-12 h-12 text-slate-600 mx-auto mb-2" />
            <p class="text-slate-400">No notifications yet</p>
          </div>
          
          <div v-else>
            <div 
              v-for="notification in notifications" 
              :key="notification.id"
              :class="[
                'p-4 border-b border-slate-700 hover:bg-slate-700/50 transition-colors cursor-pointer',
                { 'bg-slate-700/20': !notification.read }
              ]"
              @click="handleNotificationClick(notification)"
            >
              <div class="flex items-start space-x-3">
                <!-- User Avatar or Icon -->
                <div v-if="notification.user" class="flex-shrink-0 w-10 h-10 rounded-full bg-emerald-500 flex items-center justify-center">
                  <span class="text-white font-medium">{{ notification.user.initials }}</span>
                </div>
                <div v-else class="flex-shrink-0 w-10 h-10 rounded-full bg-slate-700 flex items-center justify-center">
                  <Bell class="w-5 h-5 text-slate-300" />
                </div>
                
                <!-- Content -->
                <div class="flex-1 min-w-0">
                  <p class="text-sm text-white">{{ notification.message }}</p>
                  <p class="text-xs text-slate-400 mt-1">{{ formatTime(notification.createdAt) }}</p>
                </div>
                
                <!-- Unread Indicator -->
                <div v-if="!notification.read" class="w-2 h-2 rounded-full bg-emerald-500 mt-2"></div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script setup lang="ts">
  import { computed } from 'vue';
  import { useRouter } from 'vue-router';
  import { Bell, X } from 'lucide-vue-next';
  import { useNotificationStore, type Notification } from '../stores/notification';
  
  const notificationStore = useNotificationStore();
  const router = useRouter();
  
  const showPanel = computed(() => notificationStore.showNotificationPanel);
  const notifications = computed(() => notificationStore.notifications);
  const unreadCount = computed(() => notificationStore.unreadCount);
  
  const closePanel = () => {
    notificationStore.closeNotificationPanel();
  };
  
  const markAllAsRead = () => {
    notificationStore.markAllAsRead();
  };
  
  const handleNotificationClick = (notification: Notification) => {
    // Mark as read
    notificationStore.markAsRead(notification.id);
    
    // Navigate to the linked page
    if (notification.link) {
      router.push(notification.link);
    }
    
    // Close the panel
    closePanel();
  };
  
  const formatTime = (timestamp: string) => {
    const date = new Date(timestamp);
    const now = new Date();
    const diffMs = now.getTime() - date.getTime();
    const diffMins = Math.round(diffMs / 60000);
    const diffHours = Math.round(diffMs / 3600000);
    const diffDays = Math.round(diffMs / 86400000);
    
    if (diffMins < 1) {
      return 'Just now';
    } else if (diffMins < 60) {
      return `${diffMins} min${diffMins > 1 ? 's' : ''} ago`;
    } else if (diffHours < 24) {
      return `${diffHours} hour${diffHours > 1 ? 's' : ''} ago`;
    } else if (diffDays < 7) {
      return `${diffDays} day${diffDays > 1 ? 's' : ''} ago`;
    } else {
      return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
    }
  };
  </script>