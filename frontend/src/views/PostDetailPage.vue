<template>
  <div class="max-w-4xl mx-auto px-4 py-6">
    <!-- Back Button -->
    <div class="mb-6">
      <button
        @click="$router.back()"
        class="flex items-center text-emerald-600 dark:text-emerald-400 hover:underline"
      >
        <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-1" viewBox="0 0 20 20" fill="currentColor">
          <path fill-rule="evenodd" d="M9.707 14.707a1 1 0 01-1.414 0l-5-5a1 1 0 010-1.414l5-5a1 1 0 011.414 1.414L6.414 9H15a1 1 0 110 2H6.414l3.293 3.293a1 1 0 010 1.414z" clip-rule="evenodd" />
        </svg>
        Back
      </button>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="text-center py-10">
      <div class="animate-spin h-8 w-8 mx-auto rounded-full border-t-2 border-b-2 border-emerald-500"></div>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="text-center py-4 text-red-500">
      {{ error }}
    </div>

    <!-- Post -->
    <div v-else-if="post" :class="[
      'backdrop-blur-sm rounded-xl border shadow-xl overflow-hidden transition-colors',
      isDark ? 'bg-slate-800/50 border-slate-700' : 'bg-slate-100/90 border-slate-300'
    ]">
      <!-- Post Header -->
      <div class="p-6">
        <div class="flex items-center space-x-3 mb-4">
          <img
            v-if="post.author.avatarUrl"
            :src="getAuthorAvatar(post.author)"
            :alt="post.author.name"
            class="w-12 h-12 rounded-full object-cover"
          >
          <div
            v-else
            :class="[
              'w-12 h-12 rounded-full flex items-center justify-center font-bold',
              isDark ? 'bg-emerald-500' : 'bg-emerald-600'
            ]"
          >
            {{ post.author.initials }}
          </div>
          <div>
            <p :class="[
              'font-medium',
              isDark ? 'text-white' : 'text-slate-900'
            ]">
              {{ post.author.name }}
            </p>
            <p :class="[
              'text-sm',
              isDark ? 'text-slate-400' : 'text-slate-500'
            ]">
              {{ post.author.role }} • {{ formatDate(post.createdAt) }}
            </p>
          </div>
        </div>

        <!-- Post Title & Content -->
        <h1 :class="[
          'text-3xl font-bold mb-4',
          isDark ? 'text-white' : 'text-slate-900'
        ]">
          {{ post.title }}
        </h1>
        <p :class="[
          'text-lg mb-6',
          isDark ? 'text-slate-300' : 'text-slate-700'
        ]">
          {{ post.description }}
        </p>
        <div :class="[
          'prose prose-lg max-w-none',
          isDark ? 'prose-invert' : ''
        ]">
          <p>{{ post.content }}</p>
        </div>

        <!-- Post Image -->
        <img
          v-if="post.imageUrl"
          :src="getPostImageUrl(post.imageUrl)"
          alt="Post"
          class="w-full h-96 object-cover mt-4 rounded-lg"
        />

        <!-- Post Stats -->
        <div class="flex items-center justify-between mt-6 pt-6 border-t" :class="isDark ? 'border-slate-700' : 'border-slate-200'">
          <div class="flex items-center space-x-4">
            <div class="flex -space-x-1">
              <div
                v-for="reaction in getTopReactions(post)"
                :key="reaction.emoji"
                class="w-7 h-7 rounded-full bg-slate-700/50 flex items-center justify-center text-lg ring-2 ring-slate-800 transform hover:scale-110 transition-transform cursor-pointer"
                :title="`${reaction.count} ${reaction.emoji}`"
              >
                {{ reaction.emoji }}
              </div>
            </div>
            <span :class="isDark ? 'text-slate-400' : 'text-slate-500'">
              {{ getTotalReactions(post) }} reactions
            </span>
          </div>
          <div :class="isDark ? 'text-slate-400' : 'text-slate-500'">
            {{ post.comments?.length || 0 }} comments
          </div>
        </div>
      </div>

      <!-- Post Actions -->
      <div class="px-6 py-4 border-t" :class="isDark ? 'border-slate-700' : 'border-slate-200'">
        <div class="flex items-center space-x-6">
          <!-- Like Button -->
          <div class="relative group/reactions">
            <button
              @click="showReactionPicker(post)"
              class="flex items-center space-x-2 px-3 py-2 rounded-xl transition-all duration-200"
              :class="[
                isDark ? 'text-slate-300 hover:bg-slate-700/50' : 'text-slate-600 hover:bg-slate-200/50'
              ]"
            >
              <ThumbsUp class="w-5 h-5" />
              <span>Like</span>
            </button>

            <!-- Reaction Picker -->
            <div
              v-if="showReactionPickerFor === post.id"
              class="absolute bottom-full left-0 mb-2 bg-slate-800 rounded-full shadow-lg border border-slate-700 p-2 z-50"
              @mouseenter="cancelReactionHideTimer"
              @mouseleave="hideReactionPicker"
            >
              <div class="flex items-center space-x-1">
                <button
                  v-for="emoji in reactionEmojis"
                  :key="emoji"
                  @click="addReaction(post, emoji)"
                  class="w-8 h-8 flex items-center justify-center rounded-full transition-all duration-200 text-xl transform hover:scale-125"
                  :class="isDark ? 'hover:bg-slate-700/50' : 'hover:bg-slate-200/50'"
                >
                  {{ emoji }}
                </button>
              </div>
            </div>
          </div>

          <!-- Favorite Button -->
          <button
            @click="toggleFavorite(post)"
            :class="[
              'flex items-center space-x-2 px-4 py-2 rounded-xl transition-all duration-200',
              post.isFavorite
                ? 'text-amber-400 bg-amber-500/10'
                : isDark ? 'text-slate-300 hover:bg-slate-700/50' : 'text-slate-600 hover:bg-slate-200/50'
            ]"
          >
            <Star class="w-5 h-5" :class="{ 'fill-current': post.isFavorite }" />
            <span>{{ post.isFavorite ? 'Favorited' : 'Favorite' }}</span>
          </button>

          <!-- Share Button -->
          <button
            @click="sharePost(post)"
            :class="[
              'flex items-center space-x-2 px-4 py-2 rounded-xl transition-all duration-200',
              isDark ? 'text-slate-300 hover:bg-slate-700/50' : 'text-slate-600 hover:bg-slate-200/50'
            ]"
          >
            <Share2 class="w-5 h-5" />
            <span>Share</span>
          </button>
        </div>
      </div>

      <!-- Comments Section -->
      <div class="p-6 border-t" :class="isDark ? 'border-slate-700' : 'border-slate-200'">
        <!-- Add Comment -->
        <div class="flex space-x-3 mb-6">
          <img
            v-if="authStore.user?.avatarUrl"
            :src="authStore.user.avatarUrl"
            alt="Your Avatar"
            class="w-10 h-10 rounded-full object-cover"
          >
          <div
            v-else
            :class="[
              'w-10 h-10 rounded-full flex items-center justify-center font-medium',
              isDark ? 'bg-slate-700' : 'bg-slate-600'
            ]"
          >
            {{ authStore.user?.initials || 'U' }}
          </div>
          <div class="flex-1">
            <textarea
              v-model="newCommentText"
              placeholder="Write a comment..."
              rows="2"
              :class="[
                'w-full px-4 py-2 rounded-lg resize-none focus:outline-none focus:ring-2',
                isDark
                  ? 'bg-slate-700 border-slate-600 focus:ring-emerald-500 text-white'
                  : 'bg-slate-100 border-slate-300 focus:ring-emerald-600 text-slate-900'
              ]"
            ></textarea>
            <div class="flex justify-end mt-2">
              <button
                @click="handleAddComment"
                :disabled="!newCommentText.trim()"
                :class="[
                  'px-4 py-1 rounded-lg text-sm font-medium transition-colors',
                  newCommentText.trim()
                    ? 'bg-emerald-600 hover:bg-emerald-700 text-white'
                    : 'bg-slate-300 text-slate-500 cursor-not-allowed'
                ]"
              >
                Comment
              </button>
            </div>
          </div>
        </div>

        <!-- Comments List -->
        <div class="space-y-4">
          <div
            v-for="comment in post.comments"
            :key="comment.id"
            class="border-l-2 pl-4 ml-2"
            :class="isDark ? 'border-slate-700' : 'border-slate-200'"
          >
            <div class="flex items-start space-x-3">
              <img
                v-if="comment.author.avatarUrl"
                :src="getAuthorAvatar(comment.author)"
                :alt="comment.author.name"
                class="w-8 h-8 rounded-full object-cover"
              >
              <div
                v-else
                :class="[
                  'w-8 h-8 rounded-full flex items-center justify-center text-xs font-bold',
                  isDark ? 'bg-slate-700' : 'bg-slate-600'
                ]"
              >
                {{ comment.author.initials }}
              </div>
              <div class="flex-1">
                <div class="flex items-center space-x-2">
                  <span :class="isDark ? 'text-white' : 'text-slate-900'">{{ comment.author.name }}</span>
                  <span :class="isDark ? 'text-slate-400' : 'text-slate-500'">{{ formatDate(comment.createdAt) }}</span>
                </div>
                <p :class="isDark ? 'text-slate-300' : 'text-slate-700'">{{ comment.text }}</p>

                <!-- Comment Reactions -->
                <div class="flex items-center space-x-2 mt-2">
                  <button
                    v-for="emoji in ['👍', '❤️', '😊']"
                    :key="emoji"
                    @click="addReactionToComment(comment, emoji)"
                    class="text-sm hover:bg-slate-200 dark:hover:bg-slate-700 px-2 py-1 rounded"
                  >
                    {{ emoji }}
                  </button>
                </div>

                <!-- Reply Button -->
                <button
                  @click="startReply(comment)"
                  class="text-sm text-emerald-600 dark:text-emerald-400 hover:underline mt-1"
                >
                  Reply
                </button>

                <!-- Reply Input -->
                <div v-if="replyingTo === comment.id" class="mt-3 ml-4">
                  <textarea
                    v-model="replyText"
                    placeholder="Write a reply..."
                    rows="2"
                    :class="[
                      'w-full px-3 py-2 rounded border text-sm',
                      isDark
                        ? 'bg-slate-700 border-slate-600 text-white'
                        : 'bg-white border-slate-300 text-slate-900'
                    ]"
                  ></textarea>
                  <div class="flex justify-end mt-2 space-x-2">
                    <button
                      @click="cancelReply"
                      class="px-3 py-1 text-sm rounded border hover:bg-slate-200 dark:hover:bg-slate-700"
                    >
                      Cancel
                    </button>
                    <button
                      @click="addReply(comment, replyText)"
                      :disabled="!replyText.trim()"
                      class="px-3 py-1 text-sm bg-emerald-600 text-white rounded hover:bg-emerald-700 disabled:opacity-50"
                    >
                      Reply
                    </button>
                  </div>
                </div>

                <!-- Replies -->
                <div v-if="comment.replies && comment.replies.length > 0" class="ml-6 mt-3 space-y-3">
                  <div
                    v-for="reply in comment.replies"
                    :key="reply.id"
                    :class="[
                      'p-3 rounded-lg',
                      isDark ? 'bg-slate-700/50' : 'bg-slate-100'
                    ]"
                  >
                    <div class="flex items-center space-x-2 mb-1">
                      <span class="font-medium">{{ reply.author.name }}</span>
                      <span :class="isDark ? 'text-slate-400' : 'text-slate-500'">{{ formatDate(reply.createdAt) }}</span>
                    </div>
                    <p :class="isDark ? 'text-slate-300' : 'text-slate-700'">{{ reply.text }}</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Post Not Found -->
    <div v-else class="text-center py-16">
      <h3 class="text-xl font-medium text-white">Post not found</h3>
      <p class="text-slate-400">The post you're looking for doesn't exist or has been removed.</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { useFeedStore } from '../stores/feed';
