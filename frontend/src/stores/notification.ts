import { defineStore } from 'pinia';

export interface Notification {
  id: string;
  type: 'post' | 'comment' | 'reaction' | 'mention' | 'system';
  message: string;
  read: boolean;
  createdAt: string;
  link: string;
  user?: {
    name: string;
    initials: string;
    avatar?: string;
  };
}

interface NotificationState {
  notifications: Notification[];
  unreadCount: number;
  showNotificationPanel: boolean;
}

export const useNotificationStore = defineStore('notification', {
  state: (): NotificationState => ({
    notifications: [],
    unreadCount: 0,
    showNotificationPanel: false
  }),

  actions: {
    addNotification(notification: Omit<Notification, 'id' | 'createdAt' | 'read'>) {
      const newNotification: Notification = {
        id: Date.now().toString(),
        createdAt: new Date().toISOString(),
        read: false,
        ...notification
      };
      
      this.notifications.unshift(newNotification);
      this.calculateUnreadCount();
      
      // Show browser notification if supported
      this.showBrowserNotification(newNotification);
    },
    
    markAsRead(id: string) {
      const notification = this.notifications.find(n => n.id === id);
      if (notification) {
        notification.read = true;
        this.calculateUnreadCount();
      }
    },
    
    markAllAsRead() {
      this.notifications.forEach(notification => {
        notification.read = true;
      });
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
        new Notification('Imbus Knowledge', {
          body: notification.message,
          icon: '/vite.svg' // Replace with your app icon
        });
      }
    },
    
    // Mock method to generate sample notifications for testing
    generateMockNotifications() {
      const mockNotifications: Omit<Notification, 'id' | 'createdAt' | 'read'>[] = [
        {
          type: 'post',
          message: 'Sarah Kim published a new article: "Building Scalable APIs with GraphQL"',
          link: '/feed',
          user: {
            name: 'Sarah Kim',
            initials: 'SK'
          }
        },
        {
          type: 'comment',
          message: 'Mike Johnson commented on your article "Vue.js Best Practices 2024"',
          link: '/feed',
          user: {
            name: 'Mike Johnson',
            initials: 'MJ'
          }
        },
        {
          type: 'reaction',
          message: 'Alex Chen liked your comment on "Kubernetes Deployment Strategies"',
          link: '/discussions',
          user: {
            name: 'Alex Chen',
            initials: 'AC'
          }
        },
        {
          type: 'mention',
          message: 'You were mentioned in a discussion about Docker networking',
          link: '/discussions',
          user: {
            name: 'Lisa Wong',
            initials: 'LW'
          }
        },
        {
          type: 'system',
          message: 'Your account role has been updated to Contributor',
          link: '/profile'
        }
      ];
      
      // Add mock notifications with different timestamps
      mockNotifications.forEach((notification, index) => {
        const date = new Date();
        date.setMinutes(date.getMinutes() - (index * 30)); // Spread out the timestamps
        
        this.addNotification(notification);
        
        // Mark some as read
        if (index > 2) {
          this.markAsRead(this.notifications[0].id);
        }
      });
    }
  }
});