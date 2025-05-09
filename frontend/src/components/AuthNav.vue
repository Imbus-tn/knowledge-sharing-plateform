<template>
    <nav class="bg-slate-800/50 backdrop-blur-sm border-b border-slate-700 sticky top-0 z-50">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex items-center h-16">
          <router-link to="/" class="flex items-center space-x-3">
            <BookOpen class="w-8 h-8 text-emerald-500" />
            <span class="text-xl font-bold text-white">Imbus Knowledge</span>
          </router-link>
        </div>
      </div>
    </nav>
  </template>
  
  <script setup lang="ts">
  import { BookOpen } from 'lucide-vue-next';
  </script>