import { useAuthStore } from '../stores/auth';
import { useThemeStore } from '../stores/theme';
import { useNotificationStore } from '../stores/notification';
import { ThumbsUp, Star, Share2 } from 'lucide-vue-next';
import type { Post } from '../types/post';
import { UserRole } from '../types/UserRole';
import { apiClient } from '../api';

const route = useRoute();
const feedStore = useFeedStore();
const authStore = useAuthStore();
const themeStore = useThemeStore();
const notificationStore = useNotificationStore();

const isDark = computed(() => themeStore.isDark);
const loading = ref(false);
const error = ref<string | null>(null);
const newCommentText = ref('');
const replyText = ref('');
const replyingTo = ref<number | null>(null);
const showReactionPickerFor = ref<number | null>(null);

// Convert route param to number
const postId = computed(() => {
  const id = Number(route.params.id);
  if (isNaN(id)) {
    error.value = 'Invalid post ID';
    return null;
  }
  return id;
});

// Ref for current post
const post = ref<Post | null>(null);

// Helper: Get initials
const getInitials = (name: string | undefined): string => {
  if (!name) return 'U';
  return name
    .split(' ')
    .map(n => n[0].toUpperCase())
    .join('')
    .slice(0, 2);
};

// Format date
const formatDate = (dateString: string): string => {
  try {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
  } catch {
    return 'Invalid Date';
  }
};

