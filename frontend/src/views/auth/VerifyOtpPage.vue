<template>
    <div class="min-h-screen flex flex-col">
      <div class="flex-1 flex items-center justify-center p-4">
        <div class="w-full max-w-md">
          <div class="text-center mb-8">
            <h2 class="text-3xl font-bold text-white mb-2">Verify OTP</h2>
            <p class="text-slate-400">Enter the code sent to your email</p>
          </div>
  
          <div class="bg-slate-800/50 backdrop-blur-sm rounded-lg border border-slate-700 p-6">
            <form @submit.prevent="handleSubmit" class="space-y-6">
              <!-- OTP Validation Errors -->
              <div v-if="otpErrors.length" class="text-center mt-2 text-slate-400 text-sm">
                  {{ otpErrors.join(', ') }}
              </div>
              <div>
                <label class="block text-sm font-medium text-slate-300 mb-4 text-center">
                  Enter verification code
                </label>
                <div class="flex justify-center gap-2">
                  <input
                    v-for="(_, index) in otpDigits"
                    :key="index"
                    v-model="otpDigits[index]"
                    type="text"
                    maxlength="1"
                    :ref="el => { if (el) otpRefs[index] = el as HTMLInputElement } "
                    @input="handleInput(index)"
                    @keydown="handleKeydown($event, index)"
                    @paste="handlePaste"
                    class="w-12 h-14 text-center text-xl font-semibold bg-slate-700/50 border border-slate-600 rounded-lg text-white focus:border-emerald-500 focus:ring-2 focus:ring-emerald-500 focus:outline-none transition-colors"
                  />
                </div>
                <p class="mt-4 text-center text-sm text-slate-400">
                  Didn't receive the code? 
                  <button 
                    type="button"
                    @click="resendCode" 
                    :disabled="resendTimer > 0"
                    class="text-emerald-500 hover:text-emerald-400 disabled:text-slate-500 disabled:cursor-not-allowed ml-1"
                  >
                    {{ resendTimer > 0 ? `Resend in ${resendTimer}s` : 'Resend code' }}
                  </button>
                </p>
              </div>
  
              <button
                type="submit"
                :disabled="!isComplete"
                class="w-full flex items-center justify-center px-4 py-3 border border-transparent rounded-lg shadow-sm text-sm font-medium text-white bg-emerald-500 hover:bg-emerald-600 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-emerald-500 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
              >
                <CheckCircleIcon class="w-5 h-5 mr-2" />
                Verify Code
              </button>
            </form>
  
            <div class="mt-6 text-center">
              <router-link to="/forgot-password" class="text-sm text-emerald-500 hover:text-emerald-400">
                Try different email
              </router-link>
            </div>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script setup lang="ts">
  import { ref, computed, onMounted, onUnmounted } from 'vue';
  import { CheckCircleIcon } from '@heroicons/vue/24/solid';
  import { useRouter, useRoute } from 'vue-router';
  import { apiClient } from '../../api';
  
 
  const router = useRouter();
  const route = useRoute();

  const otpDigits = ref(['', '', '', '', '', '']);
  const otpRefs = ref<(HTMLInputElement | null)[]>([]);
  const otpErrors = ref<string[]>([])
  const resendTimer = ref(0);
  let resendInterval: number | null = null;
  
  const email = computed(() => route.query.email as string);
  const isComplete = computed(() => otpDigits.value.every(digit => digit !== ''));
  
  const handleInput = (index: number) => {
    const value = otpDigits.value[index];

    // Ensure only numeric input
    if (!/^\d$/.test(value)) {
      otpDigits.value[index] = '';
      return;
    }

    //Auto-focus next input
    if (value && index <5) {
      otpRefs.value[index + 1]?.focus();
    }
    
  };
  
  const handleKeydown = (event: KeyboardEvent, index: number) => {
    // Handle backspace
    if (event.key === 'Backspace' && !otpDigits.value[index] && index > 0) {
      otpDigits.value[index - 1] = '';
      otpRefs.value[index - 1]?.focus();
    }
  };
  
  const handlePaste = (event: ClipboardEvent) => {
    event.preventDefault();
    const pastedData = event.clipboardData?.getData('text');
    if (!pastedData) return;
  
    // Get only numbers from pasted content
    const numbers = pastedData.replace(/\D/g, '').split('').slice(0, 6);
    numbers.forEach((num, index) => {
      if (index < otpDigits.value.length) {
        otpDigits.value[index] = num;
      }
    });
    otpRefs.value[numbers.length - 1]?.focus();
  };
  
  const startResendTimer = () => {
    resendTimer.value = 30;
    resendInterval = setInterval(() => {
      resendTimer.value--;
      if (resendTimer.value === 0) clearInterval(resendInterval!);
    }, 1000);
  };
  
  const resendCode = async () => {
    try {
      await apiClient.post(`/forgotPassword/verifyMail/${email.value}`);
      startResendTimer();
    }catch (error: any){
      console.error('Error resending OTP:', error.response?.data);
    }
  };
  
  const handleSubmit = async() => {
    try{
      otpErrors.value = [];
      const otp = otpDigits.value.join('');
      await apiClient.post(`/forgotPassword/verifyOtp/${otp}/${email.value}`);
      router.push({
        name: 'reset-password',
        query: {email: email.value}
      });
    }catch(error: any){
      console.error('Error response:', error.response?.data); 
      if (error.response && error.response.data) {
        const errors = error.response.data.error || ['An unexpected error occurred'];
        otpErrors.value = Array.isArray(errors) ? errors : [errors];;
      }else {
        otpErrors.value = ['An unexpected error occurred'];
      }
    }
  };
  
  onMounted(() => {
    // Focus first input on mount
    otpRefs.value[0]?.focus();
    startResendTimer();
  });
  
  onUnmounted(() => {
    if (resendInterval) {
      clearInterval(resendInterval);
    }
  });
  </script>