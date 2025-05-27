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
              Welcome to the Team
            </h2>
            <p class="text-slate-300 text-lg leading-relaxed">
              Complete your registration to join our knowledge-sharing platform where teams collaborate, learn, and build better solutions together.
            </p>
          </div>
        </div>
      </div>
  
      <!-- Right Section - Registration Form -->
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
              <h2 class="text-3xl font-bold text-white mb-2">Complete Registration</h2>
              <p class="text-slate-400">Set up your account to get started</p>
            </div>
  
            <form @submit.prevent="handleRegister" class="space-y-6">
              <!-- Full Name -->
              <div>
                <label for="fullName" class="block text-sm font-medium text-slate-300 mb-2">
                  Full Name
                </label>
                <div class="relative">
                  <User class="absolute left-3 top-1/2 transform -translate-y-1/2 text-slate-400 w-5 h-5" />
                  <input
                    id="fullName"
                    v-model="formData.name"
                    type="text"
                    required
                    class="w-full pl-10 pr-4 py-3 bg-slate-800/50 border border-slate-700 rounded-lg text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-emerald-500"
                    placeholder="Enter your full name (e.g., John Doe)"
                  />
                </div>
                <div v-if="showValidation && fullNameErrors.length" class="mt-2 text-slate-400 text-sm">
                  {{ fullNameErrors.join(', ') }}
                </div>
              </div>
  
              <!-- Email Input (disabled, pre-filled from invitation) -->
              <div>
                <label for="email" class="block text-sm font-medium text-slate-300 mb-2">
                  Email Address
                </label>
                <div class="relative">
                  <Mail class="absolute left-3 top-1/2 transform -translate-y-1/2 text-slate-400 w-5 h-5" />
                  <input
                    id="email"
                    :value="invitationEmail"
                    type="email"
                    disabled
                    class="w-full pl-10 pr-4 py-3 bg-slate-700/50 border border-slate-600 rounded-lg text-slate-300"
                  />
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
                    v-model="formData.password"
                    :type="showPassword ? 'text' : 'password'"
                    required
                    class="w-full pl-10 pr-12 py-3 bg-slate-800/50 border border-slate-700 rounded-lg text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-emerald-500"
                    placeholder="Create a password"
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
  
              <!-- Confirm Password Input -->
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
                    class="w-full pl-10 pr-12 py-3 bg-slate-800/50 border border-slate-700 rounded-lg text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-emerald-500"
                    placeholder="Confirm your password"
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
                <div v-if="showValidation && passwordMatchError" class="mt-2 text-slate-400 text-sm">
                  {{ passwordMatchError }}
                </div>
              </div>
  
              <!-- Role Badge -->
              <div class="bg-slate-700/50 rounded-lg p-4 flex items-center justify-between">
                <div>
                  <p class="text-white font-medium">Invited as</p>
                  <p class="text-sm text-slate-400">{{ invitationRole?.toLocaleLowerCase() }}</p>
                </div>
                <div class="inline-flex items-center px-3 py-1 rounded-full text-sm bg-emerald-500/10 text-emerald-500">
                  {{ invitationRole?.toLocaleLowerCase() }}
                </div>
              </div>
  
              <!-- Submit Button -->
              <button
                type="submit"
                :disabled="isSubmitting"
                class="w-full flex items-center justify-center px-4 py-3 border border-transparent rounded-lg shadow-sm text-sm font-medium text-white bg-emerald-500 hover:bg-emerald-600 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-emerald-500 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
              >
                <UserPlus class="w-5 h-5 mr-2" />
                Complete Registration
              </button>
            </form>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script setup lang="ts">
  import { ref, reactive, computed, onMounted, watch } from 'vue';
  import { useRouter, useRoute } from 'vue-router';
  import { User, Mail, Lock, Eye, EyeOff, UserPlus } from 'lucide-vue-next';
  import logoIcon from '../../assets/logo-icon.png'
  import { useAuthStore } from '../../stores/auth';
  import { UserRole } from '../../types/UserRole';
  
  const router = useRouter();
  const route = useRoute();
  const authStore = useAuthStore();
  
  // Get invitation data from query parameters
  const invitationEmail = ref('');
  const invitationRole = ref<UserRole | null>(null);
  const invitationToken = computed(() => route.query.token as string || '');
  
  // Form state
  const fullNameErrors = ref<string[]>([]);
  const showPassword = ref(false);
  const showConfirmPassword = ref(false);
  const passwordErrors = ref<string[]>([]);
  const passwordMatchError = ref<string | null>(null);
  const showValidation = ref(false);
  const isSubmitting = ref(false);
  
  // Initialize form data with invitation info
  const formData = reactive({
    name: '',
    password: '',
    repeatPassword: '',
    
  });

  // Username validation function
  const validateFullName = (fullName: string): string[] => {
    const errors: string[] = [];
    const parts = fullName.trim().split(/\s+/).filter(p => p.length > 0);

    // Check for first and last name
    if (parts.length < 2) {
      errors.push('Full name must include first and last name separated by a space');
    }

    // Check each part length (min 2 characters)
    if (parts.some(part => part.length < 2)) {
      errors.push('Each name part must be at least 2 characters');
    }

    // Check total length (5-50 characters)
    if (fullName.length < 5) {
      errors.push('Minimum 5 characters');
    }
    if (fullName.length > 50) {
      errors.push('Maximum 50 characters');
    }

    // Validate allowed characters (letters, spaces, hyphens, apostrophes)
    if (!/^[a-zA-Z '-]+$/.test(fullName)) {
      errors.push('Only letters, spaces, hyphens, and apostrophes are allowed');
    }

    return errors;
  };


  // Password validation function
  const validatePassword = (password: string): string[] => {
    const errors: string[] = [];
    if (password.length < 8) errors.push('Minimum 8 characters');
    if (password.length > 64) errors.push('Maximum 64 characters');
    if (!/[A-Z]/.test(password)) errors.push('Uppercase letter required');
    if (!/[a-z]/.test(password)) errors.push('Lowercase letter required');
    if (!/[0-9]/.test(password)) errors.push('Number required');
    if (!/[^A-Za-z0-9]/.test(password)) errors.push('Special character required (!@#$%^&*...)');
    return errors;
  };

  // Validate password match and required fields
  const isFormValid = computed(() => {
    return (
      formData.name&&
      formData.password &&
      formData.repeatPassword &&
      fullNameErrors.value.length === 0 &&
      passwordErrors.value.length === 0 &&
      !passwordMatchError.value 
    );
  });

  watch(
    () => formData.name,
    (newName) => {
      if (showValidation.value) {
        fullNameErrors.value = validateFullName(newName);
      }
    }
  );

  watch(
    () => formData.password,
    (newPassword) => {
      if (showValidation.value){
        passwordErrors.value = validatePassword(newPassword);
      }
    },
  );

  // Watch both password fields for match
  watch(
    [() => formData.password, () => formData.repeatPassword],
    ([pass, confirm]) => {
      if (showValidation.value && confirm) {
        passwordMatchError.value = pass === confirm ? null : 'Passwords do not match'; 
      } else {
        passwordMatchError.value = null;
      }
    },
    { immediate: true }
  );


  // Fetch invitation data on page load
  onMounted( async () => {
    if (!invitationToken.value) {
      router.push('/login');
      return;
    }

    try {
      const invitationData = await authStore.validateInvitation(invitationToken.value);
      invitationEmail.value = invitationData.email;
      invitationRole.value = invitationData.role;
    }catch (error) {
      console.error('Invalid invitation:', error);
      router.push('/login');
    }
  });
  
  const handleRegister = async () => {

    showValidation.value = true ;
    isSubmitting.value = true;

    fullNameErrors.value = validateFullName(formData.name);
    passwordErrors.value = validatePassword(formData.password);
    passwordMatchError.value = formData.password === formData.repeatPassword
      ? null
      : 'Passwords do not match';

    if(
      fullNameErrors.value.length > 0 ||
      passwordErrors.value.length > 0 || 
      passwordMatchError.value || 
      !isFormValid.value
    ){
      isSubmitting.value = false;
      return;
    } 

    try{
        await authStore.register(invitationToken.value,{
            name: formData.name,
            password: formData.password,
            repeatPassword: formData.repeatPassword
        });
        router.push({
          path: '/login',
          query: { registrationSuccess: 'true' }
        });
    }catch (error: any){
      if (error.response?.data) {
        const errorData = error.response.data;

        // Map backend errors to frontend state
        if (errorData.username) fullNameErrors.value = errorData.username;

        // Split password errors into field-specific messages
        if (errorData.password) {
          const passwordErrorsFromBackend = errorData.password;

          // Handle password mismatch error
          if (passwordErrorsFromBackend.includes('Passwords do not match')){
            passwordMatchError.value = 'Passwords do not match';
            passwordErrors.value = passwordErrorsFromBackend.filter(
              (err: string) => err !== 'Passwords do not match'
            );
          } else {
            passwordErrors.value = passwordErrorsFromBackend;
            passwordMatchError.value = null;
          }
        }
      } else {
        console.error('Registration failed:', error);
      }
    }finally {
        isSubmitting.value = false;
    }
  };
  </script>