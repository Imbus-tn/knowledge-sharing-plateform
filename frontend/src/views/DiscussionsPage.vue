<script setup lang="ts"> 
import { ref, onMounted, watch, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useChatStore } from '../stores/chat';
import { useAuthStore } from '../stores/auth';
import { formatDate, formatTime } from '../utils/date';
import { socketService } from '../services/socket.service';
import type { Message } from '../types/chat';
import { Check, Users, Reply, Smile, X, Plus, Send, MessageSquare } from 'lucide-vue-next';
import type { Reaction } from '../types/reaction';

const chatStore = useChatStore();
const authStore = useAuthStore();
const route = useRoute();
const router = useRouter();

// State
const newMessage = ref('');
const replyingTo = ref<Message | null>(null);
const showAttachmentMenu = ref(false);
const fileInput = ref<HTMLInputElement | null>(null);

// Computed
const currentUser = computed(() => authStore.user);
const isCurrentChatGroup = computed(() => chatStore.currentChat?.isGroup || false);
// Watch for route changes
watch(() => route.params.id, (newId) => {
  if (newId) {
    const chatId = parseInt(newId as string);
    chatStore.fetchChat(chatId);
  }
}, { immediate: true });

// Lifecycle
onMounted(async () => {
  await chatStore.fetchChats();
  socketService.connect();
});

// Methods
const sendMessage = async () => {
  if (!newMessage.value.trim()) return;

  await chatStore.sendMessage(newMessage.value, replyingTo.value || undefined);
  newMessage.value = '';
  replyingTo.value = null;
};

const handleFileUpload = async (e: Event) => {
  const target = e.target as HTMLInputElement;
  if (target.files?.length) {
    await chatStore.sendFile(target.files[0]);
    target.value = '';
  }
};

const formatMessageDate = (timestamp: string) => {
  return formatDate(timestamp);
};

const formatMessageTime = (timestamp: string) => {
  return formatTime(timestamp);
};

const formatLastSeen = (timestamp?: string): string => {
  return timestamp ? new Date(timestamp).toLocaleTimeString() : '';
};

const showDateSeparator = (message: Message, index: number) => {
  if (index === 0) return true;
  const prevMessage = chatStore.messages[index - 1];
  return formatDate(prevMessage.createdAt) !== formatDate(message.createdAt);
};
</script>

