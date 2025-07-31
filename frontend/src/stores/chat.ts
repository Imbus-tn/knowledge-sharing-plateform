import { defineStore } from 'pinia';
import { chatApi } from '../api/chat';
import { getSocketService } from '../services/socket.service';

import type {
  Chat,
  Message,
  Reaction,
  MessagePreview,
  User,
  ReactionRequest,
  SendMessageRequest
} from '../types/chat';

const socketService = getSocketService();

interface ChatState {
  chats: Chat[];
  currentChat: Chat | null;
  messages: Message[];
  unreadCount: number;
  onlineUsers: Set<number>;
  showDiscussionPanel: boolean;
  socketsInitialized: boolean;
}

export const useChatStore = defineStore('chat', {
  state: (): ChatState => ({
    chats: [],
    currentChat: null,
    messages: [],
    unreadCount: 0,
    onlineUsers: new Set<number>(),
    showDiscussionPanel: false,
    socketsInitialized: false
  }),

  actions: {
    async fetchChats() {
      this.chats = await chatApi.getAllChats();
      this.calculateUnreadCount();

      // Only initialize socket connection once
      if (!this.socketsInitialized) {
        socketService.connect();
        this.setupSocketListeners();
        this.socketsInitialized = true;
      }
    },

    async fetchChat(chatId: number) {
      this.currentChat = await chatApi.getChatById(chatId);
      this.messages = await chatApi.getChatMessages(chatId);
      await chatApi.markAsRead(chatId);
      this.calculateUnreadCount();
    },

    async createGroupChat(name: string, participantIds: number[]) {
      const chat = await chatApi.createGroupChat({ name, participantIds });
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

    addMessage(chatId: number, message: Omit<Message, 'id' | 'createdAt' | 'isRead'>) {
      const chat = this.chats.find((c: Chat) => c.id === chatId);
      if (!chat) return;

      const newMessage: Message = {
        ...message,
        id: Date.now(),
        createdAt: new Date().toISOString(),
        isRead: message.isSent || false
      };

      if (!chat.messages) {
        chat.messages = [];
      }

      chat.messages.push(newMessage);
      chat.lastActivity = newMessage.createdAt;
      chat.lastMessage = {
        id: newMessage.id,
        text: newMessage.text,
        sender: newMessage.sender,
        createdAt: newMessage.createdAt,
        isRead: newMessage.isRead
      };

      if (!newMessage.isSent) {
        chat.unreadCount++;
        this.calculateUnreadCount();
      }
    },

    calculateUnreadCount() {
      this.unreadCount = this.chats.reduce((sum, chat) => sum + chat.unreadCount, 0);
    },

    markChatAsRead(chatId: number) {
      const chat = this.chats.find((c: Chat) => c.id === chatId);
      if (!chat) return;

      chat.messages?.forEach(message => {
        if (!message.isRead && !message.isSent) {
          message.isRead = true;
        }
      });

      chat.unreadCount = 0;
      this.calculateUnreadCount();
    },

    setupSocketListeners() {
      socketService.subscribe('/topic/new-message', (incoming: any) => {
        const message: Omit<Message, 'id' | 'createdAt' | 'isRead'> = {
          ...incoming,
          text: incoming.content,
          sender: incoming.sender,
          chatId: incoming.chatId,
          isSent: false,
          reactions: []
        };

        this.addMessage(message.chatId, message);
      });

      socketService.subscribe('/topic/reaction', (incoming: any) => {
        const reaction: Reaction = {
          ...incoming,
          messageId: incoming.messageId,
          createdAt: incoming.createdAt || new Date().toISOString()
        };

        const message = this.messages.find((m: Message) => m.id === reaction.messageId);
        if (message) {
          if (!message.reactions) {
            message.reactions = [];
          }
          message.reactions.push(reaction);
        }
      });

      socketService.subscribe('/topic/presence', (update: { userId: number; online: boolean }) => {
        if (update.online) {
          this.onlineUsers.add(update.userId);
        } else {
          this.onlineUsers.delete(update.userId);
        }

        this.chats.forEach((chat: Chat) => {
          chat.participants.forEach((p: User) => {
            if (p.id === update.userId) {
              p.online = update.online;
              if (!update.online) {
                p.lastSeen = new Date().toISOString();
              }
            }
          });
        });
      });
    }
  },

  getters: {
    latestMessages: (state: ChatState) => {
      return state.chats
        .map((chat: Chat) => {
          const lastMessage = chat.messages?.[chat.messages.length - 1];
          if (!lastMessage) return null;
          return {
            id: lastMessage.id,
            chatId: chat.id,
            sender: lastMessage.sender,
            preview: lastMessage.text.length > 30
              ? lastMessage.text.substring(0, 30) + '...'
              : lastMessage.text,
            time: lastMessage.createdAt,
            unread: !lastMessage.isRead && !lastMessage.isSent
          };
        })
        .filter(Boolean);
    },

    getChatById: (state) => (id: number) => {
      return state.chats.find((chat: Chat) => chat.id === id);
    },

    isUserOnline: (state) => (userId: number) => {
      return state.onlineUsers.has(userId);
    },

    getParticipantStatus: (state) => (userId: number) => {
      if (state.onlineUsers.has(userId)) return 'online';

      const chat = state.currentChat;
      if (!chat) return 'offline';

      const participant = chat.participants.find((p: User) => p.id === userId);
      return participant?.lastSeen
        ? `last seen ${new Date(participant.lastSeen).toLocaleTimeString()}`
        : 'offline';
    }
  }
});
