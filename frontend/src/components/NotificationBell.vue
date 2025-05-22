<template>
  <button 
    @click="navigateToNotifications"
    class="relative p-2 rounded-full transition-colors"
    :class="[isDark ? 'hover:bg-slate-700/50' : 'hover:bg-slate-200']"
  >
    <Bell class="w-6 h-6" :class="[isDark ? 'text-white' : 'text-slate-600']" />
    
    <!-- Notification Badge -->
    <span v-if="unreadCount > 0" class="absolute -top-1 -right-1 flex items-center justify-center">
      <span class="animate-ping absolute h-4 w-4 rounded-full bg-red-400 opacity-75"></span>
      <span class="relative bg-red-500 text-white text-xs w-4 h-4 flex items-center justify-center rounded-full">
        {{ unreadCount > 9 ? '9+' : unreadCount }}
      </span>
    </span>
  </button>
</template>

<script setup lang="ts">
import { computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Bell } from 'lucide-vue-next'
import { useNotificationStore } from '../stores/notification'
import { useThemeStore } from '../stores/theme'

const router = useRouter()
const route = useRoute()
const notificationStore = useNotificationStore()
const themeStore = useThemeStore()

const isDark = computed(() => themeStore.isDark)
const unreadCount = computed(() => notificationStore.unreadCount)

function navigateToNotifications() {
  router.push('/notifications')
  if (unreadCount.value > 0) {
    notificationStore.markAllAsRead()
  }
}

// Clear notifications when navigating to favorites
watch(
  () => route.path,
  (newPath) => {
    if (newPath === '/notifications' || newPath === '/favorites') {
      notificationStore.markAllAsRead()
    }
  }
)
</script>