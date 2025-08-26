<script setup lang="ts">
import { ref, onMounted, watch, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useChatStore } from '../stores/chat';
import { useAuthStore } from '../stores/auth';
import { formatDate, formatTime } from '../utils/date';
import { socketService } from '../services/socket.service';
import { userApi } from '../api/userApi';
import type { Message } from '../types/chat';
import { Check, Users, Reply, Smile, X, Plus, Send, MessageSquare, Search, User } from 'lucide-vue-next';
import type { User as UserType } from '../types/user';
import type { ChatParticipant } from '../types/chatParticipants';

const chatStore = useChatStore();
const authStore = useAuthStore();
const route = useRoute();
const router = useRouter();

// State
const newMessage = ref('');
const replyingTo = ref<Message | null>(null);
const showAttachmentMenu = ref(false);
const fileInput = ref<HTMLInputElement | null>(null);

// Current user
const currentUser = computed(() => authStore.user);
const isCurrentChatGroup = computed(() => chatStore.currentChat?.isGroup || false);

// Online participants
const onlineParticipants = computed(() => {
  if (!chatStore.currentChat) return [];
  return chatStore.currentChat.participants.filter((p: ChatParticipant) => chatStore.isUserOnline(p.id));
});

// New Chat Modal
const showCreateChatModal = ref(false);
const newChatType = ref<'direct' | 'group'>('direct');
const searchQuery = ref('');
const newGroupName = ref('');
const selectedUsers = ref<UserType[]>([]);
const searchResults = ref<UserType[]>([]);

// Computed: Can create?
const canCreateChat = computed(() => {
  if (newChatType.value === 'direct') {
    return selectedUsers.value.length === 1;
  }
  if (newChatType.value === 'group') {
    return selectedUsers.value.length >= 2 && newGroupName.value.trim().length > 0;
  }
  return false;
});

// Select/Remove user
const selectUser = (user: UserType) => {
  if (!selectedUsers.value.some(u => u.id === user.id)) {
    selectedUsers.value = [...selectedUsers.value, user]; // Reactive
  }
  searchQuery.value = '';
  searchResults.value = [];
};

const removeUser = (user: UserType) => {
  selectedUsers.value = selectedUsers.value.filter(u => u.id !== user.id);
};

// Get initials from ChatParticipant
const getInitials = (user: ChatParticipant): string => {
  if (user.initials) return user.initials;
  return user.name
    ? user.name.split(' ').map(n => n[0]).join('').toUpperCase().substring(0, 2)
    : 'U';
};

// Search users
const performSearch = async () => {
  if (searchQuery.value.length < 2) {
    searchResults.value = [];
    return;
  }

  try {
    searchResults.value = await userApi.searchUsers(searchQuery.value);
    // Filter out already selected
    searchResults.value = searchResults.value.filter(u =>
      !selectedUsers.value.some(s => s.id === u.id)
    );
  } catch (error) {
    console.error('Search failed:', error);
    searchResults.value = [];
  }
};

// Debounce
const debounce = <T extends (...args: any[]) => void>(fn: T, delay: number) => {
  let timeoutId: ReturnType<typeof setTimeout>;
  return (...args: any[]) => {
    clearTimeout(timeoutId);
    timeoutId = setTimeout(() => fn(...args), delay);
  };
};

watch(searchQuery, debounce(performSearch, 300));

// Create Chat
const createNewChat = async () => {
  if (selectedUsers.value.length !== 1) return;

  try {
    const [user] = selectedUsers.value;
    const chat = await chatStore.createChat([user.id]);
    showCreateChatModal.value = false;
    selectedUsers.value = [];
   
  } catch (error) {
    console.error('Failed to create chat:', error);
  }
};

// Create Group
const createNewGroup = async () => {
  if (selectedUsers.value.length < 2 || !newGroupName.value.trim()) return;

  try {
    const chat = await chatStore.createGroup(newGroupName.value, selectedUsers.value.map(u => u.id));
    showCreateChatModal.value = false;
    selectedUsers.value = [];
    newGroupName.value = '';
    
  } catch (error) {
    console.error('Failed to create group:', error);
  }
};

