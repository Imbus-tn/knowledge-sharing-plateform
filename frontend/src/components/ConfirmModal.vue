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
          'absolute top-3 right-3 transition-colors',
          isDark ? 'text-slate-400 hover:text-slate-300' : 'text-slate-500 hover:text-slate-600'
        ]"
        class="p-1 rounded-full"
      >
        <X class="w-5 h-5" />
      </button>

      <!-- Icon -->
      <div class="flex justify-center mb-4">
        <div 
          :class="[
            'p-4 rounded-full transition-colors',
            isDark ? 'bg-blue-900/30 text-blue-400' : 'bg-blue-100 text-blue-600'
          ]"
        >
          <AlertTriangle class="w-8 h-8" />
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

      <!-- Message -->
      <p 
        :class="[
          'mb-6 text-center transition-colors duration-200',
          isDark ? 'text-slate-300' : 'text-slate-600'
        ]"
      >
        {{ message }}
      </p>

      <!-- Buttons -->
      <div class="flex justify-center space-x-3">
        <!-- Cancel Button -->
        <button 
          @click="$emit('cancel')"
          :class="[
            'px-6 py-2 rounded-2xl transition-colors',
            isDark 
              ? 'bg-slate-700/50 text-slate-300 hover:bg-slate-700 hover:text-white' 
              : 'bg-slate-100 text-slate-600 hover:bg-slate-200/50 hover:text-slate-900'
          ]"
        >
          Cancel
        </button>

        <!-- Confirm Button -->
        <button 
          @click="$emit('confirm')"
          :class="[
            'px-6 py-2 rounded-2xl transition-colors',
            isDark 
              ? 'bg-red-600 text-white hover:bg-red-700' 
              : 'bg-red-500 text-white hover:bg-red-600'
          ]"
        >
          Confirm
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useThemeStore } from '../stores/theme';
import { X, AlertTriangle } from 'lucide-vue-next';

const themeStore = useThemeStore();
const isDark = computed(() => themeStore.isDark);

defineProps({
  isOpen: Boolean,
  title: String,
  message: String,
});

defineEmits(['confirm', 'cancel']);
</script>