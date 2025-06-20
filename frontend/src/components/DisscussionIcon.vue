<template>
  <div class="relative">
    <router-link to="/discussions" class="relative group p-1 rounded-full hover:bg-slate-700/50 transition-colors">
      <MessageSquare class="w-6 h-6 text-slate-400 group-hover:text-white cursor-pointer" />
      
      <!-- Badge with Animation -->
      <span 
        v-if="unreadCount > 0"
        class="absolute -top-1 -right-1 flex items-center justify-center"
      >
        <span class="animate-ping absolute h-4 w-4 rounded-full bg-emerald-400 opacity-75"></span>
        <span class="relative bg-emerald-500 text-white text-xs w-4 h-4 flex items-center justify-center rounded-full">
          {{ unreadCount > 9 ? '9+' : unreadCount }}
        </span>
      </span>
    </router-link>
    
    <!-- Preview Tooltip -->
    <div 
      v-if="showPreview && latestMessages.length > 0" 
      class="absolute right-0 mt-2 w-72 bg-slate-800 rounded-lg shadow-lg border border-slate-700 z-50 overflow-hidden"
    >
      <div class="p-3 border-b border-slate-700 flex justify-between items-center">
        <h3 class="text-sm font-medium text-white">Recent Discussions</h3>
        <router-link to="/discussions" class="text-xs text-emerald-500 hover:text-emerald-400">
          View All
        </router-link>
      </div>
      <div class="max-h-80 overflow-y-auto">
        <div 
          v-for="message in latestMessages" 
          :key="message.id"
          class="p-3 hover:bg-slate-700/50 transition-colors cursor-pointer border-b border-slate-700/50 last:border-0"
          @click="goToDiscussion(message.chatId)"
        >
          <div class="flex items-start space-x-2">
            <div class="w-8 h-8 rounded-full bg-slate-700 flex items-center justify-center flex-shrink-0">
              <span class="text-white text-sm">{{ message.sender.initials }}</span>
            </div>
            <div class="flex-1 min-w-0">
              <div class="flex justify-between items-start">
                <p class="text-sm font-medium text-white truncate">{{ message.sender.name }}</p>
                <p class="text-xs text-slate-400">{{ formatTime(message.time) }}</p>
              </div>
              <p class="text-xs text-slate-300 truncate">{{ message.preview }}</p>
            </div>
            <div v-if="message.unread" class="w-2 h-2 rounded-full bg-emerald-500 mt-2 flex-shrink-0"></div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import { MessageSquare } from 'lucide-vue-next';
import { useDiscussionStore } from '../stores/chat';

const router = useRouter();
const discussionStore = useDiscussionStore();
const showPreview = ref(false);
let previewTimeout: number | null = null;

const unreadCount = computed(() => discussionStore.unreadCount);
const latestMessages = computed(() => discussionStore.latestMessages);

const handleMouseEnter = () => {
  previewTimeout = window.setTimeout(() => {
    showPreview.value = true;
  }, 500); // Show preview after 500ms hover
};

const handleMouseLeave = () => {
  if (previewTimeout) {
    clearTimeout(previewTimeout);
    previewTimeout = null;
  }
  showPreview.value = false;
};

const goToDiscussion = (chatId: string) => {
  discussionStore.markChatAsRead(chatId);
  router.push(`/discussions?chat=${chatId}`);
  showPreview.value = false;
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
    return `${diffMins}m`;
  } else if (diffHours < 24) {
    return `${diffHours}h`;
  } else if (diffDays < 7) {
    return `${diffDays}d`;
  } else {
    return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
  }
};

// Add event listeners
onMounted(() => {
  const element = document.querySelector('.discussion-icon');
  if (element) {
    element.addEventListener('mouseenter', handleMouseEnter);
    element.addEventListener('mouseleave', handleMouseLeave);
  }
});

onUnmounted(() => {
  if (previewTimeout) {
    clearTimeout(previewTimeout);
  }
  const element = document.querySelector('.discussion-icon');
  if (element) {
    element.removeEventListener('mouseenter', handleMouseEnter);
    element.removeEventListener('mouseleave', handleMouseLeave);
  }
});
</script>