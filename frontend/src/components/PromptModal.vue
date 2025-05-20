<template>
  <div v-if="isOpen" class="fixed inset-0 flex items-center justify-center bg-black/50 backdrop-blur-sm z-50">
    <div 
      :class="[
        'relative max-w-sm w-full rounded-2xl p-6 shadow-xl',
        isDark ? 'bg-slate-800/90 border border-slate-700' : 'bg-white border border-slate-200'
      ]"
    >
      <!-- Close Button -->
      <button 
        @click="$emit('cancel')" 
        :class="[
          'absolute top-3 right-3 p-1 rounded-full transition-colors',
          isDark ? 'text-slate-400 hover:text-slate-300' : 'text-slate-500 hover:text-slate-600']"
      >
        <X class="w-5 h-5" />
      </button>

      <!-- Icon -->
      <div class="flex justify-center mb-4">
        <div 
          :class="[
            'p-4 rounded-full flex items-center justify-center',
            isDark ? 'bg-blue-900/30 text-blue-400' : 'bg-blue-100 text-blue-600'
          ]"
        >
          <AlertCircle class="w-8 h-8" />
        </div>
      </div>

      <!-- Title -->
      <h2 
        :class="[
          'text-xl font-medium mb-2 text-center transition-colors duration-200',
          isDark ? 'text-white' : 'text-slate-900'
        ]"
      >
        {{ title }}
      </h2>

      <!-- Message Input -->
      <div class="mb-4">
        <!-- Default Message Toggle -->
        <div class="flex items-center mb-4">
          <input 
            v-model="useDefaultMessage" 
            type="checkbox" 
            id="useDefaultMessage"
            :class="isDark ? 'text-emerald-500 focus:ring-emerald-500' : 'text-emerald-600 focus:ring-emerald-600'"
            class="w-4 h-4 rounded border-slate-600 focus:ring-2"
          />
          <label 
            for="useDefaultMessage" 
            :class="isDark ? 'text-slate-300' : 'text-slate-600'"
            class="ml-2 text-sm"
          >
            Use default message
          </label>
        </div>

        <!-- Message Textarea -->
        <textarea
          v-model="inputValue"
          rows="3"
          :placeholder="placeholder"
          :disabled="useDefaultMessage"
          :class="[
            'w-full px-4 py-3 rounded-lg placeholder-slate-400 focus:outline-none focus:ring-2 transition-colors',
            isDark 
              ? 'bg-slate-700/50 border-slate-600 text-white focus:ring-emerald-500' 
              : 'bg-slate-100/50 border-slate-200 text-slate-900 focus:ring-emerald-600'
          ]"
        ></textarea>

        <!-- Error Message -->
        <p v-if="errorMessage" :class="isDark ? 'text-red-400' : 'text-red-600'" class="mt-2 text-sm">
          {{ errorMessage }}
        </p>
      </div>

      <!-- Submit Button -->
      <div class="flex justify-end space-x-3">
        <button 
          @click="$emit('cancel')"
          :class="[
            'px-4 py-2 rounded-lg transition-colors',
            isDark 
              ? 'bg-slate-700/50 text-slate-300 hover:bg-slate-600/50' 
              : 'bg-slate-200/50 text-slate-600 hover:bg-slate-300/50']"
        >
          Cancel
        </button>
        <button 
          @click="submit"
          :disabled="!isValid"
          :class="[
            'px-4 py-2 rounded-lg transition-colors',
            isDark 
              ? 'bg-red-600 text-white hover:bg-red-700' 
              : 'bg-red-500 text-white hover:bg-red-600']"
        >
          Send Warning
        </button>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref, computed } from 'vue';
import { useThemeStore } from '../stores/theme';
import { useAuthStore } from '../stores/auth';
import { AlertCircle, X } from 'lucide-vue-next';

// Theme & Auth
const themeStore = useThemeStore();
const authStore = useAuthStore();
const isDark = computed(() => themeStore.isDark);
const adminName = computed(() => authStore.user?.name || 'System Admin');

// Props
defineProps({
  isOpen: {
    type: Boolean,
    required: true
  },
  title: {
    type: String,
    default: 'Send Warning'
  },
  placeholder: {
    type: String,
    default: 'Enter a custom warning message...'
  }
});

// Define emits
const emit = defineEmits(['submit', 'cancel']);

// Reactive State
const inputValue = ref('');
const useDefaultMessage = ref(true);
const errorMessage = ref('');

// Default Message (with admin name)
const DEFAULT_MESSAGE = computed(() => {
  return `This is a formal warning from ${adminName.value}.\r\n` +
         `Please review your recent activity and ensure it aligns with platform standards.`;
});

// Validation
const isValid = computed(() => {
  return useDefaultMessage.value || (inputValue.value.trim() !== '');
});

// Submit handler
const submit = () => {
  if (!isValid.value) {
    errorMessage.value = 'Warning message cannot be empty';
    return;
  }

  const messageToSend = useDefaultMessage.value 
    ? DEFAULT_MESSAGE.value 
    : inputValue.value;

  emit('submit', messageToSend);
  resetForm();
};

// Reset form
const resetForm = () => {
  inputValue.value = '';
  useDefaultMessage.value = true;
  errorMessage.value = '';
};
</script>