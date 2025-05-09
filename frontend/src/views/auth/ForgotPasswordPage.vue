<template>
    <div class="min-h-screen flex flex-col">
      <div class="flex-1 flex items-center justify-center p-4">
        <div class="w-full max-w-md">
          <div class="text-center mb-8">
            <h2 class="text-3xl font-bold text-white mb-2">Reset your password</h2>
            <p class="text-slate-400">Enter your email to receive a reset code</p>
          </div>
  
          <div class="bg-slate-800/50 backdrop-blur-sm rounded-lg border border-slate-700 p-6">
            <form @submit.prevent="handleSubmit" class="space-y-6">
              <div>
                <label for="email" class="block text-sm font-medium text-slate-300 mb-2">
                  Email address
                </label>
                <div class="relative">
                  <Mail class="absolute left-3 top-1/2 transform -translate-y-1/2 text-slate-400 w-5 h-5" />
                  <input
                    id="email"
                    v-model="email"
                    type="email"
                    required
                    class="w-full pl-10 pr-4 py-3 bg-slate-700/50 border border-slate-700 rounded-lg text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-emerald-500"
                    placeholder="Enter your email"
                  />
                </div>
                <!-- Email Validation Errors -->
                <div v-if="emailErrors.length" class="mt-2 text-slate-400 text-sm">
                  {{ emailErrors.join(', ') }}
                </div>
              </div>
  
              <button
                type="submit"
                class="w-full flex items-center justify-center px-4 py-3 border border-transparent rounded-lg shadow-sm text-sm font-medium text-white bg-emerald-500 hover:bg-emerald-600 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-emerald-500 transition-colors"
              >
                <Send class="w-5 h-5 mr-2" />
                Send Reset Code
              </button>
            </form>
  
            <div class="mt-6 text-center">
              <router-link to="/login" class="text-sm text-emerald-500 hover:text-emerald-400">
                Back to login
              </router-link>
            </div>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script setup lang="ts">
  import { ref } from 'vue';
  import { useRouter } from 'vue-router';
  import { apiClient } from '../../api';
  
  const router = useRouter();
  const email = ref('');
  const emailErrors = ref<string[]>([]);
  
  const handleSubmit = async() => {
    try{
      // Clear any previous errors
      emailErrors.value = [];

      // Call the backend API to validate the email and send the OTP
      await apiClient.post(`/forgotPassword/verifyMail/${email.value}`);

      // Redirect to the OTP verification page with the email as a query parameter
      router.push({
        name: 'verify-otp',
        query: { email: email.value }
      });
    }catch (error: any) {
      // Handle API errors and populate emailErrors
      if(error.response && error.response.data) {
        const errors = error.response.data.email || [];
        emailErrors.value = errors;
      } else {
        emailErrors.value = ['An unexpected error occurred. Please try again later.'];
      }
    }

  };
  </script>