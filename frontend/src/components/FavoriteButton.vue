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
import { useThemeStore } from '../stores/theme'
import { useFavoritesStore } from '../stores/favorites'
import { apiClient } from '../api';
const props = defineProps<{
  item: {
    id: number
    title?: string
    description?: string
    coverImage?: string
    type?: string
    category?: string
    createdAt?: string
    authorId?: string
    likes?: number
    comments?: number
    shares?: number
    isFavorite?: boolean
  }
}>()

const themeStore = useThemeStore()
const favoritesStore = useFavoritesStore()

const isDark = computed(() => themeStore.isDark)

const isFavorite = computed(() => {
  return favoritesStore.items.some(fav => fav.id === props.item.id)
})

const toggleFavorite = async () => {
  const favorited = isFavorite.value;

  if (!favorited) {
    // Add to favorites
    try {
      await apiClient.post(`/content/favorites/${props.item.id}`);
      await favoritesStore.loadFavoritesFromAPI(); // Refresh list
    } catch (err) {
      console.error('Failed to add favorite:', err);
    }
  } else {
    // Remove from favorites
    try {
      await apiClient.delete(`/content/favorites/${props.item.id}`);
      favoritesStore.items = favoritesStore.items.filter(fav => fav.id !== props.item.id);
    } catch (err) {
      console.error('Failed to remove favorite:', err);
    }
  }
};
</script>