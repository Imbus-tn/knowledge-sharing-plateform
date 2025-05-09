import { UserRole } from "./UserRole";

export interface User {
    id: number;
    name: string;
    email: string;
    role: UserRole;
    bio?: string;
    location?: string;
    phoneNumber?: string;
    github?: string;
    linkedin?: string;
    avatarUrl?: string; 
  }