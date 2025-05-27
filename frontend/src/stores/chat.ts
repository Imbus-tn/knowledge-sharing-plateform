import { defineStore } from 'pinia';
import { chatApi } from '@/api/chat';
import { socketService } from '@/services/socket.service';
import type { Chat, Message, Reaction } from '@/types/chat';

export const useChatStore = defineStore('chat', {
  state: () => ({
    chats: [] as Chat[],
    currentChat: null as Chat | null,
    messages: [] as Message[],
    unreadCount: 0,
    onlineUsers: new Set<number>()
  }),

  actions: {
    // CRUD Operations
    async fetchChats() {
      this.chats = await chatApi.getAllChats();
      this.calculateUnreadCount();
      this.setupSocketListeners();
    },

    async fetchChat(chatId: number) {
      this.currentChat = await chatApi.getChatById(chatId);
      this.messages = await chatApi.getChatMessages(chatId);
      await chatApi.markAsRead(chatId);
      this.calculateUnreadCount();
    },

    async createGroupChat(name: string, participantIds: number[]) {
      const chat = await chatApi.createGroupChat({
        name,
        participantIds
      });
      this.chats.unshift(chat);
      return chat;
    },

    async deleteChat(chatId: number) {
      await chatApi.deleteChat(chatId);
      this.chats = this.chats.filter(c => c.id !== chatId);
      if (this.currentChat?.id === chatId) {
        this.currentChat = null;
      }
    },

    // Message Operations
    async sendMessage(text: string, replyTo?: Message) {
      if (!this.currentChat) return;

      const request: SendMessageRequest = {
        text,
        replyToId: replyTo?.id
      };

      await chatApi.sendMessage(this.currentChat.id, request);
    },

    async sendFile(file: File) {
      if (!this.currentChat) return;

      const { url } = await chatApi.uploadFile(this.currentChat.id, file);
      await chatApi.sendMessage(this.currentChat.id, {
        text: file.name,
        attachmentUrl: url
      });
    },

    async deleteMessage(messageId: number) {
      if (!this.currentChat) return;
      
      await chatApi.deleteMessage(this.currentChat.id, messageId);
      this.messages = this.messages.filter(m => m.id !== messageId);
    },

    // Reaction Operations
    async toggleReaction(message: Message, emoji: string) {
      if (!this.currentChat) return;

      const request: ReactionRequest = {
        messageId: message.id,
        emoji
      };

      await chatApi.toggleReaction(this.currentChat.id, request);
    },

    // WebSocket Handlers
    setupSocketListeners() {
      // New message handler
      socketService.subscribe('/topic/new-message', (message: Message) => {
        const chat = this.chats.find(c => c.id === message.chatId);
        if (chat) {
          chat.lastMessage = message;
          chat.lastActivity = message.createdAt;
          
          if (message.chatId === this.currentChat?.id) {
            this.messages.push(message);
            chat.unreadCount = 0;
          } else {
            chat.unreadCount++;
          }
          
          this.calculateUnreadCount();
        }
      });

      // Reaction handler
      socketService.subscribe('/topic/reaction', (reaction: Reaction) => {
        const message = this.messages.find(m => m.id === reaction.messageId);
        if (message) {
          // Remove existing reaction from this user
          message.reactions = message.reactions.filter(
            r => !(r.user.id === reaction.user.id && r.emoji === reaction.emoji)
          );
          // Add new reaction
          message.reactions.push(reaction);
        }
      });

      // User presence handler
      socketService.subscribe('/topic/presence', (update: { userId: number, online: boolean }) => {
        if (update.online) {
          this.onlineUsers.add(update.userId);
        } else {
          this.onlineUsers.delete(update.userId);
        }
        
        // Update participant status in all chats
        this.chats.forEach(chat => {
          chat.participants.forEach(p => {
            if (p.id === update.userId) {
              p.online = update.online;
              if (!update.online) {
                p.lastSeen = new Date().toISOString();
              }
            }
          });
        });
      });
    },

    // Helper Methods
    calculateUnreadCount() {
      this.unreadCount = this.chats.reduce((sum, chat) => sum + chat.unreadCount, 0);
    },

    isUserOnline(userId: number): boolean {
      return this.onlineUsers.has(userId);
    }
  },

  getters: {
    getChatById: (state) => (id: number) => {
      return state.chats.find(chat => chat.id === id);
    },
    
    getParticipantStatus: (state) => (userId: number) => {
      if (state.onlineUsers.has(userId)) {
        return 'online';
      }
      
      const chat = state.currentChat;
      if (!chat) return 'offline';
      
      const participant = chat.participants.find(p => p.id === userId);
      return participant?.lastSeen ? `last seen ${formatLastSeen(participant.lastSeen)}` : 'offline';
    }
  }
});

// Helper function
function formatLastSeen(timestamp: string): string {
  // Implement your time formatting logic
  return new Date(timestamp).toLocaleString();
}