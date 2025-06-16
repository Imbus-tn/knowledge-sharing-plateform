// src/utils/date.ts
export function formatDate(timestamp: string): string {
  const date = new Date(timestamp);
  return date.toLocaleDateString('en-US', { 
    year: 'numeric', 
    month: 'short', 
    day: 'numeric' 
  });
}

export function formatTime(timestamp: string): string {
  const date = new Date(timestamp);
  return date.toLocaleTimeString('en-US', { 
    hour: '2-digit', 
    minute: '2-digit' 
  });
}

export function formatLastSeen(timestamp: string): string {
  const now = new Date();
  const lastSeen = new Date(timestamp);
  const diffInHours = (now.getTime() - lastSeen.getTime()) / (1000 * 60 * 60);
  
  if (diffInHours < 24) {
    return formatTime(timestamp);
  } else {
    return formatDate(timestamp);
  }
}