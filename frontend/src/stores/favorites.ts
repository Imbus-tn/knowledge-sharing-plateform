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
    generateMockFavorites() {
    this.items = [
      {
        id: 'mock1',
        title: 'Vue 3 Fundamentals',
        description: 'A beginner-friendly guide to Vue 3.',
        coverImage: '/images/vue.jpg',
        authorId: 'user1',
        likes: 42,
        comments: 5,
        shares: 8
      },
      {
        id: 'mock2',
        title: 'TypeScript Tips',
        description: 'How to avoid common mistakes when writing TypeScript.',
        coverImage: '/images/ts.jpg',
        authorId: 'user2',
        likes: 67,
        comments: 9,
        shares: 12
      }
    ]
  },
    async loadFavoritesFromAPI() {
      this.loading = true
      this.error = null
      try {
        const res = await axios.get('/api/content/posts/favorites')
        this.items = res.data.content || []
      } catch (err) {
        this.error = 'Failed to load favorites'
        console.error(err)
      } finally {
        this.loading = false
      }
    },

    async toggleFavorite(post: Partial<FavoriteItem>) {
      try {
        await axios.post(`/api/content/posts/${post.id}/favorite`, {})
        await this.loadFavoritesFromAPI()
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