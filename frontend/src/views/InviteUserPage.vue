<template>
    <div class="max-w-3xl mx-auto px-4 sm:px-6 py-8">
      <div class="mb-8">
        <h1 class="text-3xl font-bold text-white">Invite New User</h1>
        <p class="text-slate-400 mt-2">Send invitation emails to new team members</p>
      </div>
  
      <!-- Invitation Form -->
      <div class="bg-slate-800/50 backdrop-blur-sm rounded-lg border border-slate-700 p-6">
        <form @submit.prevent="handleSubmit" class="space-y-6">
          <!-- Email Input -->
          <div>
            <label for="email" class="block text-sm font-medium text-slate-300 mb-2">
              Email Address
            </label>
            <div class="relative">
              <Mail class="absolute left-3 top-1/2 transform -translate-y-1/2 text-slate-400 w-5 h-5" />
              <input
                id="email"
                v-model="formData.email"
                type="email"
                required
                class="w-full pl-10 pr-4 py-3 bg-slate-700/50 border border-slate-600 rounded-lg text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-emerald-500"
                placeholder="Enter email address"
              />
            </div>
          </div>
  
          <!-- Role Selection -->
          <div>
            <label class="block text-sm font-medium text-slate-300 mb-2">
              User Role
            </label>
            <div class="grid grid-cols-2 gap-4">
              <button
                type="button"
                @click="formData.role = UserRole.CONTRIBUTOR"
                class="p-4 border rounded-lg text-left"
                :class="formData.role === UserRole.CONTRIBUTOR 
                  ? 'border-emerald-500 bg-emerald-500/10 text-white' 
                  : 'border-slate-600 bg-slate-700/50 text-slate-300 hover:border-slate-500'"
              >
                <div class="flex items-center mb-2">
                  <Edit3 class="w-5 h-5 mr-2" />
                  <span class="font-medium">Contributor</span>
                </div>
                <p class="text-sm text-slate-400">Can create and publish content, participate in discussions</p>
              </button>
  
              <button
                type="button"
                @click="formData.role = UserRole.USER"
                class="p-4 border rounded-lg text-left"
                :class="formData.role === UserRole.USER 
                  ? 'border-emerald-500 bg-emerald-500/10 text-white' 
                  : 'border-slate-600 bg-slate-700/50 text-slate-300 hover:border-slate-500'"
              >
                <div class="flex items-center mb-2">
                  <User class="w-5 h-5 mr-2" />
                  <span class="font-medium">User</span>
                </div>
                <p class="text-sm text-slate-400">Can view content and participate in discussions</p>
              </button>
            </div>
          </div>
  
          <!-- Custom Message -->
          <div>
            <label class="block text-sm font-medium text-slate-300 mb-2">
              Personal Message (Optional)
            </label>
            <textarea
              v-model="formData.message"
              rows="3"
              class="w-full px-4 py-3 bg-slate-700/50 border border-slate-600 rounded-lg text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-emerald-500"
              placeholder="Add a personal message to the invitation email"
            ></textarea>
          </div>
  
          <!-- Submit Button -->
          <button
            type="submit"
            :disabled="isSubmitting"
            class="w-full flex items-center justify-center px-4 py-3 border border-transparent rounded-lg shadow-sm text-sm font-medium text-white bg-emerald-500 hover:bg-emerald-600 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-emerald-500 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <Send class="w-5 h-5 mr-2" />
            Send Invitation
          </button>
        </form>
      </div>
  
      <!-- Recent Invitations -->
      <div class="mt-8">
        <h2 class="text-xl font-semibold text-white mb-4">Recent Invitations</h2>
        <div class="bg-slate-800/50 backdrop-blur-sm rounded-lg border border-slate-700 divide-y divide-slate-700">
          <div v-for="invitation in recentInvitations" :key="invitation.id" class="p-4">
            <div class="flex items-center justify-between">
              <div class="flex items-center space-x-3">
                <div class="w-10 h-10 rounded-full bg-slate-700 flex items-center justify-center">
                  <User class="w-5 h-5 text-slate-400" />
                </div>
                <div>
                  <p class="text-white">{{ invitation.email }}</p>
                  <p class="text-sm text-slate-400">
                    <span class="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium"
                      :class="getStatusLabel(invitation.status)"
                    >
                      {{ invitation.status.toLocaleLowerCase() }}
                    </span>
                    · Invited as {{ invitation.role.toLocaleLowerCase() }}
                  </p>
                </div>
              </div>
              <button 
                v-if="invitation.status === InvitationStatus.PENDING"
                @click="resendInvitation(invitation.id)"
                :disabled="loadingInvitations.has(invitation.id)"
                :class="[
                  loadingInvitations.has(invitation.id)
                  ? 'text-slate-400 cursor-wait'
                  : 'text-sm text-emerald-500 hover:text-emerald-400'
                ]"
              >
              <span v-if="loadingInvitations.has(invitation.id)">
                Resending...
              </span>
              <span v-else> Resend </span>  
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script setup lang="ts">
  import { ref, reactive, onMounted, computed } from 'vue';
  import { Mail, User, Edit3, Send } from 'lucide-vue-next';
  import { useNotificationStore } from '../stores/notification';
  import { useInvitationStore } from '../stores/invitation';
  import { UserRole } from '../types/UserRole';
  import { InvitationStatus } from '../types/invitation/InvitationStatus';
  
  const notificationStore = useNotificationStore();
  const invitationStore = useInvitationStore();

  const recentInvitations = computed(() => invitationStore.recentInvitation);

  const isSubmitting = ref(false);
  const loadingInvitations = ref(new Set<number>());
  
  const formData = reactive({
    email: '',
    role: UserRole.USER,
    message: ''
  });
  
  // Fetch recent invitations when the component mounts
  onMounted(() =>{
    invitationStore.fetchRecentInvitations();
  })

  // Handle form submission
  const handleSubmit = async () => {
    isSubmitting.value = true;
    
    try{
        const success = await invitationStore.sendInvitation(
            formData.email,
            formData.role,
            formData.message
        );

        if (success) {
            notificationStore.addNotification({
                type: 'system',
                message: `Invitation sent to ${formData.email}`,
                link: '/invite-user',
            });

            // Reset form
            formData.email = '';
            formData.message = '';
            formData.role = UserRole.USER;
        }else {
            throw new Error('Failed to send invitation');
        }
    }catch (error) {
        notificationStore.addNotification({
        type: 'system',
        message: 'Failed to send invitation. Please try again.',
        link: '/invite-user',
        });
    }finally {
        isSubmitting.value = false;
    }
  };

  // Resend invitation
  const resendInvitation = async (id: number) => {
    if (loadingInvitations.value.has(id)) return; // Prevent duplicate requests

    loadingInvitations.value.add(id); // Mark as loading

    try {
        const success = await invitationStore.resendInvitation(id);

        if (success) {
            notificationStore.addNotification({
                type: 'system',
                message: 'Invitation resent successfully',
                link: '/invite-user',
            }) 
        } else {
            throw new Error('Failed to resend invitation');
        }
    }catch (error){
        notificationStore.addNotification({
        type: 'system',
        message: 'Failed to resend invitation. Please try again.',
        link: '/invite-user',
        });
    }finally {
      setTimeout(() => {
        loadingInvitations.value.delete(id); // Remove loading state after 2 seconds
      }, 2000);
    }
  };
  const getStatusLabel = (status: string) => {
    switch (status) {
      case InvitationStatus.PENDING:
        return 'bg-amber-500/10 text-amber-500';
      case InvitationStatus.ACCEPTED:
        return 'bg-emerald-500/10 text-emerald-500';
      case InvitationStatus.EXPIRED:
        return 'bg-red-500/10 text-red-500';
      default:
        return 'bg-slate-500/10 text-slate-500';
    }
  };
  </script>