<template>
  <div 
    :class="[
      'max-w-3xl mx-auto px-4 sm:px-6 py-8',
      isDark ? 'text-slate-300' : 'text-slate-700'
    ]"
  >
    <div class="mb-8">
      <h1 
        :class="[
          'text-3xl font-bold mb-2 transition-colors duration-200',
          isDark ? 'text-white' : 'text-slate-900'
        ]"
      >
        Update User Role
      </h1>
      <p :class="isDark ? 'text-slate-400' : 'text-slate-500'">
        Modify the role of the selected user and optionally send them a message.
      </p>
    </div>

    <!-- Error Message -->
    <div v-if="showErrorMessage" 
      :class="[
        'mb-6 p-4 rounded-lg flex justify-center',
        isDark ? 'bg-red-500/10 border border-red-500/20' : 'bg-red-600/10 border border-red-600/20'
      ]"
    >
      <p :class="isDark ? 'text-red-500' : 'text-red-600'" class="text-sm text-center">
        {{ errorMessage }}
      </p>
    </div>

    <!-- Update Role Form -->
    <div :class="[
      'backdrop-blur-sm rounded-lg border overflow-hidden shadow-xl',
      isDark 
        ? 'bg-slate-800/50 border-slate-700' 
        : 'bg-slate-100/90 border-slate-300'
    ]">
      <div class="p-6">
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
                class="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5" 
              />
              <input
                id="email"
                v-model="userEmail"
                type="email"
                disabled
                :class="[
                  'w-full pl-10 pr-4 py-3 rounded-lg border placeholder-slate-400 focus:outline-none focus:ring-2 transition-colors cursor-not-allowed',
                  isDark 
                    ? 'bg-slate-700/50 border-slate-600 text-white focus:ring-emerald-500' 
                    : 'bg-slate-200/70 border-slate-300 text-slate-900 focus:ring-emerald-600'
                ]"
              />
            </div>
          </div>

          <!-- Role Selection -->
          <div>
            <label 
              class="block text-sm font-medium mb-2 transition-colors duration-200"
              :class="isDark ? 'text-slate-300' : 'text-slate-600'"
            >
              User Role
            </label>
            <div class="grid grid-cols-3 gap-4">
              <!-- Admin Role -->
              <button
                type="button"
                @click="selectedRole = 'ADMIN'"
                class="p-4 border rounded-lg text-left transition-colors duration-200"
                :class="[
                  selectedRole === 'ADMIN' 
                    ? (isDark ? 'border-emerald-500 bg-emerald-500/10 text-white' : 'border-emerald-600 bg-emerald-600/10 text-slate-900') 
                    : (isDark ? 'border-slate-600 bg-slate-700/50 text-slate-300 hover:border-slate-500' : 'border-slate-300 bg-slate-200/70 text-slate-600 hover:border-slate-400')
                ]"
              >
                <div class="flex items-center mb-2">
                  <Shield 
                    :class="isDark ? 'text-emerald-500' : 'text-emerald-600'" 
                    class="w-5 h-5 mr-2" 
                  />
                  <span 
                    :class="isDark ? 'text-white' : 'text-slate-900'" 
                    class="font-medium"
                  >
                    Admin
                  </span>
                </div>
                <p :class="isDark ? 'text-slate-400' : 'text-slate-500'" class="text-sm">
                  Full access to manage users and settings
                </p>
              </button>

              <!-- Contributor Role -->
              <button
                type="button"
                @click="selectedRole = 'CONTRIBUTOR'"
                class="p-4 border rounded-lg text-left transition-colors duration-200"
                :class="[
                  selectedRole === 'CONTRIBUTOR' 
                    ? (isDark ? 'border-emerald-500 bg-emerald-500/10 text-white' : 'border-emerald-600 bg-emerald-600/10 text-slate-900') 
                    : (isDark ? 'border-slate-600 bg-slate-700/50 text-slate-300 hover:border-slate-500' : 'border-slate-300 bg-slate-200/70 text-slate-600 hover:border-slate-400')
                ]"
              >
                <div class="flex items-center mb-2">
                  <Edit3 
                    :class="isDark ? 'text-emerald-500' : 'text-emerald-600'" 
                    class="w-5 h-5 mr-2" 
                  />
                  <span 
                    :class="isDark ? 'text-white' : 'text-slate-900'" 
                    class="font-medium"
                  >
                    Contributor
                  </span>
                </div>
                <p :class="isDark ? 'text-slate-400' : 'text-slate-500'" class="text-sm">
                  Can create and publish content, participate in discussions
                </p>
              </button>

              <!-- User Role -->
              <button
                type="button"
                @click="selectedRole = 'USER'"
                class="p-4 border rounded-lg text-left transition-colors duration-200"
                :class="[
                  selectedRole === 'USER' 
                    ? (isDark ? 'border-emerald-500 bg-emerald-500/10 text-white' : 'border-emerald-600 bg-emerald-600/10 text-slate-900') 
                    : (isDark ? 'border-slate-600 bg-slate-700/50 text-slate-300 hover:border-slate-500' : 'border-slate-300 bg-slate-200/70 text-slate-600 hover:border-slate-400')
                ]"
              >
                <div class="flex items-center mb-2">
                  <User 
                    :class="isDark ? 'text-emerald-500' : 'text-emerald-600'" 
                    class="w-5 h-5 mr-2" 
                  />
                  <span 
                    :class="isDark ? 'text-white' : 'text-slate-900'" 
                    class="font-medium"
                  >
                    User
                  </span>
                </div>
                <p :class="isDark ? 'text-slate-400' : 'text-slate-500'" class="text-sm">
                  Can view content and participate in discussions
                </p>
              </button>
            </div>
          </div>

          <!-- Custom Message -->
          <div>
            <label 
              class="block text-sm font-medium mb-2 transition-colors duration-200"
              :class="isDark ? 'text-slate-300' : 'text-slate-600'"
            >
              Personal Message (Optional)
            </label>
            <textarea
              v-model="message"
              rows="3"
              :class="[
                'w-full px-4 py-3 border rounded-lg placeholder-slate-400 focus:outline-none focus:ring-2 transition-colors',
                isDark 
                  ? 'bg-slate-700/50 border-slate-600 text-white focus:ring-emerald-500' 
                  : 'bg-slate-200/70 border-slate-300 text-slate-900 focus:ring-emerald-600'
              ]"
              placeholder="Add a personal message to the email"
            ></textarea>
          </div>

          <!-- Submit Buttons -->
          <div class="flex justify-end space-x-3">
            <button 
              type="button"
              @click="cancelUpdate"
              :class="[
                'px-4 py-2 rounded-2xl hover:bg-slate-600/50 transition-colors',
                isDark ? 'bg-slate-700/50 text-slate-300 hover:bg-slate-600' : 'bg-slate-200/70 text-slate-600 hover:bg-slate-300/80'
              ]"
            >
              Cancel
            </button>
            <button 
              type="submit"
              :class="[
                'px-4 py-2 rounded-xl hover:bg-emerald-600/90 transition-colors',
                isDark ? 'bg-emerald-500 text-white hover:bg-emerald-600' : 'bg-emerald-600 text-white hover:bg-emerald-700'
              ]"
            >
              Save
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>
  
  <script setup lang="ts">
  import { ref, onMounted, computed } from 'vue';
  import { useRouter } from 'vue-router';
  import { useAuthStore } from '../stores/auth';
  import { useThemeStore } from '../stores/theme';
  import { apiClient } from '../api';
  import { Mail, Edit3, User, Shield } from 'lucide-vue-next';

  const router = useRouter();
  const authStore = useAuthStore();

  const themeStore = useThemeStore();
  const isDark = computed(() => themeStore.isDark);

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