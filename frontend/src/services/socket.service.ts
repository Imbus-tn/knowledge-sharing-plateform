import { Client } from '@stomp/stompjs';
import { useAuthStore } from '../stores/auth';

class SocketService {
  private client: Client;
  private subscriptions: Map<string, (payload: any) => void> = new Map();
  
  constructor() {
    this.client = new Client({
      brokerURL: import.meta.env.VITE_WS_URL || 'ws://localhost:8080/ws',
      connectHeaders: this.getAuthHeaders(),
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      debug: (str) => console.log('[WS]', str),
      onConnect: () => {
        this.subscriptions.forEach((callback, topic) => {
          this.subscribe(topic, callback);
        });
      },
      onStompError: (frame) => {
        console.error('WebSocket error:', frame.headers.message);
      }
    });
  }

  private getAuthHeaders() {
    const authStore = useAuthStore();
    return {
      Authorization: `Bearer ${authStore.accessToken?.valueOf || ''}`
    };
  }

  connect() {
    if (!this.client.active) {
      this.client.activate();
    }
  }

  disconnect() {
    if (this.client.active) {
      this.client.deactivate();
    }
    this.subscriptions.clear();
  }

  subscribe(topic: string, callback: (payload: any) => void) {
    const sub = this.client.subscribe(topic, (message) => {
      callback(JSON.parse(message.body));
    });
    this.subscriptions.set(topic, callback);
    return sub;
  }

  send(destination: string, body: any) {
    if (!this.client.connected) {
      throw new Error('WebSocket not connected');
    }
    this.client.publish({
      destination,
      body: JSON.stringify(body),
      headers: this.getAuthHeaders()
    });
  }
}

export const socketService = new SocketService();