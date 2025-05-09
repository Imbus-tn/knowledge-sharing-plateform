<template>
    <div class="min-h-screen flex flex-col">
      <div class="flex-1 flex items-center justify-center p-4">
        <div class="w-full max-w-md">
          <div class="text-center mb-8">
            <h2 class="text-3xl font-bold text-white mb-2">Set New Password</h2>
            <p class="text-slate-400">Choose a strong password for your account</p>
          </div>
  
          <div class="bg-slate-800/50 backdrop-blur-sm rounded-lg border border-slate-700 p-6">
            <form @submit.prevent="handleSubmit" class="space-y-6">
              <div>
                <label for="password" class="block text-sm font-medium text-slate-300 mb-2">
                  New Password
                </label>
                <div class="relative">
                  <Lock class="absolute left-3 top-1/2 transform -translate-y-1/2 text-slate-400 w-5 h-5" />
                  <input
                    id="password"
                    v-model="formData.password"
                    :type="showPassword ? 'text' : 'password'"
                    required
                    class="w-full pl-10 pr-12 py-3 bg-slate-700/50 border border-slate-700 rounded-lg text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-emerald-500"
                    placeholder="Enter new password"
                  />
                  <button
                    type="button"
                    @click="showPassword = !showPassword"
                    class="absolute right-3 top-1/2 transform -translate-y-1/2 text-slate-400 hover:text-slate-300"
                  >
                    <Eye v-if="!showPassword" class="w-5 h-5" />
                    <EyeOff v-else class="w-5 h-5" />
                  </button>
                </div>
                <!-- Password Validation Errors -->
                <div v-if="showValidation && passwordErrors.length" class="mt-2 text-slate-400 text-sm">
                  {{ passwordErrors.join(', ') }}
                </div>
              </div>
  
              <div>
                <label for="confirmPassword" class="block text-sm font-medium text-slate-300 mb-2">
                  Confirm Password
                </label>
                <div class="relative">
                  <Lock class="absolute left-3 top-1/2 transform -translate-y-1/2 text-slate-400 w-5 h-5" />
                  <input
                    id="confirmPassword"
                    v-model="formData.repeatPassword"
                    :type="showConfirmPassword ? 'text' : 'password'"
                    required
                    class="w-full pl-10 pr-12 py-3 bg-slate-700/50 border border-slate-700 rounded-lg text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-emerald-500"
                    placeholder="Confirm new password"
                  />
                  <button
                    type="button"
                    @click="showConfirmPassword = !showConfirmPassword"
                    class="absolute right-3 top-1/2 transform -translate-y-1/2 text-slate-400 hover:text-slate-300"
                  >
                    <Eye v-if="!showConfirmPassword" class="w-5 h-5" />
                    <EyeOff v-else class="w-5 h-5" />
                  </button>
                </div>
                <!-- Confirm Password Match Error -->
                <div v-if="showValidation && confirmPasswordError" class="mt-2 text-slate-400 text-sm">
                  {{ confirmPasswordError }}
                </div>
              </div>
  
              <button
                type="submit"
                class="w-full flex items-center justify-center px-4 py-3 border border-transparent rounded-lg shadow-sm text-sm font-medium text-white bg-emerald-500 hover:bg-emerald-600 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-emerald-500 transition-colors"
              >
                <Save class="w-5 h-5 mr-2" />
                Set New Password
              </button>
            </form>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script setup lang="ts">
  import { ref,reactive, computed, watch } from 'vue';
  import { useRouter, useRoute } from 'vue-router';
  import { Lock, Eye, EyeOff, Save } from 'lucide-vue-next';
  import { apiClient } from '../../api';
  
  const router = useRouter();
  const route = useRoute();
  
  const showPassword = ref(false);
  const showConfirmPassword = ref(false);
  const passwordErrors = ref<string[]>([]);
  const confirmPasswordError = ref<string | null>(null);
  const showValidation = ref(false); 

  const formData = reactive({
    password: '',
    repeatPassword: '',
    
  });

  const email = computed(() => route.query.email as string);

  // Helper Function to Validate Password Complexity
  const validatePassword = (): string[] => {
    const errors: string[] = [];
    if (formData.password.length < 8) {
      errors.push('Minimum 8 characters');
    }
    if (formData.password.length > 64) {
      errors.push('Maximum 64 characters');
    }
    if (!/[A-Z]/.test(formData.password)) {
      errors.push('Uppercase letter required');
    }
    if (!/[a-z]/.test(formData.password)) {
      errors.push('Lowercase letter required');
    }
    if (!/[0-9]/.test(formData.password)) {
      errors.push('Number required');
    }
    if (!/[^A-Za-z0-9]/.test(formData.password)) {
      errors.push('Special character required (!@#$%^&*...)');
    }
    return errors;
  };


  watch(
    () => formData.password,
    () => {
      if (showValidation.value) {
        passwordErrors.value = validatePassword();
      }
    }
  );

  watch(
    [() => formData.password, () => formData.repeatPassword],
    ([pass, confirm]) => {
      if (showValidation.value && confirm) {
        confirmPasswordError.value = pass === confirm ? null : 'Passwords do not match';
      } else {
        confirmPasswordError.value = null;
      }
    },
    { immediate: true }
  );
    
  const handleSubmit = async() => {
    showValidation.value = true; // Enable validation
    passwordErrors.value = validatePassword(); // Validate password complexity
    confirmPasswordError.value =
      formData.password === formData.repeatPassword ? null : 'Passwords do not match';

    // Stop submission if there are validation errors
    if (passwordErrors.value.length > 0 || confirmPasswordError.value) {
      return;
    }

    try {
      await apiClient.post(`/forgotPassword/changePassword/${email.value}`, {
        password: formData.password,
        repeatPassword: formData.repeatPassword,
      });

      // Redirect to login page with success query
      router.push({
        name: 'login',
        query: { passwordReset: 'success' },
      });
    } catch (error: any) {
      if (error.response?.data) {
        const backendErrors = error.response.data.password || [];
        // Handle password mismatch error separately
        if (backendErrors.includes('Passwords do not match')) {
          confirmPasswordError.value = 'Passwords do not match';
          passwordErrors.value = backendErrors.filter(
            (err: string) => err !== 'Passwords do not match'
          );
        } else {
          passwordErrors.value = backendErrors;
          confirmPasswordError.value = null;
        }
      } else {
        passwordErrors.value = ['An unexpected error occurred'];
      }
    }
  };
  </script>