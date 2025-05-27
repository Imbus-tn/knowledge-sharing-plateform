import { defineStore } from 'pinia';
import axios from 'axios';

export interface Notification {
  id: string;
  type: 'post' | 'reaction' | 'comment' | 'favorite' | 'system';
  message: string;
  read: boolean;
  createdAt: string;
  link: string;
  user?: {
    name: string;
    initials: string;
    avatar?: string;
  };
  postId?: string;
  postTitle?: string;
  userId?: string; // To identify if the post belongs to current user
}

interface NotificationState {
  notifications: Notification[];
  unreadCount: number;
  showNotificationPanel: boolean;
  currentUserId: string | null;
}

export const useNotificationStore = defineStore('notification', {
  state: (): NotificationState => ({
    notifications: [],
    unreadCount: 0,
    showNotificationPanel: false,
    currentUserId: null
  }),

  actions: {
    setCurrentUser(userId: string) {
      this.currentUserId = userId;
    },

    async fetchNotifications() {
      try {
        const res = await axios.get('/api/notifications');
        this.notifications = res.data.map((n: any) => ({
          ...n,
          createdAt: n.createdAt || new Date().toISOString(),
          read: n.read || false
        }));
        this.calculateUnreadCount();
      } catch (err) {
        console.error('Failed to fetch notifications:', err);
      }
    },

    addNotification(notification: Omit<Notification, 'id' | 'createdAt' | 'read'>) {
      // Skip notifications not in allowed types
      if (!['post', 'reaction', 'comment', 'favorite', 'system'].includes(notification.type)) return;

      // Filter post notifications: only show others' posts
      if (notification.type === 'post' && notification.userId === this.currentUserId) return;

      // Filter reactions, comments, and favorites: only show if on current user's post
      if (['reaction', 'comment', 'favorite'].includes(notification.type) && notification.userId !== this.currentUserId) return;

      const newNotification: Notification = {
        id: Date.now().toString(),
        createdAt: new Date().toISOString(),
        read: false,
        ...notification
      };

      this.notifications.unshift(newNotification);
      this.calculateUnreadCount();
    },

    markAsRead(id: string) {
      const notification = this.notifications.find(n => n.id === id);
      if (notification && !notification.read) {
        notification.read = true;
        this.calculateUnreadCount();
      }
    },

    markAllAsRead() {
      this.notifications.forEach(n => n.read = true);
      this.calculateUnreadCount();
    },

    removeNotification(id: string) {
      this.notifications = this.notifications.filter(n => n.id !== id);
      this.calculateUnreadCount();
    },

    calculateUnreadCount() {
      this.unreadCount = this.notifications.filter(n => !n.read).length;
    },

    toggleNotificationPanel() {
      this.showNotificationPanel = !this.showNotificationPanel;
    },

    closeNotificationPanel() {
      this.showNotificationPanel = false;
    },

    showBrowserNotification(notification: Notification) {
      if ('Notification' in window && Notification.permission === 'granted') {
        const title = notification.user ? notification.user.name : 'New Notification';
        new Notification(title, {
          body: notification.message,
          icon: '/vite.svg'
        });
      }
    }
  }
});