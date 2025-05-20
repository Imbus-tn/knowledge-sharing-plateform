  <template>
    <div :class="[
      'max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8',
      isDark ? 'text-slate-300' : 'text-slate-700'
    ]">
      <!-- Page Header -->
      <div class="mb-8">
        <h1 :class="[
          'text-2xl font-bold mb-6',
          isDark ? 'text-slate-200' : 'text-slate-900'
        ]">
          Manage Users
        </h1>

        <!-- Success/Error Messages -->
        <div v-if="showSuccessMessage" :class="[
          'mb-6 p-4 rounded-lg flex justify-center',
          isDark ? 'bg-emerald-500/10 border border-emerald-500/20 text-emerald-500' : 'bg-emerald-100 border border-emerald-200 text-emerald-700'
        ]">
          {{ successMessageText }}
        </div>

        <div v-if="showErrorMessage" :class="[
          'mb-6 p-4 rounded-lg flex justify-center',
          isDark ? 'bg-red-500/10 border border-red-500/20 text-red-500' : 'bg-red-100 border border-red-200 text-red-700'
        ]">
          {{ errorMessageText }}
        </div>
      </div>

      <!-- Search & Filters -->
      <div class="relative max-w-2xl mx-auto mb-8">
        <div class="relative">
          <MagnifyingGlassIcon :class="isDark ? 'text-slate-400' : 'text-slate-500'" class="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5" />
          <input
            v-model="searchQuery"
            type="text"
            placeholder="Search for users..."
            :class="[
              'w-full pl-10 pr-4 py-3 rounded-lg focus:outline-none focus:ring-2 transition-colors border-2 shadow-xl',
              isDark 
                ? 'bg-slate-700/50 border-slate-600 text-white placeholder-slate-400 focus:ring-emerald-500' 
                : 'bg-slate-200/50 border-slate-300 text-slate-900 placeholder-slate-500 focus:ring-emerald-600'
            ]"
          />
        </div>

        <!-- Role Filters -->
        <div class="mt-4 flex justify-center space-x-4">
          <button
            v-for="role in roles"
            :key="role"
            @click="selectedRoleFilter = role"
            :class="[
              'px-4 py-2 rounded-full text-sm font-medium transition-colors border shadow-xl',
              selectedRoleFilter === role 
                ? (isDark ? 'bg-emerald-500 text-white' : 'bg-emerald-500 text-white') 
                : (isDark 
                    ? 'bg-slate-700/50 border-slate-700 text-slate-300 hover:bg-slate-600/50' 
                    : 'bg-slate-100/50 border-slate-300 text-slate-600 hover:bg-slate-200/50'
                  )
            ]"
          >
            {{ role }}
          </button>
        </div>
      </div>

      <!-- User Table -->
      <div :class="[
        'backdrop-blur-sm rounded-lg border overflow-hidden shadow-xl',
        isDark 
          ? 'bg-slate-800/50 border-slate-700' 
          : 'bg-slate-100/50 border-slate-200'
      ]">
        <table class="w-full border-separate border-spacing-0">
          <thead>
            <tr :class="isDark ? 'text-left text-xs font-medium uppercase tracking-wider bg-slate-700 text-slate-400' : 'text-left text-xs font-medium uppercase tracking-wider bg-slate-100 text-slate-500'">
              <th :class="[
                'px-6 py-3 whitespace-nowrap',
                isDark ? 'text-white border-b border-slate-600' : 'text-slate-900 border-b border-slate-200'
              ]">#</th>
              <th :class="[
                'px-6 py-3 whitespace-nowrap',
                isDark ? 'text-white border-b border-slate-600' : 'text-slate-900 border-b border-slate-200'
              ]">Name</th>
              <th :class="[
                'px-6 py-3 whitespace-nowrap',
                isDark ? 'text-white border-b border-slate-600' : 'text-slate-900 border-b border-slate-200'
              ]">Email</th>
              <th :class="[
                'px-6 py-3 whitespace-nowrap',
                isDark ? 'text-white border-b border-slate-600' : 'text-slate-900 border-b border-slate-200'
              ]">Role</th>
              <th :class="[
                'px-6 py-3 whitespace-nowrap',
                isDark ? 'text-white border-b border-slate-600' : 'text-slate-900 border-b border-slate-200'
              ]">Status</th>
              <th :class="[
                'px-6 py-3 whitespace-nowrap',
                isDark ? 'text-white border-b border-slate-600' : 'text-slate-900 border-b border-slate-200'
              ]">Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr 
              v-for="(user, index) in paginatedUsers" 
              :key="user.id"
              :class="isDark ? 'hover:bg-slate-700/50' : 'hover:bg-slate-200/70'"
              class="transition-colors"
            >
              <!-- ID -->
              <td :class="[
                'px-6 py-4 whitespace-nowrap',
                isDark ? 'text-white border-b border-slate-600' : 'text-slate-900 border-b border-slate-200'
              ]">
                {{ index + 1 }}
              </td>

              <!-- Name -->
              <td :class="[
                'px-6 py-4 whitespace-nowrap flex items-center space-x-2',
                isDark ? 'text-white border-b border-slate-600' : 'text-slate-900 border-b border-slate-200'
              ]">
                <img 
                  v-if="user.avatarUrl" 
                  :src="avatarUrl(user)" 
                  alt="User Avatar" 
                  class="w-8 h-8 rounded-full object-cover"
                />
                <div 
                  v-else 
                  :class="isDark ? 'bg-emerald-500 border-slate-600/50' : 'bg-emerald-500 border-slate-200/50'"
                  class="w-8 h-8 rounded-full flex items-center justify-center border"
                >
                  <span class="text-xs text-white font-medium">{{ initials(user) }}</span>
                </div>
                <span>{{ user.name }}</span>
              </td>
              
              <!-- Email -->
              <td :class="[
                'px-6 py-4 whitespace-nowrap',
                isDark ? 'text-white border-b border-slate-600' : 'text-slate-900 border-b border-slate-200'
              ]">
                {{ user.email }}
              </td>
              
              <!-- Role -->
              <td :class="[
                'px-6 py-4 whitespace-nowrap',
                isDark ? 'text-white border-b border-slate-600' : 'text-slate-900 border-b border-slate-200'
              ]">
                <span 
                  class="inline-flex items-center px-2 py-0.5"
                  :class="isDark ? 'text-white' : 'text-slate-900'"
                >
                  {{ user.role[0]+user.role.slice(1).toLowerCase() }}
                </span>
              </td>

              <!-- Status -->
              <td :class="[
                'px-6 py-4 whitespace-nowrap',
                isDark ? 'text-white border-b border-slate-600' : 'text-slate-900 border-b border-slate-200'
              ]">
                <div class="flex items-center space-x-2">
                  <span 
                    class="w-3 h-3 rounded-full"
                    :class="user.enabled ? (isDark ? 'bg-emerald-500' : 'bg-emerald-600') : (isDark ? 'bg-red-500' : 'bg-red-600')"
                  ></span>
                  <span>{{ user.enabled ? 'Active' : 'Inactive' }}</span>
                </div>
              </td>

              <!-- Actions -->
              <td :class="[
                'px-6 py-4 whitespace-nowrap border-b',
                isDark ? 'border-slate-600' : 'border-slate-200'
              ]">
                <div class="flex items-center space-x-2">
                  <!-- Hidden Placeholder (Maintains spacing) -->
                  <div v-if="user.role === UserRole.ADMIN" class="w-5 h-5 invisible"></div>

                  <!-- Admin Lock Icon (Second Position) -->
                  <div 
                    v-if="user.role === UserRole.ADMIN"
                    class="relative group"
                    title="Admin actions are restricted"
                  >
                    <Lock 
                      :class="isDark ? 'text-slate-400' : 'text-slate-500'" 
                      class="w-5 h-5 cursor-help"
                    />
                  </div>

                  <!-- Edit Role (Non-admin Only) -->
                  <router-link 
                    v-if="user.role !== UserRole.ADMIN"
                    :to="{ name: 'update-user-role', params: { userId: user.id } }"
                    :class="[
                      'text-sm transition-colors',
                      isDark ? 'text-slate-400 hover:text-slate-300' : 'text-slate-500 hover:text-slate-600']"
                    title="Edit Role"
                  >
                    <Edit3 class="w-5 h-5" />
                  </router-link>

                  <!-- Deactivate / Activate (Non-admin Only) -->
                  <button
                    v-if="user.role !== UserRole.ADMIN"
                    @click="openStatusModal(user.id)"
                    :title="user.enabled ? 'Deactivate User' : 'Activate User'"
                    class="transition-colors"
                  >
                    <component 
                      :is="user.enabled ? UserRoundCheck : UserRoundX"
                      :class="[
                        'w-5 h-5',
                        isDark 
                          ? (user.enabled ? 'text-emerald-500 hover:text-emerald-400' : 'text-red-500 hover:text-red-400') 
                          : (user.enabled ? 'text-emerald-600 hover:text-emerald-500' : 'text-red-600 hover:text-red-500')]"
                    />
                  </button>

                  <!-- Send Warning (Non-admin Only) -->
                  <button
                    v-if="user.role !== UserRole.ADMIN"
                    @click="openWarningModal(user.id)" 
                    :class="[
                      'transition-colors',
                      isDark ? 'text-amber-500 hover:text-amber-400' : 'text-amber-600 hover:text-amber-500']"
                    title="Send Warning"
                  >
                    <AlertCircle class="w-5 h-5" />
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Pagination -->
      <div class="flex justify-center mt-6 space-x-4">
        <button
          @click="prevPage"
          :disabled="currentPage === 1"
          :class="[
            'px-4 py-2 rounded-lg disabled:opacity-50 border',
            isDark ? 'bg-slate-700/50 border-slate-700 text-slate-300 hover:bg-slate-600/50' : 'bg-slate-200/50 border-slate-300 text-slate-600 hover:bg-slate-300/50'
          ]"
        >
          Previous
        </button>
        <span :class="isDark ? 'text-slate-400' : 'text-slate-500'">Page {{ currentPage }} of {{ totalPages }}</span>
        <button
          @click="nextPage"
          :disabled="currentPage >= totalPages"
          :class="[
            'px-4 py-2 rounded-lg disabled:opacity-50 border',
            isDark ? 'bg-slate-700/50 border-slate-700 text-slate-300 hover:bg-slate-600/50' : 'bg-slate-200/50 border-slate-300 text-slate-600 hover:bg-slate-300/50'
          ]"
        >
          Next
        </button>
      </div>
    </div>
    <!-- Modals -->
      <ConfirmModal
        :is-open="isStatusModalOpen"
        :title="selectedUser?.enabled ? 'Deactivate User' : 'Activate User'"
        :message="selectedUser?.enabled 
          ? 'Are you sure you want to deactivate this user? They will no longer be able to log in.'
          : 'Are you sure you want to activate this user? They will regain access to the system.'"
        @confirm="confirmToggleStatus"
        @cancel="closeStatusModal"
      />
      <PromptModal
        :is-open="isWarningModalOpen"
        title="Send Warning"
        placeholder="Enter warning message..."
        @submit="sendWarning"
        @cancel="closeWarningModal"
      />
  </template>
  
  <script setup>
  import { ref, onMounted, computed, watch } from 'vue';
  import { useAuthStore } from '../stores/auth';
  import { useRouter, useRoute } from 'vue-router';
  import { UserRole } from '../types/UserRole';
  import { useThemeStore } from '../stores/theme';
  import {  AlertCircle, Edit3 , UserRoundCheck, UserRoundX, X, Lock } from 'lucide-vue-next';
  import { MagnifyingGlassIcon } from '@heroicons/vue/20/solid';
  import { apiClient } from '../api';
  import ConfirmModal from '../components/ConfirmModal.vue';
  import PromptModal from '../components/PromptModal.vue';

  const authStore = useAuthStore();
  const router = useRouter();
  const route = useRoute();
  const users = ref([]);
  const showSuccessMessage = ref(false);
  const showErrorMessage = ref(false);

  const themeStore = useThemeStore();
  const isDark = computed(() => themeStore.isDark);

  const isWarningModalOpen = ref(false);
  const isStatusModalOpen = ref(false);
  const selectedUserId = ref(null);

  // Search and Filter State
  const searchQuery = ref('');
  const selectedRoleFilter = ref('All');

  // Pagination state
  const currentPage = ref(1);
  const pageSize = ref(10);

  // Computed properties for pagination
  const paginatedUsers = computed(() => {
    const start = (currentPage.value - 1) * pageSize.value;
    const end = start + pageSize.value;
    return filteredUsers.value.slice(start, end);
  });

  const totalPages = computed(() => 
    Math.ceil(filteredUsers.value.length / pageSize.value)
  );

  // Reset page when filters change
  watch([searchQuery, selectedRoleFilter], () => {
    currentPage.value = 1;
  });

  // Error message text
  const errorMessageText = computed(() => {
    if (route.query.error) return route.query.error;
    return '';
  });

  // Success message text
  const successMessageText = computed(() => {
    if (route.query.roleUpdateSuccess === 'true') return 'User role updated successfully!';
    if (route.query.warningSent === 'true') return 'Warning sent successfully!';
    if (route.query.status === 'activated') return 'User activated successfully!';
    if (route.query.status === 'deactivated') return 'User deactivated successfully!';
    return '';
  });

  // Watch for query parameter changes
  watch(
    () => route.query,
    (newQuery) => {
      // Show success message
      if (
        ['roleUpdateSuccess', 'userDeleted', 'warningSent'].some(key => newQuery[key] === 'true') ||
        ['activated', 'deactivated'].includes(newQuery.status)
      ) {
        showSuccessMessage.value = true;
        setTimeout(() => {
          showSuccessMessage.value = false;
          router.replace({ query: {} });
        }, 6000);
      }

      // Show error message
      if (newQuery.error) {
        showErrorMessage.value = true;
        setTimeout(() => {
          showErrorMessage.value = false;
          router.replace({ query: {} });
        }, 6000);
      }
    },
    { immediate: true }
  );

  // Fetch users on mount
  onMounted(async () => {
    try {
      const response = await apiClient.get('/admin/users', {
        headers: { Authorization: `Bearer ${authStore.accessToken}` }
      });

      // Ensure response.data is an array, fallback to empty array if not
      if (Array.isArray(response.data)) {
        users.value = response.data;
      } else {
        console.warn('API did not return an array of users:', response.data);
        users.value = [];
      }
    } catch (error) {
      console.error('Failed to fetch users:', error);
      users.value = []; // Always fallback to empty array
      router.push({ 
        name: 'manage-users',
        query: { error: 'Failed to load user list' }
      });
    }
  });

  // Computed property for filtered users
  const filteredUsers = computed(() => {
    const userList = Array.isArray(users.value) ? users.value : [];
    return userList.filter((user) => {
      const matchesSearch = !searchQuery.value || user.name?.toLowerCase().includes(searchQuery.value.toLowerCase());
      const matchesRole = selectedRoleFilter.value === 'All' || user.role?.toLowerCase() === selectedRoleFilter.value;
      return matchesSearch && matchesRole;
    });
  });

  // Roles for filters
  const roles = ['All', 'admin', 'contributor', 'user'];

  const selectedUser = computed(() => {
    return users.value.find(u => u.id === selectedUserId.value);
  });

  // Open modal function
  const openStatusModal = (userId) => {
    // Ensure selectedUserId is a number
    selectedUserId.value = Number(userId);
    isStatusModalOpen.value = true;
  };

  // Close modal function
  const closeStatusModal = () => {
    selectedUserId.value = null;
    isStatusModalOpen.value = false;
  };

  // Close Delete Modal
  const closeDeleteModal = () => {
    selectedUserId.value = null;
    isDeleteModalOpen.value = false;
  };
  
  // Confirm action handler
  const confirmToggleStatus = async () => {
    try {
      // Get current status before update
      const currentUser = users.value.find(u => u.id === selectedUserId.value);
      const wasEnabled = currentUser?.enabled;

      await apiClient.patch(`/admin/users/${selectedUserId.value}/toggle-enable`, {}, {
        headers: { Authorization: `Bearer ${authStore.accessToken}` }
      });

      // Update local state
      users.value = users.value.map(user => {
        if (user.id === selectedUserId.value) {
          return { ...user, enabled: !user.enabled };
        }
        return user;
      });

      // Determine new status
      const newStatus = wasEnabled ? 'deactivated' : 'activated';
      router.push({ name: 'manage-users', query: { status: newStatus } });
    } catch (error) {
      console.error('Failed to toggle user status:', error);
      router.push({ name: 'manage-users', query: { error: 'Failed to update user status' } });
    } finally {
      closeStatusModal();
    }
  };

  const getStatusLabel = (user) => {
    return user.enabled ? 'Active' : 'Inactive';
  };

  const statusDotClass = (user) => {
    return {
      'bg-emerald-500': user.enabled,
      'bg-red-500': !user.enabled
    };
  };

  // Open Warning Modal
  const openWarningModal = (userId) => {
    selectedUserId.value = userId;
    isWarningModalOpen.value = true;
  };

  // Close Warning Modal
  const closeWarningModal = () => {
    selectedUserId.value = null;
    isWarningModalOpen.value = false;
  };
  
  // Send warning handler
  const sendWarning = async (message) => {
    try {
      await apiClient.post(`/admin/users/${selectedUserId.value}/warn`, 
        { message }, 
        { headers: { Authorization: `Bearer ${authStore.accessToken}` } }
      );
      router.push({ 
        name: 'manage-users', 
        query: { warningSent: 'true' } 
      });
    } catch (error) {
      console.error('Failed to send warning:', error);
      router.push({ 
        name: 'manage-users', 
        query: { error: 'Failed to send warning' }
      });
    } finally {
      closeWarningModal();
    }
  };
    
  // Avatar URL helper
  const avatarUrl = (user) => {
    const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080'; // Default fallback
    return user.avatarUrl 
      ? `${apiUrl}${user.avatarUrl}` 
      : '';
  };

  // Initials helper
  const initials = (user) => {
    return user.name ? user.name.split(' ').map(n => n[0]).join('').toUpperCase() : ''
  };

  </script>