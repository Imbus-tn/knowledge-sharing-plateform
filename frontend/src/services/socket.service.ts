
import { useAuthStore } from '../stores/auth';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import type { Message } from '@stomp/stompjs'; 
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

 connect(onConnectedCallback?: () => void) {
    const authStore = useAuthStore();
    const token = authStore.accessToken;

    if (!token) {
      console.warn("No access token available");
      return;
    }
     //  Prevent duplicate activation
  if (this.client?.active) {
    console.log('[WS] Already connected or connecting');
    return;
  }
    this.client = new Client({
      //  Removed brokerURL: null
      webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
      connectHeaders: {
        Authorization: `Bearer ${token}`
      },
      debug: (str: string) => console.log('[WS] ' + str),
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      onConnect: () => {
        console.log('[WS] Connected');
        this.resubscribe();
        if (onConnectedCallback) onConnectedCallback();
      },
      onStompError: (frame) => {
        console.error('[WS] Error:', frame);
      }
    });

    this.client.activate();
  }
  private resubscribe() {
    const topics = Array.from(this.subscriptions.keys());
    this.subscriptions.clear();
    topics.forEach(topic => {
      const callback = this.subscriptions.get(topic);
      if (callback) {
        this.subscribe(topic, callback);
      }
    });
  }

  disconnect() {
    if (this.client && this.client.active) {
      this.client.deactivate();
       this.client = null;
       this.subscriptions.clear();
    }
   
  }

subscribe(topic: string, callback: (payload: any) => void) {
    if (!this.client) {
      console.warn('WebSocket not connected. Storing subscription for later.');
      this.subscriptions.set(topic, callback);
      return;
    }

    const sub = this.client.subscribe(topic, (message: Message) => {
      try {
        callback(JSON.parse(message.body));
      } catch (e) {
        console.error('Failed to parse WebSocket message', message.body);
      }
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
      body: JSON.stringify(body)
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

