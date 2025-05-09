import { defineStore } from "pinia";
import { apiClient } from "../api";
import type { InvitationDTO } from "../types/invitation/InvitationDTO";
import { UserRole } from "../types/UserRole";
import { ref } from "vue";

export const useInvitationStore = defineStore('invitation', () => {
    const recentInvitation = ref<InvitationDTO[]>([]);

    // Fetch recent invitations from the backend
    const fetchRecentInvitations = async () => {
        try{
            const response = await apiClient.get('/admin/invitations');
            recentInvitation.value = response.data;
        }catch (error) {
            console.error('Failed to fetch invitations:', error);
            throw error;
        }
    }

    // Send an invitation
    const sendInvitation = async (email: string, role: UserRole, message?: string) =>{
        try{
            const response = await apiClient.post('/admin/invite',null, { params: {email, role, message} });
            if (response.status === 200) {
                await fetchRecentInvitations();
                return true;
            }
        }catch (error){
            console.error('Failed to send invitation:', error);
            throw error;
        }
        return false;
    };

    // Resend an invitation
    const resendInvitation = async (id: number) => {
        try{
            const response = await apiClient.post(`/admin/invitations/${id}/resend`);
            if (response.status === 200) {
                await fetchRecentInvitations();
                return true;
            }
        }catch (error) {
            console.error('Failed to resend invitation:', error);
            throw error;
        }
        return false;
    };

    return {
        recentInvitation,
        fetchRecentInvitations,
        sendInvitation,
        resendInvitation,
    };
});