<template>
    <div class="relative max-w-2xl mx-auto group">
      <Search class="absolute left-3 top-1/2 transform -translate-y-1/2 text-slate-400 group-focus-within:text-emerald-500 transition-colors" />
      <input 
        type="text" 
        :value="modelValue"
        @input="$emit('update:modelValue', ($event.target as HTMLInputElement).value)"
        placeholder="Search for tutorials, guides, or discussions..."
        class="w-full pl-10 pr-12 py-3.5 bg-slate-800/50 border border-slate-700 rounded-xl text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-emerald-500/50 focus:border-emerald-500 transition-all duration-200" 
      />
      <kbd class="hidden md:flex absolute right-3 top-1/2 transform -translate-y-1/2 items-center space-x-1 px-2 py-0.5 text-xs text-slate-400 bg-slate-700/50 rounded border border-slate-600">
        <span>⌘</span>
        <span>K</span>
      </kbd>
    </div>
  </template>
  
  <script setup lang="ts">
  import { Search } from 'lucide-vue-next';
  
  defineProps<{
    modelValue: string;
  }>();
  
  defineEmits<{
    (e: 'update:modelValue', value: string): void;
  }>();
  </script>