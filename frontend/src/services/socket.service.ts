import { Client } from '@stomp/stompjs';
import { useAuthStore } from '../stores/auth';
import SockJS from 'sockjs-client';
class SocketService {
  private client: Client | null = null;
  private subscriptions: Map<string, (payload: any) => void> = new Map();

  constructor() {
    // Don't create the client here. Create it lazily in connect()
  }

  private getAuthHeaders() {
    const authStore = useAuthStore();
    return {
      Authorization: `Bearer ${authStore.accessToken?.valueOf() || ''}`
    };
  }

  connect() {
    if (this.client && this.client.active) return;

    // Create client only when connect() is called (after Pinia is ready)
  this.client = new Client({
  webSocketFactory: () => new SockJS(import.meta.env.VITE_WS_URL || 'http://localhost:8080/ws'),
      connectHeaders: this.getAuthHeaders(), // call here, not before
      debug: (str) => console.log('[WS]', str),
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      
      onConnect: () => {
        this.subscriptions.forEach((callback, topic) => {
          this.subscribe(topic, callback);
        });
      },
      onStompError: (frame) => {
        console.error('WebSocket error:', frame.headers.message);
      }
    });

    this.client.activate();
  }

  disconnect() {
    if (this.client && this.client.active) {
      this.client.deactivate();
       this.subscriptions.clear();
    }
   
  }

  subscribe(topic: string, callback: (payload: any) => void) {
    if (!this.client) {
      throw new Error('WebSocket client is not connected');
    }
    const sub = this.client.subscribe(topic, (message) => {
      callback(JSON.parse(message.body));
    });
    this.subscriptions.set(topic, callback);
    return sub;
  }

  send(destination: string, body: any) {
    if (!this.client || !this.client.connected) {
      throw new Error('WebSocket not connected');
    }
    this.client.publish({
      destination,
      body: JSON.stringify(body),
      headers: this.getAuthHeaders()
    });
  }
}

let socketServiceInstance: SocketService | null = null;



export function getSocketService() {
  if (!socketServiceInstance) {
    socketServiceInstance = new SocketService();
  }
  return socketServiceInstance;
}

export const socketService = new SocketService();

