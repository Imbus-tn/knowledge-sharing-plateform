<template>
    <div v-if="isOpen" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-slate-900 p-6 rounded-lg shadow-lg w-96">
        <h2 class="text-xl font-bold mb-4 text-slate-200">{{ title }}</h2>
        <p class="text-slate-400 mb-6">{{ message }}</p>
        <div class="flex justify-end space-x-3">
          <button 
            @click="cancel" 
            class="px-4 py-2 bg-slate-700 text-slate-300 rounded hover:bg-slate-600 transition-colors"
          >
            Cancel
          </button>
          <button 
            @click="confirm" 
            class="px-4 py-2 bg-red-500 text-white rounded hover:bg-red-600 transition-colors"
          >
            Confirm
          </button>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  const props = defineProps({
    isOpen: Boolean,
    title: String,
    message: String
  });
  
  const emit = defineEmits(['confirm', 'cancel']);
  
  const confirm = () => {
    emit('confirm');
  };
  
  const cancel = () => {
    emit('cancel');
  };
  </script>