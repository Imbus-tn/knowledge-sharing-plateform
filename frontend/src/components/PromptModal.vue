<template>
    <div v-if="isOpen" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-slate-900 p-6 rounded-lg shadow-lg w-96">
        <h2 class="text-xl font-bold mb-4 text-slate-200">{{ title }}</h2>
        <textarea
          v-model="inputValue"
          rows="3"
          class="w-full px-4 py-3 bg-slate-700/50 border border-slate-600 rounded-lg text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-emerald-500 mb-4"
          :placeholder="placeholder"
        ></textarea>
        <div class="flex justify-end space-x-3">
          <button 
            @click="cancel" 
            class="px-4 py-2 bg-slate-700 text-slate-300 rounded hover:bg-slate-600 transition-colors"
          >
            Cancel
          </button>
          <button 
            @click="submit" 
            class="px-4 py-2 bg-emerald-500 text-white rounded hover:bg-emerald-600 transition-colors"
          >
            Submit
          </button>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue';
  
  const props = defineProps({
    isOpen: Boolean,
    title: String,
    placeholder: String
  });
  
  const emit = defineEmits(['submit', 'cancel']);
  const inputValue = ref('');
  
  const submit = () => {
    emit('submit', inputValue.value);
    inputValue.value = ''; // Reset input
  };
  
  const cancel = () => {
    emit('cancel');
    inputValue.value = ''; // Reset input
  };
  </script>