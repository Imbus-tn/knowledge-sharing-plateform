<template>
    <div class="max-w-3xl mx-auto px-4 sm:px-6 py-8">
      <div class="mb-8">
        <h1 class="text-3xl font-bold text-white">Update User Role</h1>
        <p class="text-slate-400 mt-2">Modify the role of the selected user and optionally send them a message.</p>
      </div>

      <!-- Error Message -->
      <div v-if="showErrorMessage" 
          class="mb-6 p-4 bg-red-500/10 border border-red-500/20 rounded-lg flex justify-center"
      >
        <p class="text-red-500 text-sm text-center">
          {{ errorMessage }}
        </p>
      </div>
  
      <!-- Update Role Form -->
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
                v-model="userEmail"
                type="email"
                disabled
                class="w-full pl-10 pr-4 py-3 bg-slate-700/50 border border-slate-600 rounded-lg text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-emerald-500 cursor-not-allowed"
              />
            </div>
          </div>
  
          <!-- Role Selection -->
          <div>
            <label class="block text-sm font-medium text-slate-300 mb-2">
              User Role
            </label>
            <div class="grid grid-cols-3 gap-4">
            <!-- Admin Role -->
            <button
              type="button"
              @click="selectedRole = 'ADMIN'"
              class="p-4 border rounded-lg text-left"
              :class="{
                'border-emerald-500 bg-emerald-500/10 text-white': selectedRole === 'ADMIN',
                'border-slate-600 bg-slate-700/50 text-slate-300 hover:border-slate-500': selectedRole !== 'ADMIN'
              }"
            >
              <div class="flex items-center mb-2">
                <Shield class="w-5 h-5 mr-2" />
                <span class="font-medium">Admin</span>
              </div>
              <p class="text-sm text-slate-400">Full access to manage users and settings</p>
            </button>

            <!-- Contributor Role -->
            <button
              type="button"
              @click="selectedRole = 'CONTRIBUTOR'"
              class="p-4 border rounded-lg text-left"
              :class="{
                'border-emerald-500 bg-emerald-500/10 text-white': selectedRole === 'CONTRIBUTOR',
                'border-slate-600 bg-slate-700/50 text-slate-300 hover:border-slate-500': selectedRole !== 'CONTRIBUTOR'
              }"
            >
              <div class="flex items-center mb-2">
                <Edit3 class="w-5 h-5 mr-2" />
                <span class="font-medium">Contributor</span>
              </div>
              <p class="text-sm text-slate-400">Can create and publish content, participate in discussions</p>
            </button>

            <!-- User Role -->
            <button
              type="button"
              @click="selectedRole = 'USER'"
              class="p-4 border rounded-lg text-left"
              :class="{
                'border-emerald-500 bg-emerald-500/10 text-white': selectedRole === 'USER',
                'border-slate-600 bg-slate-700/50 text-slate-300 hover:border-slate-500': selectedRole !== 'USER'
              }"
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
              v-model="message"
              rows="3"
              class="w-full px-4 py-3 bg-slate-700/50 border border-slate-600 rounded-lg text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-emerald-500"
              placeholder="Add a personal message to the email"
            ></textarea>
          </div>
  
          <!-- Submit Buttons -->
          <div class="flex justify-end space-x-3">
            <button 
              type="button"
              @click="cancelUpdate"
              class="px-4 py-2 bg-slate-700 text-slate-300 rounded hover:bg-slate-600 transition-colors"
            >
              Cancel
            </button>
            <button 
              type="submit"
              class="px-4 py-2 bg-emerald-500 text-white rounded hover:bg-emerald-600 transition-colors"
            >
              Save
            </button>
          </div>
        </form>
      </div>
    </div>
  </template>
  
  <script setup lang="ts">
  import { ref, onMounted } from 'vue';
  import { useRouter } from 'vue-router';
  import { useAuthStore } from '../stores/auth';
  import { apiClient } from '../api';
  import { Mail, Edit3, User, Shield } from 'lucide-vue-next';

  const router = useRouter();
  const authStore = useAuthStore();

  const props = defineProps({
    userId: {
      type: Number,
      required: true
    }
  });

  const userEmail = ref('');
  const selectedRole = ref('');
  const message = ref('');

  const showErrorMessage = ref(false);
  const errorMessage = ref('');

  onMounted(async () => {
    try {
      const response = await apiClient.get(`/admin/users/${props.userId}`, {
        headers: { Authorization: `Bearer ${authStore.accessToken}` }
      });
      userEmail.value = response.data.email;
      selectedRole.value = response.data.role;
    } catch (error) {
      console.error('Failed to fetch user details:', error);
      errorMessage.value = 'An error occurred while loading user details.';
      showErrorMessage.value = true;

      setTimeout(() => {
        showErrorMessage.value = false;
      }, 6000);
    }
  });

  const handleSubmit = async () => {
    try {
      await apiClient.put(`/admin/users/${props.userId}/update-role`, 
        { message: message.value }, // Send message in request body
        { 
          params: { role: selectedRole.value },
          headers: { Authorization: `Bearer ${authStore.accessToken}` }
        }
      );
      
      // Redirect with success query param
      router.push({ 
        name: 'manage-users', 
        query: { roleUpdateSuccess: 'true' } 
      });
    } catch (error) {
      console.error('Failed to update role:', error);

      // Show error message
      errorMessage.value = 'An error occurred while updating the role.';
      showErrorMessage.value = true;

      // Hide error message after 6 seconds
      setTimeout(() => {
        showErrorMessage.value = false;
      }, 6000);
      }
  };

  const cancelUpdate = () => {
    router.replace({ 
      name: 'manage-users',
      query: {} // Explicitly clear all query parameters
    });
  };
  </script>