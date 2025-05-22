<template>
    <div class="max-w-3xl mx-auto px-4 sm:px-6 py-8">
      <!-- Push Notifications Banner -->
      <div 
        v-if="showBanner" 
        :class="[
          'backdrop-blur-sm rounded-lg border shadow-xl p-6 mb-8 relative transition-colors duration-200',
          isDark ? 'bg-slate-800/50 border-slate-700' : 'bg-slate-100/90 border-slate-300'
        ]"
      >
        <button 
          @click="closeBanner"
          :class="[
            'absolute top-4 right-4 transition-colors',
            isDark ? 'text-slate-400 hover:text-white' : 'text-slate-500 hover:text-slate-900'
          ]"
        >
          <X class="w-5 h-5" />
        </button>
        <h2 :class="['text-xl font-semibold mb-2', isDark ? 'text-white' : 'text-slate-900']">
          Push notifications
        </h2>
        <p :class="[isDark ? 'text-slate-300' : 'text-slate-600']">
          Stay in the loop whenever you get a mention, reply, or other important updates.
        </p>
        <button 
          class="px-4 py-2 mt-4 rounded-lg hover:bg-emerald-600 transition-colors"
          :class="[isDark ? 'bg-emerald-500 text-white' : 'bg-emerald-600 text-white']"
          @click="enableNotifications"
        >
          Enable notifications
        </button>
      </div>
  
      <!-- Notifications Header -->
      <div class="mb-6 flex justify-between items-center">
        <h1 :class="['text-2xl font-bold', isDark ? 'text-white' : 'text-slate-900']">
          Notifications
        </h1>
        <button 
          v-if="hasUnread"
          @click="markAllAsRead"
          class="text-sm transition-colors"
          :class="[isDark ? 'text-emerald-500 hover:text-emerald-400' : 'text-emerald-600 hover:text-emerald-700']"
        >
          Mark all as read
        </button>
      </div>
  
      <!-- Notifications List -->
      <div v-if="combinedNotifications.length > 0" class="space-y-6">
        <!-- Individual Notifications -->
        <div 
          v-for="notification in combinedNotifications" 
          :key="notification.id"
          class="backdrop-blur-sm rounded-lg border overflow-hidden cursor-pointer transition-colors duration-200"
          :class="[
            isDark ? 'bg-slate-800/50 border-slate-700' : 'bg-slate-100/90 border-slate-300',
            !notification.read && borderHighlight
          ]"
          @click="markAsRead(notification.id)"
        >
          <div class="p-4 flex items-start space-x-3">
            <div class="flex-shrink-0">
              <Bell 
                v-if="notification.type === 'system'" 
                :class="isDark ? 'text-emerald-500' : 'text-emerald-600'"
                class="w-6 h-6"
              />
              <div 
                v-else 
                class="w-8 h-8 rounded-full flex items-center justify-center"
                :class="isDark ? 'bg-emerald-500' : 'bg-emerald-600'"
              >
                <span class="text-white font-medium">
                  {{ notification.user?.initials || 'UN' }}
                </span>
              </div>
            </div>
  
            <div class="flex-1 min-w-0">
              <div class="flex items-center space-x-2">
                <span 
                  v-if="notification.user?.name" 
                  class="font-medium"
                  :class="isDark ? 'text-white' : 'text-slate-900'"
                >
                  {{ notification.user.name }}
                </span>
                <span :class="isDark ? 'text-slate-400' : 'text-slate-500'">
                  {{ getActionText(notification) }}
                </span>
              </div>
              <p :class="isDark ? 'text-white mt-1' : 'text-slate-900 mt-1'">
                {{ notification.postTitle || notification.message }}
              </p>
              <p :class="isDark ? 'text-sm text-slate-400 mt-1' : 'text-sm text-slate-500 mt-1'">
                {{ formatTime(notification.createdAt) }}
              </p>
            </div>
          </div>
        </div>
  
        <!-- Welcome Notification -->
        <div 
          class="backdrop-blur-sm rounded-lg border p-4 transition-colors duration-200"
          :class="isDark ? 'bg-slate-800/50 border-slate-700' : 'bg-slate-100/90 border-slate-300'"
        >
          <div class="flex items-start space-x-3">
            <div class="flex-shrink-0">
              <Bell :class="isDark ? 'text-emerald-500' : 'text-emerald-600'" class="w-6 h-6" />
            </div>
            <div>
              <p class="font-medium" :class="isDark ? 'text-white' : 'text-slate-900'">
                Welcome to your new notification center!
              </p>
              <p :class="isDark ? 'text-slate-300 mt-2' : 'text-slate-600 mt-2'">
                The notification system notifies you of important events such as replies, mentions, updates, etc.
              </p>
            </div>
          </div>
        </div>
      </div>
  
      <!-- Empty State -->
      <div v-else class="text-center py-16">
        <div :class="[
          'inline-flex items-center justify-center w-16 h-16 rounded-full mb-4 shadow-xl',
          isDark ? 'bg-slate-800/50' : 'bg-slate-100/90'
        ]">
          <Bell :class="isDark ? 'text-slate-600' : 'text-slate-400'" class="w-8 h-8" />
        </div>
        <h3 :class="['text-xl font-medium mb-2', isDark ? 'text-white' : 'text-slate-900']">
          No notifications yet
        </h3>
        <p :class="isDark ? 'text-slate-400 max-w-md mx-auto' : 'text-slate-500 max-w-md mx-auto'">
          When you receive notifications, they will appear here.
        </p>
      </div>
    </div>
  </template>
  
  <script setup lang="ts">
  import { ref, computed, onMounted, onActivated, watch } from 'vue';
  import { useNotificationStore } from '../stores/notification';
  import { useThemeStore } from '../stores/theme';
  import { useRoute, useRouter } from 'vue-router';
  import { Bell, X } from 'lucide-vue-next';
  
  const router = useRouter();
  const route = useRoute ();
  const notificationStore = useNotificationStore();
  const themeStore = useThemeStore();
  const isDark = computed(() => themeStore.isDark);
  const showBanner = ref(true);
  
  const borderHighlight = computed(() => isDark.value ? 'border-l-4 border-l-emerald-500' : 'border-l-4 border-l-emerald-600');
  
  // Combine notifications from the store
  const combinedNotifications = computed(() => {
    return [...notificationStore.notifications].sort((a, b) => {
      const dateA = new Date(a.createdAt);
      const dateB = new Date(b.createdAt);
      return dateB.getTime() - dateA.getTime();
    });
  });
  
  const hasUnread = computed(() => combinedNotifications.value.some(n => !n.read));
  
  function closeBanner() {
    showBanner.value = false;
  }
  
  function enableNotifications() {
    if ('Notification' in window) {
      Notification.requestPermission().then(permission => {
        if (permission === 'granted') {
          notificationStore.addNotification({
            type: 'system',
            message: 'Push notifications have been enabled!',
            link: '/notifications'
          });
        }
      });
    }
    showBanner.value = false;
  }
  
  function markAllAsRead() {
    notificationStore.markAllAsRead();
  }
  
  function markAsRead(id: string) {
    notificationStore.markAsRead(id);
  }
  
  function formatTime(timestamp: string) {
    const date = new Date(timestamp);
    const now = new Date();
    const diffMs = now.getTime() - date.getTime();
    const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24));
  
    if (diffDays === 0) return 'Today';
    if (diffDays === 1) return 'Yesterday';
    if (diffDays < 7) return `${diffDays} days ago`;
    return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
  }
  
  function getActionText(notification: any) {
    switch (notification.type) {
      case 'post': return 'shared a new post on';
      case 'comment': return 'commented on your post';
      case 'favorite': return 'favorited your post';
      case 'reaction': return 'reacted to your post';
      default: return notification.message || '';
    }
  }
  
  onMounted(() => {
    const userId = '2'; // Replace with real user ID from auth store
    notificationStore.setCurrentUser(userId);
    notificationStore.fetchNotifications();
  });
  
  onActivated(() => {
    if (router.currentRoute.value.path === '/notifications') {
      notificationStore.markAllAsRead();
    }
  });

  watch(
    () => route.path,
    (newPath) => {
        if (newPath === '/notifications') {
        notificationStore.markAllAsRead()
        }
    }
    )
  </script>