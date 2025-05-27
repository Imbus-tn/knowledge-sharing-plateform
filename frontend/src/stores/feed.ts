import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import { apiClient } from '../api'
import { useAuthStore } from './auth'
import type { Post } from '../types/post'
import { UserRole } from '../types/UserRole'

export const useFeedStore = defineStore('feed', () => {
  // State
  const authStore = useAuthStore();
  const posts = ref<Post[]>([])
  const loading = ref<boolean>(false)
  const error = ref<string | null>(null)

  // Getters
  const getPostById = computed(() => {
    return (postId: string) => {
      return posts.value.find((post: Post) => post.id === postId)
    }
  })

  // Actions

  // Fetch all posts
  const fetchPosts = async (): Promise<void> => {
    try {
      loading.value = true;
      const response = await apiClient.get('/content/posts');

      let postData: Post[] = [];

      if (Array.isArray(response.data)) {
        postData = response.data.map(post => ({
          ...post,
          id: String(post.id),
          reactions: post.reactions || [],
          favorites: post.favorites || [],
          comments: post.comments || [],
          shares: post.shares || [],
          author: {
            id: post.authorId ?? 'unknown',
            name: post.authorName ?? 'Anonymous',
            email: post.authorEmail ?? '',
            role: post.authorRole ?? UserRole.USER
          }
        }));
      } else if (response.data && Array.isArray(response.data.content)) {
        postData = response.data.content.map(post => ({
          id: String(post.id),
          content: post.content || '',
          imageUrl: post.imageUrl || undefined,
          author: {
            id: post.author.id ?? 'unknown',
            name: post.author.name ?? 'Anonymous',
            email: post.author.email ?? '',
            role: post.author.role ?? UserRole.USER,
            avatarUrl: post.author.avatarUrl
          },
          createdAt: post.createdAt || new Date().toISOString(),
          reactions: post.reactions || [],
          favorites: post.favorites || [],
          comments: post.comments || [],
          shares: post.shares || []
        }));
      } else {
        postData = [];
      }

      postData.sort((a, b) => {
        const dateA = new Date(a.createdAt).getTime();
        const dateB = new Date(b.createdAt).getTime();
        return dateB - dateA;
      });

      posts.value = postData;
    } catch (err: any) {
      error.value = err.message || 'Failed to load posts';
      console.error("Error fetching posts:", err);
    } finally {
      loading.value = false;
    }
  };

  // Create a new post
  const createPost = async (postData: { content: string, imageUrl?: string }): Promise<void> => {
    try {
      loading.value = true
      const response = await apiClient.post('/content/posts', postData)

      const backendPost = response.data

      // ✅ Construct a valid Post object with safe ID and fallbacks
      const newPost: Post = {
        id: String(backendPost.id),
        content: backendPost.content || postData.content,
        imageUrl: backendPost.imageUrl || postData.imageUrl || undefined,
        author: {
          id: backendPost.authorId ?? authStore.user?.id ?? 'unknown',
          name: backendPost.authorName || authStore.user?.name || 'Anonymous',
          email: backendPost.authorEmail || authStore.user?.email || '',
          role: authStore.user?.role || UserRole.USER,
          avatarUrl: backendPost.author?.avatarUrl ?? authStore.user?.avatarUrl ?? undefined
        },
        createdAt: backendPost.createdAt || new Date().toISOString(),
        updatedAt: undefined,
        reactions: backendPost.reactions || [],
        favorites: backendPost.favorites || [],
        comments: backendPost.comments || [],
        shares: backendPost.shares || []
      };

      // ✅ Validate minimal required fields
      if (newPost.id && newPost.content) {
        posts.value.unshift(newPost)
      } else {
        throw new Error('Incomplete post data from server')
      }
    } catch (err: any) {
      error.value = err.message || 'Failed to create post.'
      console.error("Error creating post:", err)
      throw err
    } finally {
      loading.value = false
    }
  }

  // Update existing post
  const updatePost = async (postId: string, content: string, imageUrl?: string): Promise<void> => {
    try {
      loading.value = true
      const data = { content, ...(imageUrl && { imageUrl }) }
      const response = await apiClient.put(`/content/posts/${postId}`, data)

      const updatedPost = {
        ...response.data,
        id: String(response.data.id),
        reactions: response.data.reactions || [],
        favorites: response.data.favorites || [],
        comments: response.data.comments || [],
        shares: response.data.shares || []
      }

      const index = posts.value.findIndex((p: Post) => p.id === postId)
      if (index !== -1) {
        posts.value[index] = updatedPost
      }
    } catch (err: any) {
      error.value = err.message || 'Failed to update post.'
      console.error("Error updating post:", err)
      throw err
    } finally {
      loading.value = false
    }
  }

  // Delete a post
  const deletePost = async (postId: string): Promise<void> => {
    try {
      loading.value = true
      await apiClient.delete(`/content/posts/${postId}`)
      posts.value = posts.value.filter(post => post.id !== postId)
    } catch (err: any) {
      error.value = err.message || 'Failed to delete post.'
      console.error("Error deleting post:", err)
      throw err
    } finally {
      loading.value = false
    }
  }

  // Toggle favorite
  const toggleFavorite = async (postId: string): Promise<void> => {
    try {
      await apiClient.post(`/content/posts/${postId}/favorite`, {})
      const post = posts.value.find((p: Post) => p.id === postId)
      if (post) {
        post.isFavorite = !post.isFavorite
      }
    } catch (err: any) {
      error.value = err.message || 'Failed to toggle favorite.'
      console.error("Error toggling favorite:", err)
      throw err
    }
  }

  // React to post
  const reactToPost = async (postId: string, reactionType: string): Promise<void> => {
    try {
      const response = await apiClient.post(`/content/posts/${postId}/react`, { type: reactionType })
      const newReaction = response.data

      const post = posts.value.find((p: Post) => p.id === postId)
      if (post && newReaction) {
        post.reactions.push(newReaction)
      }
    } catch (err: any) {
      error.value = err.message || 'Failed to react to post.'
      console.error("Error reacting to post:", err)
      throw err
    }
  }

  // Add comment
  const addComment = async (postId: string, text: string): Promise<void> => {
    try {
      loading.value = true
      const response = await apiClient.post(`/content/posts/${postId}/comment`, { text })
      const newComment = response.data

      const post = posts.value.find((p: Post) => p.id === postId)
      if (post && newComment) {
        post.comments.push(newComment)
      }
    } catch (err: any) {
      error.value = err.message || 'Failed to add comment.'
      console.error("Error adding comment:", err)
      throw err
    } finally {
      loading.value = false
    }
  }

  // Report post
  const reportPost = async (postId: string, reason: string): Promise<void> => {
    try {
      await apiClient.post(`/content/posts/${postId}/report`, { reason })
    } catch (err: any) {
      error.value = err.message || 'Failed to report post.'
      console.error("Error reporting post:", err)
      throw err
    }
  }

  // Share post
  const sharePost = async (postId: string): Promise<void> => {
    try {
      await apiClient.post(`/content/posts/${postId}/share`, {})
    } catch (err: any) {
      error.value = err.message || 'Failed to share post.'
      console.error("Error sharing post:", err)
      throw err
    }
  }

  // Reply to comment
  const replyToComment = async (commentId: string, text: string): Promise<void> => {
    try {
      await apiClient.post(`/content/comments/${commentId}/reply`, { text })
    } catch (err: any) {
      error.value = err.message || 'Failed to reply to comment.'
      console.error("Error replying to comment:", err)
      throw err
    }
  }

  // React to comment
  const reactToComment = async (commentId: string, reactionType: string): Promise<void> => {
    try {
      await apiClient.post(`/content/comments/${commentId}/react`, { type: reactionType })
    } catch (err: any) {
      error.value = err.message || 'Failed to react to comment.'
      console.error("Error reacting to comment:", err)
      throw err
    }
  }

  return {
    posts,
    loading,
    error,
    getPostById,
    fetchPosts,
    createPost,
    updatePost,
    deletePost,
    toggleFavorite,
    reactToPost,
    addComment,
    replyToComment,
    reactToComment,
    reportPost,
    sharePost
  }
})