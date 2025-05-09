import { UserRole } from "../UserRole";
import { InvitationStatus } from "./InvitationStatus";

export interface InvitationDTO {
    id: number; 
    email: string; 
    role: UserRole; 
    used: boolean;
    expiresAt: string; 
    status: InvitationStatus;
  }
  
