<template>
  <nav class="bg-slate-800/50 backdrop-blur-sm border-b border-slate-700 sticky top-0 z-50">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex items-center h-16">
        <router-link to="/" class="flex items-center">
          <img :src="logoDark" alt="Imbus Knowledge Logo" class="h-14 w-auto" />
        </router-link>
      </div>
    </div>
  </nav>
</template>

<script setup lang="ts">
import logoDark from '../assets/logo-dark.png';
</script>