<template>
  <!-- Your template from the designer, updated to use the store -->
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
    <div class="grid grid-cols-1 lg:grid-cols-12 gap-6 h-[calc(100vh-8rem)]">
      <!-- Left Sidebar -->
      <div class="lg:col-span-4 flex flex-col h-full">
        <div class="bg-slate-800/50 backdrop-blur-sm rounded-xl border border-slate-700 overflow-hidden flex flex-col h-full">
          <!-- Search and Filters -->
          <div class="p-4 border-b border-slate-700 space-y-3">
            <!-- Search input -->
          </div>
          
          <!-- Conversations List -->
          <div class="overflow-y-auto flex-1">
            <div 
              v-for="chat in chatStore.chats" 
              :key="chat.id"
              @click="router.push(`/chat/${chat.id}`)"
              class="p-4 hover:bg-slate-700/50 cursor-pointer transition-colors border-b border-slate-700/50 last:border-0 group"
              :class="{ 'bg-slate-700/50': chat.id === chatStore.currentChat?.id }"
            >
              <!-- Chat item content -->
              <div class="flex items-start space-x-3">
                <!-- Avatar -->
                <div class="relative">
                  <div v-if="!chat.isGroup" class="w-12 h-12 rounded-full bg-emerald-500 flex items-center justify-center">
                    <span class="text-white font-medium">{{ chat.participants[0].firstName.charAt(0) }}{{ chat.participants[0].lastName.charAt(0) }}</span>
                  </div>
                  <div v-else class="w-12 h-12 rounded-full bg-blue-500 flex items-center justify-center">
                    <Users class="w-6 h-6 text-white" />
                  </div>
                  <div v-if="!chat.isGroup && chatStore.isUserOnline(chat.participants[0].id)" 
                    class="absolute -bottom-1 -right-1 w-4 h-4 bg-emerald-500 rounded-full border-2 border-slate-800"
                  ></div>
                </div>
                
                <!-- Chat info -->
               <div class="flex-1 min-w-0">
                        <div class="flex justify-between items-start">
                          <p class="text-white font-medium truncate">
                            {{ chat.isGroup ? chat.name : `${chat.participants[0].firstName} ${chat.participants[0].lastName}` }}
                          </p>
                          <span class="text-xs text-slate-400 flex-shrink-0">{{ formatTime(chat.lastActivity) }}</span>
                        </div>
                        <div class="flex items-center space-x-1 text-sm text-slate-400">
                          <span v-if="chat.lastMessage?.sender.id === currentUser?.id" class="text-emerald-500">You:</span>
                          <p class="truncate">{{ chat.lastMessage?.text || 'No messages yet' }}</p>
                        </div>
                        <div class="flex items-center justify-between mt-1">
                          <div class="flex items-center space-x-2">
                            <span v-if="chat.lastMessage?.sender.id === currentUser?.id" class="text-blue-400">
                              <!-- Add null check for isRead -->
                              <Check 
                                class="w-4 h-4" 
                                :class="chat.lastMessage?.isRead !== undefined && chat.lastMessage.isRead ? 'text-blue-400' : 'text-slate-400'" 
                              />
                            </span>
                          </div>
                          <div v-if="chat.unreadCount > 0" 
                            class="px-2 py-0.5 rounded-full bg-emerald-500 text-xs text-white font-medium"
                          >
                            {{ chat.unreadCount }}
                          </div>
                        </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Main Chat Area -->
      <div class="lg:col-span-8 flex flex-col h-full">
        <div v-if="chatStore.currentChat" class="bg-slate-800/50 backdrop-blur-sm rounded-xl border border-slate-700 overflow-hidden flex flex-col h-full">
          <!-- Chat Header -->
          <div class="p-4 border-b border-slate-700 flex items-center justify-between">
            <div class="flex items-center space-x-3">
              <div v-if="!isCurrentChatGroup" class="w-10 h-10 rounded-full bg-emerald-500 flex items-center justify-center">
                <span class="text-white font-medium">
                  {{ chatStore.currentChat.participants[0].firstName.charAt(0) }}{{ chatStore.currentChat.participants[0].lastName.charAt(0) }}
                </span>
              </div>
              <div v-else class="w-10 h-10 rounded-full bg-blue-500 flex items-center justify-center">
                <Users class="w-6 h-6 text-white" />
              </div>
              <div>
                <h3 class="text-white font-medium">
                  {{ isCurrentChatGroup ? chatStore.currentChat.name : `${chatStore.currentChat.participants[0].firstName} ${chatStore.currentChat.participants[0].lastName}` }}
                </h3>
                <p class="text-sm text-slate-400">
                  <span v-if="isCurrentChatGroup">
                    {{ chatStore.currentChat.participants.filter(p => chatStore.isUserOnline(p.id)).length }} online • 
                    {{ chatStore.currentChat.participants.length }} members
                  </span>
                  <span v-else>
                    {{ chatStore.isUserOnline(chatStore.currentChat.participants[0].id) ? 'Online' : `Last seen ${formatLastSeen(chatStore.currentChat.participants[0].lastSeen)}` }}
                  </span>
                </p>
              </div>
            </div>
            <div class="flex items-center space-x-3">
              <!-- Action buttons -->
            </div>
          </div>

          <!-- Messages Area -->
          <div class="flex-1 overflow-y-auto p-4 space-y-6">
            <div 
              v-for="(message, index) in chatStore.messages" 
              :key="message.id"
              class="flex flex-col"
              :class="message.sender.id === currentUser?.id ? 'items-end' : 'items-start'"
            >
              <!-- Date separator -->
              <div v-if="showDateSeparator(message, index)" class="flex justify-center my-2">
                <div class="px-4 py-1 rounded-full bg-slate-700/30 text-sm text-slate-400">
                  {{ formatMessageDate(message.createdAt) }}
                </div>
              </div>

              <!-- Message bubble -->
              <div 
                class="group relative max-w-[70%] rounded-lg px-4 py-2"
                :class="[
                  message.sender.id === currentUser?.id ? 'bg-emerald-500 text-white' : 'bg-slate-700/50 text-white',
                ]"
              >
                <!-- Message content -->
                <p>{{ message.text }}</p>
                
                <!-- Message meta -->
                <div class="flex items-center justify-end space-x-1 mt-1">
                  <span class="text-xs opacity-75">{{ formatMessageTime(message.createdAt) }}</span>
                  <div v-if="message.sender.id === currentUser?.id" class="flex">
                    <Check class="w-4 h-4" :class="message.isRead ? 'text-blue-400' : 'opacity-75'" />
                  </div>
                </div>

                <!-- Message actions -->
                <div class="absolute top-0 -right-20 opacity-0 group-hover:opacity-100 transition-opacity flex items-center space-x-1">
                  <button 
                    @click="replyingTo = message"
                    class="p-1.5 bg-slate-700 rounded-lg text-slate-300 hover:text-white"
                  >
                    <Reply class="w-4 h-4" />
                  </button>
                  <button 
                    @click="chatStore.toggleReaction(message, '👍')"
                    class="p-1.5 bg-slate-700 rounded-lg text-slate-300 hover:text-white"
                  >
                    <Smile class="w-4 h-4" />
                  </button>
                </div>
              </div>
            </div>
          </div>

          <!-- Message Input -->
          <div class="p-4 border-t border-slate-700">
            <!-- Reply preview -->
            <div v-if="replyingTo" class="flex items-center justify-between bg-slate-700/30 rounded-lg px-4 py-2 mb-4">
              <div>
                <p class="text-sm text-emerald-500 font-medium">
                  Replying to {{ replyingTo.sender.id === currentUser?.id ? 'yourself' : replyingTo.sender.firstName }}
                </p>
                <p class="text-sm text-slate-400 truncate">{{ replyingTo.text }}</p>
              </div>
              <button @click="replyingTo = null" class="text-slate-400 hover:text-white">
                <X class="w-5 h-5" />
              </button>
            </div>

            <div class="flex items-end space-x-3">
              <button 
                @click="showAttachmentMenu = !showAttachmentMenu"
                class="p-2 text-slate-400 hover:text-white hover:bg-slate-700/50 rounded-lg transition-colors relative"
              >
                <Plus class="w-5 h-5" />
                <input 
                  ref="fileInput"
                  type="file" 
                  class="hidden" 
                  @change="handleFileUpload"
                >
              </button>
              
              <div class="flex-1 relative">
                <textarea
                  v-model="newMessage"
                  rows="1"
                  placeholder="Type a message..."
                  class="w-full px-4 py-3 bg-slate-700/50 border border-slate-600 rounded-lg text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-emerald-500 resize-none"
                  @keydown.enter.prevent="sendMessage"
                ></textarea>
              </div>
              
              <button 
                @click="sendMessage"
                class="p-3 bg-emerald-500 text-white rounded-lg hover:bg-emerald-600 transition-colors"
                :disabled="!newMessage.trim()"
              >
                <Send class="w-5 h-5" />
              </button>
            </div>
          </div>
        </div>

        <!-- No Chat Selected -->
        <div v-else class="bg-slate-800/50 backdrop-blur-sm rounded-xl border border-slate-700 p-8 text-center h-full flex items-center justify-center">
          <div>
            <div class="w-16 h-16 bg-slate-700/50 rounded-full flex items-center justify-center mx-auto mb-4">
              <MessageSquare class="w-8 h-8 text-slate-400" />
            </div>
            <h3 class="text-xl font-semibold text-white mb-2">Select a conversation</h3>
            <p class="text-slate-400">Choose a conversation from the list to start messaging</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>