
import { ref, computed } from 'vue';
import { defineStore } from 'pinia';
import { apiClient } from '../api';
import { useAuthStore } from './auth';
import type { Post } from '../types/post';
import { UserRole } from '../types/UserRole';
export const useFeedStore = defineStore('feed', () => {
  const authStore = useAuthStore();
  const posts = ref<Post[]>([]);
  const loading = ref<boolean>(false);
  const error = ref<string | null>(null);

const recommendedPosts = ref<Post[]>([]);
  const getPostById = computed(() => {
    return (postId: number) => {
      return posts.value.find((post: Post) => post.id === postId);
    };
  });

  const fetchPosts = async (): Promise<void> => {
  try {
    loading.value = true;
    const response = await apiClient.get('/content/posts', {
      params: { page: 0, size: 10 }
    });

    let postData: Post[] = [];

    if (Array.isArray(response.data)) {
      // Direct array (rare)
      postData = response.data;
    } else if (response.data && Array.isArray(response.data.content)) {
      // Paginated (common)
      postData = response.data.content;
    } else {
      postData = [];
    }

    // ✅ Map to Post interface — make sure ALL fields are included
    posts.value = postData.map((post: any) => ({
      id: post.id,
      content: post.content || '',
      imageUrl: post.imageUrl || undefined,
      title: post.title || undefined,
      description: post.description || undefined,
      category: post.category || undefined,
      viewCount: typeof post.viewCount === 'number' ? post.viewCount : 0,
      likeCount: typeof post.likeCount === 'number' ? post.likeCount : 0,
      shareCount: typeof post.shareCount === 'number' ? post.shareCount : 0,
      author: {
        id: post.author?.id ?? 0,
        name: post.author?.name ?? 'Unknown',
        email: post.author?.email ?? '',
        role: post.author?.role ?? UserRole.USER,
        avatarUrl: post.author?.avatarUrl ?? undefined,
        initials: post.author?.initials ?? ''
      },
      createdAt: post.createdAt,
      reactions: Array.isArray(post.reactions) ? post.reactions : [],
      favorites: Array.isArray(post.favorites) ? post.favorites : [],
      comments: Array.isArray(post.comments) ? post.comments : [],
      shares: Array.isArray(post.shares) ? post.shares : [],
      tags: Array.isArray(post.tags) ? post.tags : []
    }));

  } catch (err: any) {
    error.value = err.message || 'Failed to load posts';
    console.error('Error fetching posts:', err);
  } finally {
    loading.value = false;
  }
};

const createPost = async (postData: {
  content: string;
  imageUrl: string | null;
  tags: string[];
}): Promise<void> => {
  try {
    loading.value = true;

    const response = await apiClient.post('/content/posts', postData);
    const backendPost = response.data;

    // Normalize imageUrl: convert null → undefined
    const normalizedImageUrl = backendPost.imageUrl ?? postData.imageUrl ?? undefined;

    const newPost: Post = {
      id: backendPost.id,
      content: backendPost.content || postData.content,
      imageUrl: normalizedImageUrl,
      title: backendPost.title || undefined,
      description: backendPost.description || undefined,
      category: backendPost.category || undefined,
      // ✅ Add missing required fields
      viewCount: backendPost.viewCount ?? 0,
      likeCount: backendPost.likeCount ?? 0,
      shareCount: backendPost.shareCount ?? 0,
      author: {
        id: backendPost.author?.id ?? authStore.user?.id ?? 0,
        name: backendPost.author?.name ?? authStore.user?.name ?? 'Anonymous',
        email: backendPost.author?.email ?? authStore.user?.email ?? '',
        role: backendPost.author?.role ?? UserRole.USER,
        avatarUrl: backendPost.author?.avatarUrl ?? authStore.user?.avatarUrl ?? undefined,
        initials: backendPost.author?.initials ?? authStore.user?.initials ?? undefined,
      },
      createdAt: backendPost.createdAt || new Date().toISOString(),
      reactions: backendPost.reactions || [],
      favorites: backendPost.favorites || [],
      comments: backendPost.comments || [],
      shares: backendPost.shares || [],
      tags: backendPost.tags || postData.tags || [],
      isFavorite: false
    };

    if (!newPost.id) {
      throw new Error('Post has no ID');
    }

    posts.value.unshift(newPost);

  } catch (err: any) {
    error.value = err.message || 'Failed to create post.';
    console.error('Error creating post:', err);
    throw err;
  } finally {
    loading.value = false;
  }
};
  const updatePost = async (postId: number, content: string, imageUrl?: string): Promise<void> => {
    try {
      loading.value = true;
      const data = { content, ...(imageUrl && { imageUrl }) };
      const response = await apiClient.put(`/content/posts/${postId}`, data);
      const updatedPost = {
        ...response.data,
        id: response.data.id, // Keep as number
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

  const toggleFavorite = async (postId: number): Promise<void> => {
    try {
      await apiClient.post(`/content/posts/${postId}/favorite`, {});
      const post = posts.value.find(p => p.id === postId);
      if (post) {
        post.isFavorite = !post.isFavorite;
      }
    } catch (err: any) {
      error.value = err.message || 'Failed to toggle favorite.';
      console.error('Error toggling favorite:', err);
      throw err;
    }
  };

  const reactToPost = async (postId: number, reactionType: string): Promise<void> => {
    try {
      const response = await apiClient.post(`/content/posts/${postId}/react`, { type: reactionType });
      const newReaction = response.data;
      const post = posts.value.find(p => p.id === postId);
      if (post && newReaction) {
        post.reactions.push(newReaction);
      }
    } catch (err: any) {
      error.value = err.message || 'Failed to react to post.';
      console.error('Error reacting to post:', err);
      throw err;
    }
  };

  const addComment = async (postId: number, text: string): Promise<void> => {
    try {
      loading.value = true;
      const response = await apiClient.post(`/content/posts/${postId}/comment`, { text });
      const newComment = response.data;
      const post = posts.value.find(p => p.id === postId);
      if (post && newComment) {
        post.comments.push(newComment);
      }
    } catch (err: any) {
      error.value = err.message || 'Failed to add comment.';
      console.error('Error adding comment:', err);
      throw err;
    } finally {
      loading.value = false;
    }
  };


const fetchRecommendedPosts = async (): Promise<void> => {
  try {
    loading.value = true;
    const response = await apiClient.get('/recommend/feed');

    if (Array.isArray(response.data?.recommendations)) {
      recommendedPosts.value = response.data.recommendations.map((rec: any) => ({
        id: rec.content_id || rec.id, // ✅ Map content_id → id
        title: rec.title || '',
        description: rec.description || '',
        content: rec.content || '',
        imageUrl: rec.image_url || undefined,
        category: rec.category || undefined,
        viewCount: typeof rec.view_count === 'number' ? rec.view_count : 0,
        likeCount: typeof rec.like_count === 'number' ? rec.like_count : 0,
        shareCount: typeof rec.share_count === 'number' ? rec.share_count : 0,
        author: {
          id: rec.author?.id ?? 0,
          name: rec.author?.name ?? 'Unknown',
          email: rec.author?.email ?? '',
          role: rec.author?.role ?? UserRole.USER,
          avatarUrl: rec.author?.avatarUrl ?? undefined,
          initials: rec.author?.name?.charAt(0).toUpperCase() ?? 'A'
        },
        createdAt: rec.createdAt || new Date().toISOString(),
        reactions: Array.isArray(rec.reactions) ? rec.reactions : [],
        favorites: Array.isArray(rec.favorites) ? rec.favorites : [],
        comments: Array.isArray(rec.comments) ? rec.comments : [],
        shares: Array.isArray(rec.shares) ? rec.shares : [],
        tags: Array.isArray(rec.tags) ? rec.tags : [],
        isFavorite: Boolean(rec.isFavorite)
      }));
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
const reportPost = async (postId: number, reportData: { reason: string }): Promise<void> => {
  try {
    loading.value = true
    await apiClient.post(`/content/posts/${postId}/report`, reportData)
    // Optional: show success in UI
  } catch (err: any) {
    error.value = err.message || 'Failed to report post'
    console.error("Error reporting post:", err)
    throw err
  } finally {
    loading.value = false
  }
};

const sharePost = async (postId: number): Promise<void> => {
  try {
    loading.value = true
    await apiClient.post(`/content/posts/${postId}/share`)
    // Optional: update share count
  } catch (err: any) {
    error.value = err.message || 'Failed to share post'
    console.error("Error sharing post:", err)
    throw err
  } finally {
    loading.value = false
  }
};

return {
  posts,
  recommendedPosts, // ✅ Must be here
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
  reportPost,
  sharePost,
  fetchRecommendedPosts
};
});
