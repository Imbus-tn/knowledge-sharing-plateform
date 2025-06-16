import { defineStore } from 'pinia';
import { chatApi } from '../api/chat';
import { socketService } from '../services/socket.service';
import type { 
  Chat, 
  Message, 
  Reaction,
  SendMessageRequest,
  ReactionRequest,
  MessagePreview
} from '../types/chat';

export const useChatStore = defineStore('chat', {
  state: () => ({
    chats: [] as Chat[],
    currentChat: null as Chat | null,
    messages: [] as Message[],
    unreadCount: 0,
    onlineUsers: new Set<number>()
  }),

  actions: {
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
      this.chats = this.chats.filter((c: Chat) => c.id !== chatId);
      if (this.currentChat?.id === chatId) {
        this.currentChat = null;
      }
    },

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
      this.messages = this.messages.filter((m: Message) => m.id !== messageId);
    },

    async toggleReaction(message: Message, emoji: string) {
      if (!this.currentChat) return;

      const request: ReactionRequest = {
        messageId: message.id,
        emoji
      };

      await chatApi.toggleReaction(this.currentChat.id, request);
    },

    setupSocketListeners() {
      socketService.subscribe('/topic/new-message', (incomingMessage: any) => {
        const message: Message = {
          ...incomingMessage,
          chatId: incomingMessage.chatId || this.currentChat?.id || 0,
          isRead: incomingMessage.isRead || false,
          reactions: incomingMessage.reactions || [],
          isSent: incomingMessage.isSent || true,
          isForwarded: incomingMessage.isForwarded || false
        };

        const chat = this.chats.find((c: Chat) => c.id === message.chatId);
        if (chat) {
          const lastMessage: MessagePreview = {
            id: message.id,
            text: message.text,
            sender: message.sender,
            createdAt: message.createdAt,
            isRead: message.isRead
          };
          
          chat.lastMessage = lastMessage;
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

      socketService.subscribe('/topic/reaction', (incomingReaction: any) => {
        const reaction: Reaction = {
          ...incomingReaction,
          messageId: incomingReaction.messageId || 0,
          createdAt: incomingReaction.createdAt || new Date().toISOString()
        };

        const message = this.messages.find((m: Message) => m.id === reaction.messageId);
        if (message) {
          if (!message.reactions) {
            message.reactions = [];
          }
          
          message.reactions = message.reactions.filter(
            (r: Reaction) => !(r.user.id === reaction.user.id && r.emoji === reaction.emoji)
          );
          message.reactions.push(reaction);
        }
      });

      socketService.subscribe('/topic/presence', (update: { userId: number, online: boolean }) => {
        if (update.online) {
          this.onlineUsers.add(update.userId);
        } else {
          this.onlineUsers.delete(update.userId);
        }
        
        this.chats.forEach((chat: Chat) => {
          chat.participants.forEach((p) => {
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

    calculateUnreadCount() {
      this.unreadCount = this.chats.reduce((sum: number, chat: Chat) => sum + chat.unreadCount, 0);
    },

    isUserOnline(userId: number): boolean {
      return this.onlineUsers.has(userId);
    }
  },

  getters: {
    getChatById: (state) => (id: number) => {
      return state.chats.find((chat: Chat) => chat.id === id);
    },
    
    getParticipantStatus: (state) => (userId: number) => {
      if (state.onlineUsers.has(userId)) {
        return 'online';
      }
      
      const chat = state.currentChat;
      if (!chat) return 'offline';
      
      const participant = chat.participants.find((p) => p.id === userId);
      return participant?.lastSeen ? `last seen ${formatLastSeen(participant.lastSeen)}` : 'offline';
    }
  }
});

function formatLastSeen(timestamp: string): string {
  return new Date(timestamp).toLocaleTimeString();
}