// Get full image URL
const getPostImageUrl = (url: string | undefined): string => {
  if (!url) return '';
  const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080';
  if (url.startsWith('http')) return url;
  if (url.startsWith('/uploads/')) return `${apiUrl}${url}`;
  return `${apiUrl}/api${url}`;
};

// Get author avatar
const getAuthorAvatar = (author: any) => {
  const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080';
  return author.avatarUrl ? `${apiUrl}${author.avatarUrl}` : '';
};

// Reactions
const reactionEmojis = ['👍', '❤️', '😊', '🎉', '👏', '🔥'];
const getTopReactions = (post: Post) => {
  return (post.reactions || [])
    .sort((a: any, b: any) => (b.count ?? 0) - (a.count ?? 0))
    .slice(0, 3);
};
const getTotalReactions = (post: Post) => {
  return (post.reactions || []).reduce((sum, r) => sum + (r.count ?? 0), 0);
};

// Reaction picker
let reactionHideTimer: number | null = null;
const cancelReactionHideTimer = () => {
  if (reactionHideTimer) {
    clearTimeout(reactionHideTimer);
    reactionHideTimer = null;
  }
};
const showReactionPicker = (post: Post) => {
  showReactionPickerFor.value = post.id;
};
const hideReactionPicker = () => {
  reactionHideTimer = window.setTimeout(() => {
    showReactionPickerFor.value = null;
  }, 300);
};

