<template>
  <div v-if="isOpen" class="fixed inset-0 flex items-center justify-center bg-black/50">
    <div class="bg-white rounded-2xl p-6 max-w-sm w-full"> 
      <!-- Close Button -->
      <button 
        @click="$emit('cancel')"
        class="absolute top-3 right-3 text-gray-400 hover:text-gray-500"
      >
        <X class="w-5 h-5" />
      </button>
      <!-- Icon -->
      <div class="flex justify-center mb-4">
        <div class="p-4 bg-blue-100 rounded-full">
          <AlertTriangle class="w-8 h-8 text-red-600" />
        </div>
      </div>
      <!-- Title -->
      <h2 class="text-xl font-medium mb-2 text-center">{{ title }}</h2>
      <!-- Message -->
      <p class="text-gray-600 mb-6 text-center">{{ message }}</p>
      <!-- Buttons -->
      <div class="flex justify-center space-x-3"> <!-- Center buttons -->
        <button 
          @click="$emit('cancel')"
          class="px-8 py-2 border border-gray-600 rounded-2xl hover:bg-gray-100" 
        >
          Cancel
        </button>
        <button 
          @click="$emit('confirm')"
          class="px-8 py-2 bg-red-500 text-white rounded-2xl hover:bg-red-600" 
        >
          Confirm
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { X, AlertTriangle } from 'lucide-vue-next';

defineProps({
  isOpen: Boolean,
  title: String,
  message: String,
});

defineEmits(['confirm', 'cancel']);
</script>