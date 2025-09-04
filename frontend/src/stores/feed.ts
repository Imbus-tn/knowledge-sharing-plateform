// src/stores/feed.ts
import { ref, computed } from 'vue';
import { defineStore } from 'pinia';
import { apiClient } from '../api';
import { useAuthStore } from './auth';
import type { Post } from '../types/post';
import { UserRole } from '../types/UserRole';
import { useNotificationStore } from './notification';

export const useFeedStore = defineStore('feed', () => {
  const authStore = useAuthStore();
  const posts = ref<Post[]>([]);
  const loading = ref<boolean>(false);
  const error = ref<string | null>(null);
  const notificationStore = useNotificationStore();
  const recommendedPosts = ref<Post[]>([]);
const searchQuery = ref('');
const setSearchQuery = (query: string) => {
  searchQuery.value = query;
};

  const getPostById = computed(() => {
    return (postId: number) => {
      return posts.value.find((post: Post) => post.id === postId);
    };
  });

const getInitials = (name: string | undefined): string => {
  if (!name) return 'U';
  return name
    .split(' ')
    .map(n => n[0].toUpperCase())
    .join('')
    .slice(0, 2);
};

  // ✅ Fetch all posts
  const fetchPosts = async (): Promise<void> => {
  try {
    loading.value = true;
    error.value = null;

    const response = await apiClient.get('/content/posts');
    let rawData = response.data;

    // Handle paginated response
    const data = Array.isArray(rawData) ? rawData : rawData.content || [];

    if (!Array.isArray(data)) {
      console.error('Invalid data format:', rawData);
      error.value = 'Server returned invalid data.';
      return;
    }

    const mappedPosts = data.map((post: any) => {
      const author = post.author || {};
      if (!author.name) {
        console.warn(`Post ${post.id} has incomplete author data:`, author);
      }

      return {
        id: post.id,
        content: post.content || '',
        title: post.title || '',
        description: post.description || '',
        imageUrl: post.imageUrl || undefined,
        category: post.category || undefined,
        tags: Array.isArray(post.tags) ? post.tags : [],
        createdAt: post.createdAt || new Date().toISOString(),
        viewCount: typeof post.viewCount === 'number' ? post.viewCount : 0,
        likeCount: typeof post.likeCount === 'number' ? post.likeCount : 0,
        shareCount: typeof post.shareCount === 'number' ? post.shareCount : 0,
        isFavorite: Boolean(post.isFavorite),
        reactions: Array.isArray(post.reactions) ? post.reactions : [],
        comments: Array.isArray(post.comments) ? post.comments : [],
        favorites: Array.isArray(post.favorites) ? post.favorites : [],
        shares: Array.isArray(post.shares) ? post.shares : [],
        author: {
          id: author.id ?? 0,
          name: author.name ?? 'Anonymous',
          email: author.email ?? '',
          role: author.role ?? UserRole.USER,
          avatarUrl: author.avatarUrl ?? undefined,
          initials: author.initials ?? getInitials(author.name ?? 'Anonymous')
        }
      };
    });

    posts.value = mappedPosts;
    loading.value = false;
  } catch (err: any) {
    error.value = err.message || 'Failed to load posts';
    console.error('Error fetching posts:', err);
    loading.value = false;
  }
};
  // ✅ Create a new post
const createPost = async (postData: {
  content: string;
  imageUrl: string | null;
  title?: string;
  description?: string;
  category?: string;
  tags?: string[];
}): Promise<void> => {
  try {
    loading.value = true;

    const formData = new FormData();
    formData.append('content', postData.content);
    if (postData.title) formData.append('title', postData.title);
    if (postData.description) formData.append('description', postData.description);
    if (postData.category) formData.append('category', postData.category);
    if (postData.tags) {
      postData.tags.forEach(tag => formData.append('tags', tag));
    }

    if (postData.imageUrl) {
      const blob = await fetch(postData.imageUrl).then(r => r.blob());
      formData.append('imageUrl', blob, 'upload.jpg');
    }

    const response = await apiClient.post('/content/posts', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });

    const backendPost = response.data;
    posts.value.unshift(backendPost);
  } catch (err: any) {
    error.value = err.message || 'Failed to create post.';
    console.error('Error:', err);
    throw err;
  } finally {
    loading.value = false;
  }
};

  // ✅ Update post
  const updatePost = async (postId: number, content: string, imageUrl?: string): Promise<void> => {
    try {
      loading.value = true;
      const data = { content, ...(imageUrl && { imageUrl }) };
      const response = await apiClient.put(`/content/posts/${postId}`, data);
      const updatedPost = {
        ...response.data,
        id: response.data.id,
        reactions: response.data.reactions || [],
        favorites: response.data.favorites || [],
        comments: response.data.comments || [],
        shares: response.data.shares || [],
      };

      const index = posts.value.findIndex((p: Post) => p.id === postId);
      if (index !== -1) {
        posts.value[index] = updatedPost;
      }
    } catch (err: any) {
      error.value = err.message || 'Failed to update post.';
      console.error('Error updating post:', err);
      throw err;
    } finally {
      loading.value = false;
    }
  };
// feedStore.ts
const checkForNewPosts = async (since: string): Promise<Post[]> => {
  try {
    const response = await apiClient.get('/content/posts/latest', {
      params: { since, limit: 20 }
    });
    return response.data;
  } catch (err: any) {
    console.error('Failed to check for new posts:', err);
    return [];
  }
};
  // ✅ Delete post
  const deletePost = async (postId: number): Promise<void> => {
    try {
      loading.value = true;
      await apiClient.delete(`/content/posts/${postId}`);
      posts.value = posts.value.filter(post => post.id !== postId);
    } catch (err: any) {
      error.value = err.message || 'Failed to delete post.';
      console.error('Error deleting post:', err);
      throw err;
    } finally {
      loading.value = false;
    }
  };

  // ✅ Toggle favorite
const toggleFavorite = async (postId: number): Promise<void> => {
  try {
    loading.value = true;
    await apiClient.post(`/content/posts/${postId}/favorite`);
    const post = posts.value.find(p => p.id === postId);
    if (post) {
      post.isFavorite = !post.isFavorite;
    }
  } catch (err: any) {
    error.value = err.message || 'Failed to toggle favorite.';
    console.error('Error:', err);
    throw err;
  } finally {
    loading.value = false;
  }
};
  // ✅ Remove favorite
  const removeFavorite = async (postId: number): Promise<void> => {
    try {
      await apiClient.delete(`/content/posts/${postId}/favorite`);
      posts.value = posts.value.filter(post => post.id !== postId);
    } catch (err: any) {
      error.value = err.message || 'Failed to remove favorite.';
      console.error('Error removing favorite:', err);
      throw err;
    }
  };

  // ✅ React to post
  const reactToPost = async (postId: number, reactionType: string): Promise<void> => {
  try {
    loading.value = true;
    const response = await apiClient.post(`/content/posts/${postId}/react`, { type: reactionType  });
    
    // Update the post's reactions with the server response
    const post = posts.value.find(p => p.id === postId);
    if (post && response.data) {
      post.reactions = response.data.reactions || post.reactions;
    }
  } catch (err: any) {
    console.error("Error reacting to post:", err);
    notificationStore.addNotification({
      type: 'error',
      message: 'Failed to react to post.',
      link: '/feed'
    });
    throw err;
  } finally {
    loading.value = false;
  }
};

  // ✅ Add comment
const addComment = async (postId: number, text: string): Promise<void> => {
  try {
    loading.value = true;
    const response = await apiClient.post(`/content/posts/${postId}/comment`, { text });
    const newComment = response.data;

    const post = posts.value.find(p => p.id === postId);
    if (post && newComment) {
      // ✅ Ensure comments array exists
      if (!post.comments) post.comments = [];

      post.comments.push({
        id: newComment.id,
        text: newComment.text,
        author: {
          id: newComment.author?.id ?? authStore.user?.id ?? 0,
          name: newComment.author?.name ?? authStore.user?.name ?? 'Anonymous',
          email: newComment.author?.email ?? '',
          role: newComment.author?.role ?? UserRole.USER,
          avatarUrl: newComment.author?.avatarUrl ?? undefined,
          initials: newComment.author?.initials ?? getInitials(newComment.author?.name)
        },
        createdAt: newComment.createdAt || new Date().toISOString(),
        reactions: newComment.reactions || []
      });
    }
  } catch (err: any) {
    error.value = err.message || 'Failed to add comment.';
    console.error('Error adding comment:', err);
    throw err;
  } finally {
    loading.value = false;
  }
};
  
// ✅ Fixed: Handle optional reactions safely
const reactToComment = async (commentId: number, reactionType: string): Promise<void> => {
  try {
    loading.value = true;

    // Call API
    await apiClient.post(`/content/comments/${commentId}/react`, {
      type: reactionType,
    });

    // Find post and comment
    const post = posts.value.find((p) => p.comments.some((c) => c.id === commentId));
    if (!post) return;

    const comment = post.comments.find((c) => c.id === commentId);
    if (!comment) return;

    // ✅ Ensure reactions array exists
    if (!comment.reactions) {
      comment.reactions = [];
    }

    // Find existing reaction
    const existingReaction = comment.reactions.find((r) => r.emoji === reactionType);

    const currentUserId = String(authStore.user?.id);

    if (existingReaction) {
      // Increment count
      existingReaction.count = (existingReaction.count ?? 1) + 1;
      if (!existingReaction.users) {
        existingReaction.users = [];
      }
      if (!existingReaction.users.includes(currentUserId)) {
        existingReaction.users.push(currentUserId);
      }
    } else {
      // Add new reaction
      comment.reactions.push({
        emoji: reactionType,
        count: 1,
        users: [currentUserId],
        createdAt: new Date().toISOString(),
      });
    }
  } catch (err: any) {
    error.value = err.message || 'Failed to react to comment';
    console.error('Error reacting to comment:', err);
    notificationStore.addNotification({
      type: 'error',
      message: 'Failed to react to comment.',
      link: '/feed'
    });
  } finally {
    loading.value = false;
  }
};
const createSharedPost = async (postData: {
  title: string;
  description: string;
  content: string;
  imageUrl: string | null;
  linkUrl: string;
  linkPreview: any;
  category: string;
  tags: string[];
}): Promise<void> => {
  try {
    loading.value = true;
    const response = await apiClient.post('/content/posts', postData);
    posts.value.unshift(response.data);
  } catch (err: any) {
    error.value = err.message || 'Failed to share link.';
    throw err;
  } finally {
    loading.value = false;
  }
};

  // ✅ Fetch recommended posts
  const fetchRecommendedPosts = async (): Promise<void> => {
  try {
    loading.value = true;
    const response = await apiClient.get('/recommend/feed');

    if (Array.isArray(response.data?.recommendations)) {
      recommendedPosts.value = response.data.recommendations.map((rec: any) => {
        const author = rec.author || {};
        if (!author.name) {
          console.warn(`Recommended post ${rec.id} has incomplete author data:`, author);
        }
        return {
          id: rec.content_id || rec.id,
          title: rec.title || '',
          description: rec.description || '',
          content: rec.content || '',
          imageUrl: rec.image_url || undefined,
          category: rec.category || undefined,
          viewCount: typeof rec.view_count === 'number' ? rec.view_count : 0,
          likeCount: typeof rec.like_count === 'number' ? rec.like_count : 0,
          shareCount: typeof rec.share_count === 'number' ? rec.share_count : 0,
          author: {
            id: author.id ?? 0,
            name: author.name ?? 'Anonymous',
            email: author.email ?? '',
            role: author.role ?? UserRole.USER,
            avatarUrl: author.avatarUrl ?? undefined,
            initials: author.initials ?? getInitials(author.name ?? 'Anonymous')
          },
          createdAt: rec.createdAt || new Date().toISOString(),
          reactions: Array.isArray(rec.reactions) ? rec.reactions : [],
          favorites: Array.isArray(rec.favorites) ? rec.favorites : [],
          comments: Array.isArray(rec.comments) ? rec.comments : [],
          shares: Array.isArray(rec.shares) ? rec.shares : [],
          tags: Array.isArray(rec.tags) ? rec.tags : [],
          isFavorite: Boolean(rec.isFavorite)
        };
      });
    } else {
      recommendedPosts.value = [];
    }
  } catch (err: any) {
    error.value = err.message || 'Failed to load recommended posts';
    console.error('Error fetching recommended posts:', err);
    recommendedPosts.value = [];
  } finally {
    loading.value = false;
  }
};
  // ✅ Report post
  const reportPost = async (postId: number, reportData: { reason: string }): Promise<void> => {
  try {
    loading.value = true;
    if (!reportData.reason.trim()) {
      throw new Error('Report reason cannot be empty.');
    }
    await apiClient.post(`/content/posts/${postId}/report`, { reason: reportData.reason });
    notificationStore.addNotification({
      type: 'system',
      message: 'Post reported successfully. Thank you for your feedback.',
      link: '/feed'
    });
  } catch (err: any) {
    error.value = err.message || 'Failed to report post.';
    console.error('Error reporting post:', err);
    notificationStore.addNotification({
      type: 'error',
      message: 'Failed to report post. Please try again.',
      link: '/feed'
    });
    throw err;
  } finally {
    loading.value = false;
  }
};

  // ✅ Share post
  const sharePost = async (postId: number): Promise<void> => {
    try {
      loading.value = true;
      await apiClient.post(`/content/posts/${postId}/share`);
    } catch (err: any) {
      error.value = err.message || 'Failed to share post';
      console.error("Error sharing post:", err);
      throw err;
    } finally {
      loading.value = false;
    }
  };

  return {
    posts,
    recommendedPosts,
    loading,
    error,
    getPostById,
    fetchPosts,
    createPost,
    updatePost,
    deletePost,
    toggleFavorite,
    removeFavorite,
    reactToPost,
    addComment,
    reportPost,
    sharePost,
    fetchRecommendedPosts,
    reactToComment,
    setSearchQuery,
    createSharedPost
  };
});