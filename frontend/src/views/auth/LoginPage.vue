<template>
    <div class="min-h-screen flex">
      <!-- Left Section -->
      <div class="hidden lg:flex lg:w-1/2 relative overflow-hidden">
        <div class="absolute inset-0 bg-gradient-to-br from-slate-900 via-slate-800 to-emerald-900/20">
          <div class="absolute inset-0 bg-[radial-gradient(circle_at_30%_50%,rgba(16,185,129,0.1),transparent_50%)]"></div>
        </div>
        <img
          src="../../assets/login-bg.jpeg"
          alt="Team collaboration"
          class="object-cover w-full h-full opacity-20"
        />
        <div class="absolute inset-0 flex items-center justify-center p-12">
          <div class="max-w-xl">
            <h2 class="text-4xl font-bold text-white mb-6">
              Share Knowledge, <br />
              Grow Together
            </h2>
            <p class="text-slate-300 text-lg leading-relaxed">
              Join our knowledge-sharing platform where teams collaborate, learn, and build better solutions together. Access a wealth of technical resources and best practices.
            </p>
          </div>
        </div>
      </div>
  
      <!-- Right Section -->
      <div class="w-full lg:w-1/2 flex flex-col">
        <div class="flex-grow flex flex-col items-center justify-center p-8 sm:p-12 lg:p-16">
          <div class="w-full max-w-md">
            <div class="text-center mb-8">
              <div class="flex justify-center mb-6">
                <img 
                  :src="logoIcon" 
                  alt="Imbus Knowledge Logo" 
                  class="w-20 h-20"
                />
              </div>
              <h2 class="text-3xl font-bold text-white mb-2">Sign In</h2>
              <p class="text-slate-400">Please enter your email address and password!</p>
            </div>
  
            <!-- Success Message -->
            <div v-if="showSuccessMessage" class="mb-6 p-4 bg-emerald-500/10 border border-emerald-500/20 rounded-lg">
              <p class="text-emerald-500 text-sm">
                {{ 
                  route.query.registrationSuccess === 'true'
                    ? 'Registration complete! Please sign in to your account.'
                    : 'Password has been successfully reset. Please sign in with your new password.'   
                }}
              </p>
            </div>
  
            <form @submit.prevent="handleLogin" class="space-y-6" novalidate>

              <!-- Email Input -->
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
                    class="w-full pl-10 pr-4 py-3 bg-slate-800/50 border border-slate-700 rounded-lg text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:border-emerald-500 transition-colors"
                    placeholder="Enter your email"
                  />
                </div>
                <!-- Email Validation Errors -->
                <div v-if="emailErrors.length" class="mt-2 text-slate-400 text-sm">
                  {{ emailErrors.join(', ') }}
                </div>
              </div>
  
              <!-- Password Input -->
              <div>
                <label for="password" class="block text-sm font-medium text-slate-300 mb-2">
                  Password
                </label>
                <div class="relative">
                  <Lock class="absolute left-3 top-1/2 transform -translate-y-1/2 text-slate-400 w-5 h-5" />
                  <input
                    id="password"
                    v-model="password"
                    :type="showPassword ? 'text' : 'password'"
                    required
                    class="w-full pl-10 pr-12 py-3 bg-slate-800/50 border border-slate-700 rounded-lg text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:border-emerald-500 transition-colors"
                    placeholder="Enter your password"
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
                <div v-if="passwordErrors.length" class="mt-2 text-slate-400 text-sm">
                  {{ passwordErrors.join(', ') }}
                </div>
                <!-- General Error (Invalid credentials) -->
                <div v-if="generalError">
                  <p class="mt-2 text-slate-400 text-sm">{{ generalError }}</p>
                </div>
              </div>
  
              <!-- Remember Me & Forgot Password -->
              <div class="flex items-center justify-between">
                <label class="inline-flex items-center cursor-pointer">
                  <div class="relative">
                    <input
                      type="checkbox"
                      v-model="rememberMe"
                      class="sr-only peer"
                    />
                    <div class="w-11 h-6 bg-slate-700 rounded-full peer peer-checked:after:translate-x-full rtl:peer-checked:after:-translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-0.5 after:start-[2px] after:bg-white after:border-slate-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-emerald-500"></div>
                  </div>
                  <span class="ml-3 text-sm text-slate-300">Keep me signed in</span>
                </label>
                <router-link to="/forgot-password" class="text-sm text-emerald-500 hover:text-emerald-400">
                  Forgot password?
                </router-link>
              </div>
  
              <!-- Sign In Button -->
              <button
                type="submit"
                class="w-full flex items-center justify-center px-4 py-3 border border-transparent rounded-lg shadow-sm text-sm font-medium text-white bg-emerald-500 hover:bg-emerald-600 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-emerald-500 transition-colors"
              >
                <LogIn class="w-5 h-5 mr-2" />
                Sign in to your account
              </button>
            </form>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script setup lang="ts">
  import { watch, ref } from 'vue';
  import { useRouter, useRoute } from 'vue-router';
  import { Mail, Lock, Eye, EyeOff, LogIn } from 'lucide-vue-next';
  import logoIcon from '../../assets/logo-icon.png'
  import { useAuthStore } from '../../stores/auth';
  import { useNotificationStore } from '../../stores/notification';
  
  const router = useRouter();
  const route = useRoute();
  const authStore = useAuthStore();
  const notificationStore = useNotificationStore();
  
  const email = ref('');
  const password = ref('');
  const showPassword = ref(false);
  const isLoading = ref(false);
  const rememberMe = ref(false);
  
  const emailErrors = ref<string[]>([]);
  const passwordErrors = ref<string[]>([]);
  const generalError = ref<string>('');


  const showSuccessMessage = ref(false);

  const handleLogin = async () => {

    emailErrors.value = [];
    passwordErrors.value = [];
    generalError.value = '';
    isLoading.value = true;
    
    try{

      if (!email.value.trim()) {
        emailErrors.value = ["Email is required"];
      } else if (!isValidEmail(email.value)) {
        emailErrors.value = ["Invalid email format"];
      }
      
      if (!password.value.trim()) {
        passwordErrors.value = ["Password is required"];
      }
  
      // Stop if there are validation errors
      if (emailErrors.value.length || passwordErrors.value.length) {
        return;
      }

      // Get redirect path from query or default to '/feed'
      const redirectPath = route.query.redirect?.toString() || '/feed';

      await authStore.login(email.value, password.value, rememberMe.value);

      notificationStore.addNotification({
        type: 'system',
        message: `Welcome back, ${authStore.user?.name || 'User'}!`,
        link: redirectPath
      });

      router.push(redirectPath);
    }catch (error: any) {
      if (error.response?.data){
        const errorData = error.response.data;

        // Map field-specific errors
        if (errorData.email) emailErrors.value = errorData.email;
        if (errorData.password) passwordErrors.value = errorData.password;
        if (errorData.general) generalError.value = errorData.general[0];

        password.value = '';
        showSuccessMessage.value = false;
      } else {
        generalError.value = 'Login failed. Please check your connection.';
      }
      password.value = ''; // Clear password after error
    }finally {
      isLoading.value = false;
    }

  };

  const isValidEmail = (email: string) => {
    return /^[A-Za-z0-9+_.-]+@(.+)$/.test(email);
  };

  // Watch for query parameter changes
  watch(
    () => route.query,
    (newQuery) => {
      showSuccessMessage.value = 
        newQuery.registrationSuccess === 'true' ||
        newQuery.passwordReset === 'success';

      // Auto-hide after 6 seconds
      if (showSuccessMessage.value) {
        setTimeout(() => {
          showSuccessMessage.value = false;
          // Remove query param from URL
          router.replace({query: {}});
        }, 6000);
      }
     
    },
    { immediate: true} // This enables immediate execution
  )
  
  
  </script>