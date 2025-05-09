  <template>
    <div class="max-w-7xl mx-auto p-6">
      <h1 class="text-2xl font-bold mb-6 text-slate-200">Manage Users</h1>

      <!-- Success/Error Messages -->
      <div v-if="showSuccessMessage" 
          class="mb-6 p-4 bg-emerald-500/10 border border-emerald-500/20 rounded-lg flex justify-center">
        <p class="text-emerald-500 text-sm text-center">{{ successMessageText }}</p>
      </div>
      <div v-if="showErrorMessage" 
          class="mb-6 p-4 bg-red-500/10 border border-red-500/20 rounded-lg flex justify-center">
        <p class="text-red-500 text-sm text-center">{{ errorMessageText }}</p>
      </div>

      <!-- Search & Filters -->
      <div class="relative max-w-2xl mx-auto mb-8">
        <div class="relative">
          <MagnifyingGlassIcon class="absolute left-3 top-1/2 transform -translate-y-1/2 text-slate-400 w-5 h-5" />
          <input
            v-model="searchQuery"
            type="text"
            placeholder="Search for users..."
            class="w-full pl-10 pr-4 py-3 bg-slate-700/50 border border-slate-600 rounded-lg text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-emerald-500"
          />
        </div>
        <div class="mt-4 flex justify-center space-x-4">
          <button
            v-for="role in roles"
            :key="role"
            @click="selectedRoleFilter = role"
            class="px-4 py-2 rounded-full text-sm font-medium"
            :class="selectedRoleFilter === role ? 'bg-emerald-500 text-white' : 'bg-slate-700/50 text-slate-300 hover:bg-slate-600'"
          >
            {{ role }}
          </button>
        </div>
      </div>

      <!-- User List Container -->
      <div class="bg-slate-800/50 border border-slate-700 rounded-lg p-6">
        <!-- Check if paginatedUsers is empty -->
        <div v-if="paginatedUsers.length === 0" class="text-slate-400 text-center py-4">
          No users found
        </div>
        <div v-else class="space-y-4">
          <div 
            v-for="user in paginatedUsers" 
            :key="user.id"
            class="bg-slate-800/50 border border-slate-700 p-4 rounded-lg flex items-center justify-between transition-shadow hover:shadow-md"
          >
            <!-- User Info -->
            <div class="flex items-center space-x-4">
              <div class="w-12 h-12 rounded-full overflow-hidden border border-slate-700">
                <img v-if="avatarUrl(user)" :src="avatarUrl(user)" class="w-full h-full object-cover">
                <div v-else class="w-full h-full bg-emerald-500 flex items-center justify-center">
                  <span class="text-white text-lg">{{ initials(user) }}</span>
                </div>
              </div>
              <div>
                <p class="text-slate-300 font-medium">{{ user.name }}</p>
                <p class="text-slate-400 text-sm">{{ user.email }}</p>
                <div class="mt-1 inline-flex items-center px-2 py-0.5 rounded-full text-xs" :class="roleStyle(user.role)">
                  {{ user.role.toLowerCase() }}
                </div>
              </div>
            </div>

            <!-- Actions -->
            <div class="flex items-center space-x-3">
              <router-link 
                :to="{ name: 'update-user-role', params: { userId: user.id } }"
                class="text-slate-400 hover:text-slate-300 transition-colors"
                title="Edit Role"
              >
                <Edit3 class="w-5 h-5" />
              </router-link>
              <button @click="openDeleteModal(user.id)" class="text-red-500 hover:text-red-600" title="Delete User">
                <Trash2 class="w-5 h-5" />
              </button>
              <button @click="openWarningModal(user.id)" class="text-yellow-500 hover:text-yellow-600" title="Send Warning">
                <AlertCircle class="w-5 h-5" />
              </button>
            </div>
          </div>
        </div>

        <!-- Pagination Controls -->
        <div class="flex justify-center mt-6 space-x-4">
          <button
            @click="prevPage"
            :disabled="currentPage === 1"
            class="px-4 py-2 bg-slate-700/50 text-slate-300 rounded-lg disabled:opacity-50"
          >
            Previous
          </button>
          <span class="text-slate-400">Page {{ currentPage }} of {{ totalPages }}</span>
          <button
            @click="nextPage"
            :disabled="currentPage >= totalPages"
            class="px-4 py-2 bg-slate-700/50 text-slate-300 rounded-lg disabled:opacity-50"
          >
            Next
          </button>
        </div>
      </div>

      <!-- Modals -->
      <ConfirmModal
        :is-open="isDeleteModalOpen"
        title="Confirm Deletion"
        message="Are you sure you want to delete this user? This action cannot be undone."
        @confirm="deleteUser"
        @cancel="closeDeleteModal"
      />
      <PromptModal
        :is-open="isWarningModalOpen"
        title="Send Warning"
        placeholder="Enter warning message..."
        @submit="sendWarning"
        @cancel="closeWarningModal"
      />
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted, computed, watch } from 'vue';
  import { useAuthStore } from '../stores/auth';
  import { useRouter, useRoute } from 'vue-router';
  import { Trash2, AlertCircle, Edit3 } from 'lucide-vue-next';
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

  const isDeleteModalOpen = ref(false);
  const isWarningModalOpen = ref(false);
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
    if (route.query.userDeleted === 'true') return 'User deleted successfully!';
    if (route.query.warningSent === 'true') return 'Warning sent successfully!';
    return '';
  });

  // Watch for query parameter changes
  watch(
    () => route.query,
    (newQuery) => {
      // Show success message
      if (['roleUpdateSuccess', 'userDeleted', 'warningSent'].some(key => newQuery[key] === 'true')) {
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
      users.value = response.data;
    } catch (error) {
      console.error('Failed to fetch users:', error);
      router.push({ 
        name: 'manage-users',
        query: { error: 'Failed to load user list' }
      });
    }
  });

  // Computed property for filtered users
  const filteredUsers = computed(() => {
    return users.value.filter((user) => {
      // Match search query (case-insensitive)
      const matchesSearch = !searchQuery.value || user.name?.toLowerCase().includes(searchQuery.value.toLowerCase());
      // Match role filter
      const matchesRole = selectedRoleFilter.value === 'All' || user.role.toLowerCase() === selectedRoleFilter.value;
      return matchesSearch && matchesRole;
    });
  });

  // Roles for filters
  const roles = ['All', 'user', 'contributor'];

  // Open Delete Modal
  const openDeleteModal = (userId) => {
    selectedUserId.value = userId;
    isDeleteModalOpen.value = true;
  };

  // Close Delete Modal
  const closeDeleteModal = () => {
    selectedUserId.value = null;
    isDeleteModalOpen.value = false;
  };
  
  // Delete user handler
  const deleteUser = async () => {
    try {
      await apiClient.delete(`/admin/users/${selectedUserId.value}`, {
        headers: { Authorization: `Bearer ${authStore.accessToken}` }
      });
      users.value = users.value.filter(u => u.id !== selectedUserId.value);
      router.push({ 
        name: 'manage-users', 
        query: { userDeleted: 'true' } 
      });
    } catch (error) {
      console.error('Failed to delete user:', error);
      router.push({ 
        name: 'manage-users', 
        query: { error: 'Failed to delete user' }
      });
    } finally {
      closeDeleteModal();
    }
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
    return user.name
      ? user.name.split(' ').map(n => n[0]).join('').toUpperCase()
      : '';
  };
  
  // Role styling helper
  const roleStyle = (role) => {
    return {
      'bg-orange-500/10 text-orange-500': role === 'CONTRIBUTOR',
      'bg-blue-500/10 text-blue-500': role === 'USER'
    };
  };
  </script>