// Watch route
watch(() => route.params.id, (newId) => {
  if (newId) {
    const chatId = parseInt(newId as string);
    chatStore.fetchChat(chatId);
  }
}, { immediate: true });

// Debug
watch(canCreateChat, (val) => {
  console.log('canCreateChat:', val);
});

onMounted(async () => {
  await chatStore.fetchChats();
  socketService.connect();
});

// Send message
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

// Format message date
const formatMessageDate = (timestamp: string) => {
  const date = new Date(timestamp);
  const now = new Date();
  const diffDays = Math.floor((now.getTime() - date.getTime()) / (1000 * 60 * 60 * 24));
  if (diffDays === 0) return 'Today';
  if (diffDays === 1) return 'Yesterday';
  return date.toLocaleDateString();
};

const formatMessageTime = (timestamp: string) => {
  return formatTime(timestamp);
};

const formatLastSeen = (timestamp?: string): string => {
  return timestamp ? new Date(timestamp).toLocaleTimeString() : '';
};

const showDateSeparator = (message: Message, index: number) => {
  if (index === 0) return true;
  const prev = chatStore.messages[index - 1];
  return formatDate(prev.createdAt) !== formatDate(message.createdAt);
};
</script>

<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
    <div class="grid grid-cols-1 lg:grid-cols-12 gap-6 h-[calc(100vh-8rem)]">
      <!-- Left Sidebar -->
      <div class="lg:col-span-4 flex flex-col h-full">
        <div class="bg-slate-800/50 backdrop-blur-sm rounded-xl border border-slate-700 overflow-hidden flex flex-col h-full">
          
          <!-- New Chat Button -->
          <button
            @click="showCreateChatModal = true"
            class="w-full flex items-center justify-center px-4 py-2 bg-emerald-500 text-white rounded-lg hover:bg-emerald-600 transition-colors mb-3"
          >
            <Plus class="w-4 h-4 mr-2" />
            New Chat
          </button>

          <!-- Create Chat Modal -->
          <div v-if="showCreateChatModal" class="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center p-4 z-50">
            <div class="bg-slate-800 rounded-xl shadow-xl max-w-md w-full p-6" @click.stop>
              <div class="flex justify-between items-center mb-6">
                <h2 class="text-xl font-semibold text-white">Create New Chat</h2>
                <button @click="showCreateChatModal = false" class="text-slate-400 hover:text-white">
                  <X class="w-5 h-5" />
                </button>
              </div>

              <!-- Chat Type -->
              <div class="mb-6">
                <label class="block text-sm font-medium text-slate-300 mb-3">Type</label>
                <div class="grid grid-cols-2 gap-3">
                  <button
                    @click="newChatType = 'direct'"
                    :class="{
                      'bg-emerald-500/20 border-emerald-500 text-white': newChatType === 'direct',
                      'border-slate-600 text-slate-300 hover:border-slate-500': newChatType !== 'direct'
                    }"
                    class="border rounded-lg p-3"
                  >
                    <User class="w-5 h-5 mx-auto mb-1" />
                    <span class="text-sm">Direct</span>
                  </button>
                  <button
                    @click="newChatType = 'group'"
                    :class="{
                      'bg-emerald-500/20 border-emerald-500 text-white': newChatType === 'group',
                      'border-slate-600 text-slate-300 hover:border-slate-500': newChatType !== 'group'
                    }"
                    class="border rounded-lg p-3"
                  >
                    <Users class="w-5 h-5 mx-auto mb-1" />
                    <span class="text-sm">Group</span>
                  </button>
                </div>
              </div>

              <!-- User Search -->
              <div class="mb-6">
                <label class="block text-sm font-medium text-slate-300 mb-3">Select Users</label>
                <input
                  v-model="searchQuery"
                  type="text"
                  placeholder="Search users..."
                  class="w-full px-3 py-2 border rounded-lg text-sm text-slate-300 focus:border-emerald-500 focus:outline-none"
                />

                <!-- Search Results -->
                <div v-if="searchResults.length > 0" class="mt-2 max-h-32 overflow-y-auto border border-slate-600 rounded-lg bg-slate-700/50">
                  <div
                    v-for="user in searchResults"
                    :key="user.id"
                    @click="selectUser(user)"
                    class="flex items-center space-x-3 p-2 hover:bg-slate-600 cursor-pointer"
                  >
                    <div class="w-8 h-8 rounded-full bg-slate-600 flex items-center justify-center text-xs font-medium text-white">
                      {{ user.name?.charAt(0) || 'U' }}
                    </div>
                    <div>
                      <p class="text-sm text-white">{{ user.name }}</p>
                      <p class="text-xs text-slate-400">{{ user.email }}</p>
                    </div>
                  </div>
                </div>

                <!-- Selected Users -->
                <div v-if="selectedUsers.length > 0" class="mt-3 flex flex-wrap gap-2">
                  <span
                    v-for="user in selectedUsers"
                    :key="user.id"
                    class="inline-flex items-center px-3 py-1 rounded-full text-sm bg-emerald-500/20 text-emerald-300"
                  >
                    {{ user.name }}
                    <button @click="removeUser(user)" class="ml-2 text-emerald-300 hover:text-white">×</button>
                  </span>
                </div>
              </div>

              <!-- Group Name -->
              <div v-if="newChatType === 'group'" class="mb-6">
                <label class="block text-sm font-medium text-slate-300 mb-3">Group Name</label>
                <input
                  v-model="newGroupName"
                  type="text"
                  placeholder="Enter group name"
                  class="w-full px-3 py-2 border rounded-lg text-sm text-slate-300 focus:border-emerald-500 focus:outline-none"
                />
              </div>

              <!-- Actions -->
              <div class="flex justify-end space-x-4">
                <button
                  @click="showCreateChatModal = false"
                  class="px-4 py-2 text-slate-300 hover:text-white"
                >
                  Cancel
                </button>
                <button
                  @click="newChatType === 'direct' ? createNewChat() : createNewGroup()"
                  :disabled="!canCreateChat"
                  class="px-6 py-2 bg-emerald-500 text-white rounded-lg hover:bg-emerald-600 disabled:opacity-50"
                >
                  Create {{ newChatType === 'group' ? 'Group' : 'Chat' }}
                </button>
              </div>
            </div>
          </div>

          <!-- Search and Filters -->
          <div class="p-4 border-b border-slate-700 space-y-3">
            <div class="relative">
              <Search class="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-slate-400" />
              <input
                v-model="searchQuery"
                type="text"
                placeholder="Search conversations..."
                class="w-full rounded-lg border border-slate-600 bg-slate-700 py-2 pl-10 pr-4 text-sm text-white placeholder:text-slate-400 focus:border-emerald-500 focus:outline-none"
              />
            </div>
          </div>

          <!-- Conversations List -->
          <div class="overflow-y-auto flex-1">
            <div
              v-for="chat in chatStore.chats"
              :key="chat.id"
              @click="chatStore.fetchChat(chat.id)"
              class="p-4 hover:bg-slate-700/50 cursor-pointer border-b border-slate-700/50"
              :class="{ 'bg-slate-700/50': chat.id === chatStore.currentChat?.id }"
            >
              <!-- Avatar -->
              <div class="flex items-center space-x-3">
                <div class="relative">
                  <div v-if="!chat.isGroup" class="w-10 h-10 rounded-full bg-emerald-500 flex items-center justify-center">
                    <span class="text-white font-medium">{{ getInitials(chat.participants[0]) }}</span>
                  </div>
                  <div v-else class="w-10 h-10 rounded-full bg-blue-500 flex items-center justify-center">
                    <Users class="w-6 h-6 text-white" />
                  </div>
                  <div
                    v-if="!chat.isGroup && chatStore.isUserOnline(chat.participants[0].id)"
                    class="absolute -bottom-1 -right-1 w-3 h-3 bg-green-400 rounded-full border-2 border-slate-800"
                  ></div>
                </div>

                <!-- Chat Info -->
                <div class="flex-1 min-w-0">
                  <div class="flex justify-between items-start">
                    <h3 class="text-sm font-medium text-white truncate">
                      {{ chat.isGroup ? chat.name : chat.participants[0].name }}
                    </h3>
                    <span class="text-xs text-slate-400">{{ formatTime(chat.lastActivity) }}</span>
                  </div>
                  <p class="text-xs text-slate-400 truncate mt-1">
                    <span v-if="chat.lastMessage?.sender.id === currentUser?.id" class="text-emerald-500">You: </span>
                    {{ chat.lastMessage?.text || 'No messages yet' }}
                  </p>
                </div>

                <!-- Unread Badge -->
                <div
                  v-if="chat.unreadCount > 0"
                  class="flex-shrink-0 w-5 h-5 bg-emerald-500 text-white text-xs rounded-full flex items-center justify-center"
                >
                  {{ chat.unreadCount }}
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
                <span class="text-white font-medium">{{ getInitials(chatStore.currentChat.participants[0]) }}</span>
              </div>
              <div v-else class="w-10 h-10 rounded-full bg-blue-500 flex items-center justify-center">
                <Users class="w-6 h-6 text-white" />
              </div>
              <div>
                <h3 class="text-white font-medium">
                  {{ isCurrentChatGroup ? chatStore.currentChat.name : chatStore.currentChat.participants[0].name }}
                </h3>
                <p class="text-sm text-slate-400">
                  <span v-if="isCurrentChatGroup">
                    {{ onlineParticipants.length }} online • {{ chatStore.currentChat.participants.length }} members
                  </span>
                  <span v-else>
                    {{ chatStore.isUserOnline(chatStore.currentChat.participants[0].id) ? 'Online' : `Last seen ${formatLastSeen(chatStore.currentChat.participants[0].lastSeen)}` }}
                  </span>
                </p>
              </div>
            </div>
          </div>

          <!-- Messages -->
          <div class="flex-1 overflow-y-auto p-4 space-y-6">
            <div
              v-for="(message, index) in chatStore.messages"
              :key="message.id"
              class="flex flex-col"
              :class="message.sender.id === currentUser?.id ? 'items-end' : 'items-start'"
            >
              <!-- Date Separator -->
              <div v-if="showDateSeparator(message, index)" class="flex justify-center my-2">
                <div class="px-4 py-1 rounded-full bg-slate-700/30 text-sm text-slate-400">
                  {{ formatMessageDate(message.createdAt) }}
                </div>
              </div>

              <!-- Message Bubble -->
              <div
                class="group relative max-w-[70%] rounded-lg px-4 py-2"
                :class="message.sender.id === currentUser?.id ? 'bg-emerald-500 text-white' : 'bg-slate-700/50 text-white'"
              >
                <p>{{ message.text }}</p>
                <div class="flex items-center justify-end space-x-1 mt-1">
                  <span class="text-xs opacity-75">{{ formatMessageTime(message.createdAt) }}</span>
                  <Check
                    v-if="message.sender.id === currentUser?.id"
                    class="w-4 h-4"
                    :class="message.isRead ? 'text-blue-400' : 'opacity-75'"
                  />
                </div>
              </div>
            </div>
          </div>

          <!-- Message Input -->
          <div class="p-4 border-t border-slate-700">
            <div class="flex items-end space-x-3">
              <button @click="fileInput?.click()" class="p-2 text-slate-400 hover:text-white">
                <Plus class="w-5 h-5" />
              </button>
              <input ref="fileInput" type="file" class="hidden" @change="handleFileUpload" />

              <textarea
                v-model="newMessage"
                rows="1"
                placeholder="Type a message..."
                class="flex-1 px-4 py-3 bg-slate-700/50 border border-slate-600 rounded-lg text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-emerald-500 resize-none"
                @keydown.enter.prevent="sendMessage"
              ></textarea>

              <button
                @click="sendMessage"
                class="p-3 bg-emerald-500 text-white rounded-lg hover:bg-emerald-600"
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