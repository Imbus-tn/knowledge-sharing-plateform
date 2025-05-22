<template>
    <button 
      @click="toggleFavorite"
      class="flex items-center justify-center p-2 rounded-full transition-colors duration-200 focus:outline-none"
      :class="[
        isFavorite 
          ? 'text-amber-400 bg-amber-500/10' 
          : isDark 
            ? 'text-slate-400 hover:text-slate-300 hover:bg-slate-700/50' 
            : 'text-slate-500 hover:text-slate-700 hover:bg-slate-200/70'
      ]"
      aria-label="Toggle Favorite"
    >
      <Star class="w-5 h-5" :class="{ 'fill-current': isFavorite }" />
    </button>
  </template>
  
  <script setup lang="ts">
  import { computed } from 'vue'
  import { useThemeStore } from '../stores/theme';
  import { useFavoritesStore } from '../stores/favorites';
  
  const props = defineProps<{
    item: {
        id: string
        title?: string
        description?: string
        coverImage?: string
        type?: string
        category?: string
        createdAt?: string
        authorId: string
    }
  }>()
  
  const themeStore = useThemeStore()
  const favoritesStore = useFavoritesStore()
  
  const isDark = computed(() => themeStore.isDark)
  const isFavorite = computed(() => favoritesStore.isFavorite(props.item.id))
  
  function toggleFavorite() {
    if (isFavorite.value) {
      favoritesStore.removeFavorite(props.item.id)
    } else {
      favoritesStore.addFavorite(props.item)
    }
  }
  </script>