// Lifecycle: Load post
onMounted(async () => {
  if (!postId.value) return;

  loading.value = true;
  error.value = null;

  try {
   let foundPost: Post | undefined = feedStore.posts.find(p => p.id === postId.value);

try {
  if (!foundPost) {
    console.log(`Fetching post ID ${postId.value} from API...`);
    const response = await apiClient.get(`/content/posts/${postId.value}`);
    console.log('Raw API response:', response.data);
    foundPost = response.data;

    if (!foundPost?.id) {
      error.value = 'Post not found.';
      return;
    }

    const index = feedStore.posts.findIndex(p => p.id === foundPost!.id);
    if (index === -1) {
      feedStore.posts.unshift(foundPost);
    } else {
      feedStore.posts[index] = foundPost;
    }
  }

  // ✅ Now safe to use
  if (!foundPost.author) {
    foundPost.author = {
      id: 0,
      name: 'Unknown',
      email: '',
      role: UserRole.USER,
      initials: 'U',
      avatarUrl: undefined
    };
  } else {
    foundPost.author.role = foundPost.author.role ?? UserRole.USER;
    foundPost.author.name = foundPost.author.name ?? 'Unknown';
    foundPost.author.initials = foundPost.author.initials ?? getInitials(foundPost.author.name);
  }

  if (!foundPost.comments) foundPost.comments = [];

  post.value = foundPost;
} catch (err: any) {
  if (err.response?.status === 404) {
    error.value = 'Post not found.';
  } else if (err.message.includes('Network Error')) {
    error.value = 'Unable to connect to server.';
  } else {
    error.value = err.message || 'Failed to load post.';
  }
  console.error('Error loading post:', err);
} finally {
  loading.value = false;
}
  } catch (err: any) {
    if (err.response?.status === 404) {
      error.value = 'Post not found.';
    } else if (err.message.includes('Network Error')) {
      error.value = 'Unable to connect to server.';
    } else {
      error.value = err.message || 'Failed to load post.';
    }
    console.error('Error loading post:', err);
  } finally {
    loading.value = false;
  }
});

// Add reaction
const addReaction = async (post: Post, emoji: string) => {
  try {
    await feedStore.reactToPost(post.id, emoji);
    notificationStore.addNotification({
      type: 'system',
      message: `You reacted with ${emoji} to a post`,
      link: `/feed/${post.id}`
    });
  } catch (err: any) {
    error.value = err.message;
  }
};

// Toggle favorite
const toggleFavorite = async (post: Post) => {
  try {
    await feedStore.toggleFavorite(post.id);
    if (post.author.id !== authStore.user?.id && post.isFavorite) {
      notificationStore.addNotification({
        type: 'favorite',
        message: `${authStore.user?.name} favorited your post "${post.title}"`,
        link: `/feed/${post.id}`,
        userId: String(post.author.id)
      });
    }
  } catch (err: any) {
    error.value = err.message;
  }
};

// Share post
const sharePost = async (post: Post) => {
  try {
    await feedStore.sharePost(post.id);
    notificationStore.addNotification({
      type: 'system',
      message: 'Post shared successfully!',
      link: '/feed'
    });
  } catch (err: any) {
    error.value = err.message;
  }
};

// Add comment
const handleAddComment = () => {
  if (post.value?.id && newCommentText.value.trim()) {
    feedStore.addComment(post.value.id, newCommentText.value)
      .then(() => {
        newCommentText.value = '';
      })
      .catch((err: any) => {
        error.value = err.message;
      });
  }
};

// Reply to comment
const startReply = (comment: any) => {
  replyingTo.value = comment.id;
  replyText.value = '';
};

const cancelReply = () => {
  replyingTo.value = null;
  replyText.value = '';
};

const addReply = async (parentComment: any, text: string) => {
  if (!text.trim()) return;
  try {
    await feedStore.addComment(parentComment.id, text);
    replyText.value = '';
    replyingTo.value = null;
  } catch (err: any) {
    error.value = err.message;
  }
};

// Add reaction to comment
const addReactionToComment = async (comment: any, emoji: string) => {
  console.log(`React to comment ${comment.id} with ${emoji}`);
};
</script>