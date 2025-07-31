// src/types/chatParticipant.ts
export interface ChatParticipant {
  id: number;
  name: string;
  avatarUrl?: string;
  online: boolean;
  lastSeen?: string;
  initials?: string;
  status?: 'online' | 'offline' | 'away' | 'busy';
}