import { defineStore } from 'pinia'
import axios from 'axios'
import type { FavoriteItem } from '../types/Favorite'

export const useFavoritesStore = defineStore('favorites', {
  state: () => ({
    items: [] as FavoriteItem[],
    loading: false,
    error: null as string | null
  }),

  actions: {
    async loadFavoritesFromAPI() {
      this.loading = true
      this.error = null
      try {
        const res = await axios.get('/api/content/posts/favorites')
        this.items = res.data.content
      } catch (err) {
        this.error = 'Failed to load favorites'
        console.error(err)
      } finally {
        this.loading = false
      }
    },

    async toggleFavorite(post: FavoriteItem) {
      try {
        await axios.post(`/api/content/posts/${post.id}/favorite`, {})
        await this.loadFavoritesFromAPI()
        return true
      } catch (err) {
        console.error('Failed to toggle favorite:', err)
        return false
      }
    },

    async removeFavorite(postId: string) {
      try {
        await axios.delete(`/api/content/posts/${postId}/favorite`)
        this.items = this.items.filter(item => item.id !== postId)
      } catch (err) {
        console.error('Failed to remove favorite:', err)
      }
    }
  }
})