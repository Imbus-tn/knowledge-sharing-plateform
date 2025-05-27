<template>
  <div class="max-w-3xl mx-auto px-4 sm:px-6 py-8">
    <div class="mb-8">
      <!-- Page Title -->
      <h1 :class="[
        'text-3xl font-bold mb-2 transition-colors duration-200',
        isDark ? 'text-white' : 'text-slate-900'
      ]">
        Invite New User
      </h1>
      <!-- Description -->
      <p :class="isDark ? 'text-slate-400' : 'text-slate-500'">
        Send invitation emails to new team members
      </p>
    </div>

    <!-- Invitation Form -->
    <div :class="[
      'backdrop-blur-sm rounded-lg border p-6 transition-colors duration-200 shadow-xl',
      isDark ? 'bg-slate-800/50 border-slate-700' : 'bg-slate-100/90 border-slate-300'
    ]">
      <form @submit.prevent="handleSubmit" class="space-y-6">
        <!-- Email Input -->
        <div>
          <label 
            for="email" 
            :class="[
              'block text-sm font-medium mb-2 transition-colors duration-200',
              isDark ? 'text-slate-300' : 'text-slate-600'
            ]"
          >
            Email Address
          </label>
          <div class="relative">
            <Mail 
              :class="isDark ? 'text-slate-400' : 'text-slate-500'" 
              class="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5" 
            />
            <input
              id="email"
              v-model="formData.email"
              type="email"
              required
              :class="[
                'w-full pl-10 pr-4 py-3 rounded-lg placeholder-slate-400 focus:outline-none focus:ring-2 transition-colors border',
                isDark 
                  ? 'bg-slate-700/50 border-slate-600 text-white focus:ring-emerald-500' 
                  : 'bg-slate-200/70 border-slate-300 text-slate-900 focus:ring-emerald-600'
              ]"
              placeholder="Enter email address"
            />
          </div>
        </div>

        <!-- Role Selection -->
        <div>
          <label :class="[
            'block text-sm font-medium mb-2 transition-colors duration-200',
            isDark ? 'text-slate-300' : 'text-slate-600'
          ]">
            User Role
          </label>
          <div class="grid grid-cols-3 gap-4">
            <button
              v-for="role in roleOptions"
              :key="role.value"
              type="button"
              @click="formData.role = role.value"
              :class="[
                'p-4 border rounded-lg text-left transition-colors duration-200',
                formData.role === role.value 
                  ? (isDark ? 'border-emerald-500 bg-emerald-500/10 text-white' : 'border-emerald-600 bg-emerald-600/10 text-slate-900') 
                  : (isDark ? 'border-slate-600 bg-slate-700/50 text-slate-300 hover:border-slate-500' : 'border-slate-300 bg-slate-200/70 text-slate-600 hover:border-slate-300')
              ]"
            >
              <div class="flex items-center mb-2">
                <component 
                  :is="role.icon" 
                  :class="isDark ? 'text-emerald-500' : 'text-emerald-600'" 
                  class="w-5 h-5 mr-2"
                />
                <span :class="isDark ? 'text-white' : 'text-slate-900'">{{ role.label }}</span>
              </div>
              <p :class="isDark ? 'text-slate-400' : 'text-slate-500'">{{ role.description }}</p>
            </button>
          </div>
        </div>

        <!-- Custom Message -->
        <div>
          <label :class="[
            'block text-sm font-medium mb-2 transition-colors duration-200',
            isDark ? 'text-slate-300' : 'text-slate-600'
          ]">
            Personal Message (Optional)
          </label>
          <textarea
            v-model="formData.message"
            rows="3"
            :class="[
              'w-full px-4 py-3 rounded-lg placeholder-slate-400 focus:outline-none focus:ring-2 transition-colors border',
              isDark 
                ? 'bg-slate-700/50 border-slate-600 text-white focus:ring-emerald-500' 
                : 'bg-slate-200/70 border-slate-300 text-slate-900 focus:ring-emerald-600'
            ]"
            placeholder="Add a personal message to the invitation email"
          ></textarea>
        </div>

        <!-- Submit Button -->
        <button
          type="submit"
          :disabled="isSubmitting"
          :class="[
            'w-full flex items-center justify-center px-4 py-3 rounded-lg shadow-sm text-sm font-medium transition-colors duration-200',
            'focus:outline-none focus:ring-2 focus:ring-offset-2',
            isDark 
              ? 'bg-emerald-500 text-white hover:bg-emerald-600 focus:ring-emerald-500 disabled:opacity-50 disabled:cursor-not-allowed' 
              : 'bg-emerald-600 text-white hover:bg-emerald-700 focus:ring-emerald-600 disabled:opacity-50 disabled:cursor-not-allowed'
          ]"
        >
          <Send :class="isDark ? 'text-white' : 'text-white'" class="w-5 h-5 mr-2" />
          Send Invitation
        </button>
      </form>
    </div>

    <!-- Recent Invitations -->
    <div class="mt-8">
      <h2 :class="[
        'text-xl font-semibold mb-4 transition-colors duration-200',
        isDark ? 'text-white' : 'text-slate-900'
      ]">
        Recent Invitations
      </h2>
      <div :class="[
        'backdrop-blur-sm rounded-lg border divide-y divide-slate-700 transition-colors duration-200 shadow-xl',
        isDark ? 'bg-slate-800/50 border-slate-700 divide-slate-700' : 'bg-slate-100/90 border-slate-200 divide-slate-200/70'
      ]">
        <div 
          v-for="invitation in recentInvitations" 
          :key="invitation.id" 
          class="p-4"
        >
          <div class="flex items-center justify-between">
            <div class="flex items-center space-x-3">
              <!-- Avatar -->
              <div :class="[
                'w-10 h-10 rounded-full flex items-center justify-center',
                isDark ? 'bg-slate-700' : 'bg-slate-200/70'
              ]">
                <User :class="isDark ? 'text-slate-400' : 'text-slate-500'" class="w-5 h-5" />
              </div>
              <!-- Email & Status -->
              <div>
                <p :class="isDark ? 'text-white' : 'text-slate-900'">{{ invitation.email }}</p>
                <p :class="[
                  'text-sm flex items-center',
                  isDark ? 'text-slate-400' : 'text-slate-500'
                ]">
                  <span 
                    class="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium"
                    :class="getStatusLabel(invitation.status)"
                  >
                    {{ invitation.status.toLocaleLowerCase() }}
                  </span>
                  · Invited as {{ invitation.role.toLocaleLowerCase() }}
                </p>
              </div>
            </div>
            <!-- Resend Button -->
            <button 
              v-if="invitation.status === InvitationStatus.PENDING"
              @click="resendInvitation(invitation.id)"
              :disabled="loadingInvitations.has(invitation.id)"
              :class="[
                'transition-colors',
                loadingInvitations.has(invitation.id) 
                  ? (isDark ? 'text-slate-400 cursor-wait' : 'text-slate-500 cursor-wait') 
                  : (isDark ? 'text-emerald-500 hover:text-emerald-400' : 'text-emerald-600 hover:text-emerald-500')
              ]"
            >
              <span v-if="loadingInvitations.has(invitation.id)">Resending...</span>
              <span v-else>Resend</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
  
  <script setup lang="ts">
  import { ref, reactive, onMounted, computed } from 'vue';
  import { Mail, User, Edit3,Shield, Send } from 'lucide-vue-next';
  import { useNotificationStore } from '../stores/notification';
  import { useInvitationStore } from '../stores/invitation';
  import { useThemeStore } from '../stores/theme';
  import { UserRole } from '../types/UserRole';
  import { InvitationStatus } from '../types/invitation/InvitationStatus';
  
  const notificationStore = useNotificationStore();
  const invitationStore = useInvitationStore();

  const themeStore = useThemeStore();
  const isDark = computed(() => themeStore.isDark);

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

  // Role options with icons
  const roleOptions = [
    {
      label: 'Admin',
      value: UserRole.ADMIN,
      description: 'Full access to manage users and settings',
      icon: Shield
    },
    {
      label: 'Contributor',
      value: UserRole.CONTRIBUTOR,
      description: 'Can create and publish content, participate in discussions',
      icon: Edit3
    },
    {
      label: 'User',
      value: UserRole.USER,
      description: 'Can view content and participate in discussions',
      icon: User
    }
  ];

  const getStatusLabel = (status: string) => {
    return {
      [InvitationStatus.PENDING]: isDark ? 'bg-amber-500/10 text-amber-500' : 'bg-amber-600/10 text-amber-600',
      [InvitationStatus.ACCEPTED]: isDark ? 'bg-emerald-500/10 text-emerald-500' : 'bg-emerald-600/10 text-emerald-600',
      [InvitationStatus.EXPIRED]: isDark ? 'bg-red-500/10 text-red-500' : 'bg-red-600/10 text-red-600'
    }[status];
  };
  </script>