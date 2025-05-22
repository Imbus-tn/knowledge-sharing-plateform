<template>
    <div class="max-w-7xl mx-auto px-4 sm:px-6 py-6">
      <div class="grid grid-cols-1 lg:grid-cols-4 gap-6">
        <!-- Left Sidebar - User Profile -->
        <div class="lg:col-span-1">
          <div :class="[
            'backdrop-blur-sm rounded-xl border shadow-xl overflow-hidden transition-colors duration-200',
            isDark 
              ? 'bg-slate-800/50 border-slate-700' 
              : 'bg-slate-100/90 border-slate-300'
          ]">
            <!-- Cover Image -->
            <div class="h-24 bg-gradient-to-r from-slate-900 via-indigo-900 to-slate-900 relative overflow-hidden">
              <div class="absolute inset-0 bg-gradient-to-b from-transparent via-black/30 to-black/60"></div>
              <div class="absolute inset-0 bg-[radial-gradient(circle_at_20%_30%,rgba(255,255,255,0.05),transparent)]"></div>
            </div>
            <!-- Profile Info -->
            <div class="px-4 pb-4">
              <!-- Avatar -->
              <div class="relative -mt-12 mb-4">
                <img 
                  v-if="avatarUrl" 
                  :src="avatarUrl" 
                  alt="User Avatar"
                  :class="[
                    'w-24 h-24 rounded-xl object-cover shadow-xl',
                    isDark ? 'border-4 border-slate-800' : 'border-4 border-white'
                  ]"
                >
                <div 
                  v-else 
                  :class="[
                    'w-24 h-24 rounded-xl shadow-xl flex items-center justify-center',
                    isDark ? 'bg-emerald-500 border-4 border-slate-800' : 'bg-emerald-600 border-4 border-white'
                  ]"
                >
                  <span class="text-2xl font-bold text-white">{{ userInitials }}</span>
                </div>
              </div>
              
              <!-- User Info -->
              <div class="mb-4">
                <h2 :class="[
                  'text-xl font-bold mb-1 transition-colors',
                  isDark ? 'text-white' : 'text-slate-900'
                ]">
                  {{ user?.name || 'John Doe' }}
                </h2>
              </div>

              <!-- Quick Stats -->
              <div :class="[
                'border-t pt-4 mb-4 transition-colors',
                isDark ? 'border-slate-700' : 'border-slate-200'
              ]">
                <div class="grid grid-cols-3 gap-4 text-center">
                  <div>
                    <div :class="[
                      'text-lg font-bold transition-colors',
                      isDark ? 'text-white' : 'text-slate-900'
                    ]">
                      {{ quickStats.articlesPublished }}
                    </div>
                    <div :class="[
                      'text-xs transition-colors',
                      isDark ? 'text-slate-400' : 'text-slate-500'
                    ]">
                      Posts
                    </div>
                  </div>
                  <div>
                    <div :class="[
                      'text-lg font-bold transition-colors',
                      isDark ? 'text-white' : 'text-slate-900'
                    ]">
                      {{ quickStats.totalViews }}
                    </div>
                    <div :class="[
                      'text-xs transition-colors',
                      isDark ? 'text-slate-400' : 'text-slate-500'
                    ]">
                      Views
                    </div>
                  </div>
                  <div>
                    <div :class="[
                      'text-lg font-bold transition-colors',
                      isDark ? 'text-white' : 'text-slate-900'
                    ]">
                      {{ quickStats.contributions }}
                    </div>
                    <div :class="[
                      'text-xs transition-colors',
                      isDark ? 'text-slate-400' : 'text-slate-500'
                    ]">
                      Reactions
                    </div>
                  </div>
                </div>
              </div>

              <!-- Quick Links -->
              <div class="space-y-2">
                <router-link 
                  to="/profile" 
                  :class="[
                    'flex items-center px-3 py-2 rounded-lg transition-all duration-200',
                    isDark 
                      ? 'text-slate-300 hover:bg-slate-700/50' 
                      : 'text-slate-600 hover:bg-slate-200/70 hover:text-slate-900'
                  ]"
                >
                  <User 
                    :class="[
                      'w-5 h-5 mr-3 transition-colors',
                      isDark ? 'text-slate-400' : 'text-slate-500'
                    ]" 
                  />
                  <span>View Profile</span>
                </router-link>
                
                <router-link 
                  to="/dashboard" 
                  :class="[
                    'flex items-center px-3 py-2 rounded-lg transition-all duration-200',
                    isDark 
                      ? 'text-slate-300 hover:bg-slate-700/50' 
                      : 'text-slate-600 hover:bg-slate-200/70 hover:text-slate-900'
                  ]"
                >
                  <LayoutDashboard 
                    :class="[
                      'w-5 h-5 mr-3 transition-colors',
                      isDark ? 'text-slate-400' : 'text-slate-500'
                    ]" 
                  />
                  <span>Dashboard</span>
                </router-link>
                
                <router-link 
                  to="/favorites" 
                  :class="[
                    'flex items-center px-3 py-2 rounded-lg transition-all duration-200',
                    isDark 
                      ? 'text-slate-300 hover:bg-slate-700/50' 
                      : 'text-slate-600 hover:bg-slate-200/70 hover:text-slate-900'
                  ]"
                >
                  <Bookmark 
                    :class="[
                      'w-5 h-5 mr-3 transition-colors',
                      isDark ? 'text-slate-400' : 'text-slate-500'
                    ]" 
                  />
                  <span>Favorites</span>
                </router-link>
              </div>
            </div>
          </div>
          <!-- Trending Topics -->
          <div :class="[
            'mt-6 backdrop-blur-sm rounded-xl border shadow-xl p-4 transition-colors duration-200',
            isDark 
              ? 'bg-slate-800/50 border-slate-700' 
              : 'bg-slate-100/90 border-slate-300'
          ]">
            <h3 :class="[
              'font-semibold mb-4 transition-colors duration-200',
              isDark ? 'text-white' : 'text-slate-900'
            ]">
              Trending Topics
            </h3>
            <div class="space-y-3">
              <div 
                v-for="topic in trendingTopics" 
                :key="topic.tag"
                class="flex items-center justify-between group cursor-pointer"
              >
                <div class="flex items-center space-x-2">
                  <component 
                    :is="topic.icon" 
                    :class="[
                      'w-4 h-4 transition-colors duration-200',
                      isDark 
                        ? 'text-slate-400 group-hover:text-emerald-500' 
                        : 'text-slate-500 group-hover:text-emerald-600'
                    ]" 
                  />
                  <span :class="[
                    'transition-colors duration-200',
                    isDark 
                      ? 'text-slate-300 group-hover:text-white' 
                      : 'text-slate-600 group-hover:text-slate-900'
                  ]">
                    #{{ topic.tag }}
                  </span>
                </div>
                <span :class="[
                  'text-sm transition-colors duration-200',
                  isDark ? 'text-slate-400' : 'text-slate-500'
                ]">
                  {{ topic.posts }}
                </span>
              </div>
            </div>
          </div>
        </div>
        <!-- Main Content -->
        <div class="lg:col-span-3">
          <!-- Create Post Card -->
          <div :class="[
            'backdrop-blur-sm rounded-xl border shadow-xl mb-6 transition-colors duration-200',
            isDark 
              ? 'bg-slate-800/50 border-slate-700' 
              : 'bg-slate-100/90 border-slate-300'
          ]">
            <div class="flex items-start space-x-4 p-4">
              <!-- Avatar -->
              <div class="flex-shrink-0">
                <img 
                  v-if="avatarUrl" 
                  :src="avatarUrl" 
                  alt="User Avatar"
                  :class="[
                    'w-12 h-12 rounded-full object-cover border-2 shadow-xl transition-colors duration-200',
                    isDark ? 'border-slate-800' : 'border-white'
                  ]"
                >
                <div 
                  v-else 
                  :class="[
                    'w-12 h-12 rounded-full bg-emerald-500 border-2 flex items-center justify-center transition-colors duration-200',
                    isDark ? 'border-slate-800' : 'border-white'
                  ]"
                >
                  <span class="text-white font-medium">{{ userInitials }}</span>
                </div>
              </div>
              
              <!-- Post Input -->
              <div class="flex-1 min-w-0">
                <div 
                  @click="showCreateModal = true"
                  tabindex="0"
                  :class="[
                    'w-full px-4 py-3 rounded-3xl cursor-pointer transition-all duration-200 border-2',
                    isDark 
                      ? 'bg-slate-700/50 border-slate-600 text-slate-400 hover:bg-slate-700/70 focus:ring-2 focus:ring-emerald-500 focus:outline-none' 
                      : 'bg-slate-200/70 border-slate-300 text-slate-500 hover:bg-slate-200/70 focus:ring-2 focus:ring-emerald-600 focus:outline-none'
                  ]"
                >
                  Start a post...
                </div>

                <!-- Action Buttons -->
                <div class="flex items-center space-x-4 mt-4">
                  <button 
                    :class="[
                      'flex items-center space-x-2 transition-colors duration-200',
                      isDark 
                        ? 'text-slate-300 hover:text-white' 
                        : 'text-slate-600 hover:text-slate-900'
                    ]"
                  >
                    <Image :class="isDark ? 'text-slate-400' : 'text-slate-500'" class="w-5 h-5" />
                    <span>Photo</span>
                  </button>
                  <button 
                    :class="[
                      'flex items-center space-x-2 transition-colors duration-200',
                      isDark 
                        ? 'text-slate-300 hover:text-white' 
                        : 'text-slate-600 hover:text-slate-900'
                    ]"
                  >
                    <Video :class="isDark ? 'text-slate-400' : 'text-slate-500'" class="w-5 h-5" />
                    <span>Video</span>
                  </button>
                  <button 
                    :class="[
                      'flex items-center space-x-2 transition-colors duration-200',
                      isDark 
                        ? 'text-slate-300 hover:text-white' 
                        : 'text-slate-600 hover:text-slate-900'
                    ]"
                  >
                    <FileText :class="isDark ? 'text-slate-400' : 'text-slate-500'" class="w-5 h-5" />
                    <span>Article</span>
                  </button>
                </div>
              </div>
            </div>
          </div>
          <!-- Posts Feed -->
          <div v-if="!feedStore.loading && posts.length === 0" 
            :class="[
              'text-center py-10 transition-colors duration-200',
              isDark ? 'text-slate-400' : 'text-slate-500'
            ]">
            No posts yet. Be the first to share something!
          </div>

          <div v-if="feedStore.loading" class="text-center py-10">
            <div class="animate-spin rounded-full h-8 w-8 mx-auto"
              :class="isDark ? 'border-t-2 border-b-2 border-emerald-500' : 'border-t-2 border-b-2 border-emerald-600'">
            </div>
          </div>

          <div v-if="feedStore.error" 
            class="text-center py-4"
            :class="isDark ? 'text-red-400' : 'text-red-600'">
            {{ feedStore.error }}
          </div>

          <div class="space-y-6">
            <div 
              v-for="post in posts" 
              :key="post.id"
              :class="[
                'backdrop-blur-sm rounded-xl border shadow-xl overflow-hidden transition-colors duration-200',
                isDark 
                  ? 'bg-slate-800/50 border-slate-700' 
                  : 'bg-slate-100/90 border-slate-300'
              ]"
            >
              <!-- Post Header -->
              <div class="p-4">
                <div class="flex items-center space-x-3">
                  <!-- Author Info -->
                  <div class="flex-shrink-0">
                    <img 
                      v-if="post.author.avatarUrl" 
                      :src="getAuthorAvatar(post.author)" 
                      alt="Author Avatar"
                      class="w-12 h-12 rounded-full object-cover"
                      :class="isDark ? 'border-2 border-slate-800' : 'border-2 border-white'"
                    >
                    <div 
                      v-else 
                      :class="[
                        'w-12 h-12 rounded-full flex items-center justify-center transition-colors',
                        isDark ? 'border-2 border-slate-800' : 'border-2 border-white'
                      ]"
                    >
                      <span class="text-white font-medium">{{ post.author.initials }}</span>
                    </div>
                  </div>
                  <div class="flex-1 min-w-0">
                    <p :class="[
                      'font-medium transition-colors',
                      isDark ? 'text-white' : 'text-slate-900'
                    ]">
                      {{ post.author.name }}
                    </p>
                    <p :class="[
                      'text-xs transition-colors',
                      isDark ? 'text-slate-500' : 'text-slate-400'
                    ]">
                      {{ formatTime(post.createdAt) }}
                    </p>
                  </div>
                  <!-- More Menu -->
                  <div class="relative">
                    <button 
                      @click="togglePostMenu(post.id)"
                      :class="[
                        'p-2 rounded-lg transition-all duration-200',
                        isDark 
                          ? 'text-slate-400 hover:text-white hover:bg-slate-700/50' 
                          : 'text-slate-500 hover:text-slate-900 hover:bg-slate-100/50'
                      ]"
                      :data-post-menu-button="post.id"
                    >
                      <MoreVertical class="w-5 h-5" />
                    </button>
                    <!-- Dropdown Menu -->
                    <div v-if="activePostMenu === post.id"
                      :class="[
                        'absolute right-0 mt-2 w-48 rounded-lg shadow-lg ring-1 ring-black ring-opacity-5 z-50',
                        isDark ? 'bg-slate-800' : 'bg-white'
                      ]"
                      :data-post-menu="post.id"
                    >
                      <div class="py-1">
                        <button 
                          @click="editPost(post)"
                          :class="[
                            'flex items-center w-full px-4 py-2 text-sm transition-colors',
                            isDark 
                              ? 'text-slate-300 hover:bg-slate-700 hover:text-white' 
                              : 'text-slate-600 hover:bg-slate-100 hover:text-slate-900'
                          ]"
                        >
                          <Edit 
                            :class="isDark ? 'text-slate-400' : 'text-slate-500'" 
                            class="w-4 h-4 mr-2" 
                          />
                          Edit Post
                        </button>
                        <button 
                          @click="savePost(post)"
                          :class="[
                            'flex items-center w-full px-4 py-2 text-sm transition-colors',
                            isDark 
                              ? 'text-slate-300 hover:bg-slate-700 hover:text-white' 
                              : 'text-slate-600 hover:bg-slate-100 hover:text-slate-900'
                          ]"
                        >
                          <Bookmark 
                            :class="isDark ? 'text-slate-400' : 'text-slate-500'" 
                            class="w-4 h-4 mr-2" 
                          />
                          Save Post
                        </button>
                        <button 
                          @click="sharePost(post)"
                          :class="[
                            'flex items-center w-full px-4 py-2 text-sm transition-colors',
                            isDark 
                              ? 'text-slate-300 hover:bg-slate-700 hover:text-white' 
                              : 'text-slate-600 hover:bg-slate-100 hover:text-slate-900'
                          ]"
                        >
                          <Share2 
                            :class="isDark ? 'text-slate-400' : 'text-slate-500'" 
                            class="w-4 h-4 mr-2" 
                          />
                          Share Post
                        </button>
                        <button 
                          @click="reportPost(post)"
                          :class="[
                            'flex items-center w-full px-4 py-2 text-sm transition-colors',
                            isDark 
                              ? 'text-slate-300 hover:bg-slate-700 hover:text-white' 
                              : 'text-slate-600 hover:bg-slate-100 hover:text-slate-900'
                          ]"
                        >
                          <Flag 
                            :class="isDark ? 'text-slate-400' : 'text-slate-500'" 
                            class="w-4 h-4 mr-2" 
                          />
                          Report Post
                        </button>
                        <button 
                          @click="deletePost(post)"
                          :class="[
                            'flex items-center w-full px-4 py-2 text-sm transition-colors',
                            isDark 
                              ? 'text-red-400 hover:bg-slate-700' 
                              : 'text-red-600 hover:bg-slate-100'
                          ]"
                        >
                          <Trash2 
                            :class="isDark ? 'text-red-400' : 'text-red-600'" 
                            class="w-4 h-4 mr-2" 
                          />
                          Delete Post
                        </button>
                      </div>
                    </div>
                  </div>
                </div>
                <p 
                  class="mt-3 transition-colors"
                  :class="isDark ? 'text-slate-300' : 'text-slate-700'"
                >
                  {{ post.content }}
                </p>
              </div>
              <!-- Post Image -->
              <img 
                v-if="post.imageUrl"
                :src="post.imageUrl" 
                :alt="post.content"
                class="w-full h-96 object-cover"
              />
              <!-- Post Stats -->
              <!-- Post Stats -->
              <div class="px-4 py-2">
                <div class="flex items-center justify-between">
                  <div class="flex items-center space-x-1">
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
                    <span class="text-slate-400 text-sm ml-2">{{ getTotalReactions(post) }}</span>
                  </div>
                  <div class="flex items-center space-x-4 text-sm text-slate-400">
                    <div class="flex items-center">
                      <MessageSquare class="w-4 h-4 mr-1" />
                      <span>{{ post.comments }}</span>
                    </div>
                    <div class="flex items-center">
                      <Share2 class="w-4 h-4 mr-1" />
                      <span>{{ post.shares }}</span>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Post Actions -->
              <div class="px-4 py-2 relative">
                <div class="flex items-center justify-between">
                  <!-- Like Button with Reaction Options -->
                  <div class="relative">
                    <div class="relative group/reactions">
                      <button 
                        @click="showReactionPicker(post)"
                        class="flex items-center space-x-2 px-3 py-2 rounded-xl transition-all duration-200"
                        :class="[
                          isDark 
                            ? 'text-slate-300 hover:bg-slate-700/50' 
                            : 'text-slate-600 hover:bg-slate-200/50'
                        ]"
                        @mouseenter="showReactionPickerFor = post.id"
                        @mouseleave="handleReactionMouseLeave"
                      >
                        <ThumbsUp class="w-5 h-5" />
                        <span>Like</span>
                      </button>
                      <!-- Reaction Picker Popup -->
                      <div 
                        v-if="showReactionPickerFor === post.id"
                        class="absolute bottom-full left-0 mb-2 bg-slate-800 rounded-full shadow-lg border border-slate-700 p-2 z-50 transition-all duration-200 transform origin-bottom-left"
                        @mouseenter="cancelReactionHideTimer"
                        @mouseleave="hideReactionPicker"
                      >
                        <div class="flex items-center space-x-1">
                          <button 
                            v-for="emoji in reactionEmojis" 
                            :key="emoji"
                            @click="addReaction(post, emoji)"
                            class="w-8 h-8 flex items-center justify-center rounded-full transition-all duration-200 text-xl transform hover:scale-125"
                            :class="[
                              isDark 
                                ? 'hover:bg-slate-700/50' 
                                : 'hover:bg-slate-200/50',
                              hasReacted(post, emoji) ? 'bg-slate-700 scale-110' : ''
                            ]"
                          >
                            {{ emoji }}
                          </button>
                        </div>
                      </div>
                    </div>
                  </div>
                  <button 
                    class="flex items-center space-x-2 px-4 py-2 rounded-lg transition-colors"
                    :class="[
                      isDark 
                        ? 'text-slate-300 hover:bg-slate-700/50' 
                        : 'text-slate-600 hover:bg-slate-200/50'
                    ]"
                  >
                    <MessageSquare class="w-5 h-5" />
                    <span>Comment</span>
                  </button>
                  <button 
                    class="flex items-center space-x-2 px-4 py-2 rounded-lg transition-colors"
                    :class="[
                      isDark 
                        ? 'text-slate-300 hover:bg-slate-700/50' 
                        : 'text-slate-600 hover:bg-slate-200/50'
                    ]"
                  >
                    <Share2 class="w-5 h-5" />
                    <span>Share</span>
                  </button>
                  <!-- Favorite Button -->
                  <button 
                    @click="toggleFavorite(post)"
                    class="flex items-center space-x-2 px-4 py-2 rounded-xl transition-all duration-200"
                    :class="[
                      post.isFavorite 
                        ? 'text-amber-400 bg-amber-500/10' 
                        : isDark ? 'text-slate-300 hover:bg-slate-700/50' : 'text-slate-600 hover:bg-slate-200/50'
                    ]"
                  >
                    <Star class="w-5 h-5" :class="{ 'fill-current': post.isFavorite }" />
                    <span>{{ post.isFavorite ? 'Favorited' : 'Favorite' }}</span>
                  </button>
                </div>
              </div>
          </div>
        </div>
      </div>
    </div>  
  </div>
      <!-- Create Post Modal -->
      <CreatePostModal 
        v-if="showCreateModal"
        @close="showCreateModal = false"
        @submit="handleCreatePost"
      />
  </template>
  
  <script setup lang="ts">
  import { ref, computed, onMounted, onUnmounted, onActivated } from 'vue';
  import { useAuthStore } from '../stores/auth';
  import { useThemeStore } from '../stores/theme';
  import { 
    Image, Video, FileText, MoreVertical, ThumbsUp,
    MessageSquare, Share2, User, LayoutDashboard, Bookmark,
    Code, Server, Cloud, Database, Terminal, Lock, Star,
    Edit, Flag, Trash2
  } from 'lucide-vue-next';
  import CreatePostModal from '../components/CreatePostModal.vue';
  import { useNotificationStore } from '../stores/notification';
  import { useFeedStore } from '../stores/feed';
  
  const authStore = useAuthStore();
  const feedStore = useFeedStore();
  const themeStore = useThemeStore();
  const user = computed(() => authStore.user);
  const notificationStore = useNotificationStore();
  const showCreateModal = ref(false);
  const activePostMenu = ref<string | null>(null);
  const posts = computed(() => feedStore.posts);
  const isDark = computed(() => themeStore.isDark);
  
  // Avatar handling
  const avatarUrl = computed(() => {
    const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080'; 
    return user.value?.avatarUrl
      ? `${apiUrl}${user.value.avatarUrl}`
      : '';
  });

  // User initials
  const userInitials = computed(() => {
    return user.value?.name
      ? user.value.name.split(' ').map(n => n[0]).join('').toUpperCase()
      : '';
  });

  const getAuthorAvatar = (author: any) => {
    const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080';
    const rawUrl = author?.avatarUrl;

    if (!rawUrl) return '';

    // Avoid duplicating the base URL if already absolute
    if (rawUrl.startsWith('http')) {
      return rawUrl;
    }

    return `${apiUrl}${rawUrl}`;
  };
  
  // Quick stats
  const quickStats = computed(() => ({
    articlesPublished: user.value?.articlesCount || 24,
    totalViews: user.value?.totalViews || '12.4K',
    contributions: user.value?.contributions || 156
  }));
  
  // Trending topics data
  const trendingTopics = [
    { tag: 'vue3', icon: Code, posts: '2.5k posts' },
    { tag: 'devops', icon: Server, posts: '1.8k posts' },
    { tag: 'cloud', icon: Cloud, posts: '1.2k posts' },
    { tag: 'database', icon: Database, posts: '956 posts' },
    { tag: 'terminal', icon: Terminal, posts: '845 posts' },
    { tag: 'security', icon: Lock, posts: '734 posts' }
  ];

  const showReactionPickerFor = ref<string | null>(null);
  const reactionPickerPosition = ref({ top: '0px', left: '0px' });
  const reactionEmojis = ['👍', '❤️', '😊', '🎉', '🤔', '👏', '🔥', '💯', '✨', '🙌'];
  let reactionHideTimer: number | null = null;
  
  

  const getTopReactions = (post: Post) => {
    return post.reactions
      .sort((a: any, b: any) => b.count - a.count)
      .slice(0, 3);
  };

  const getTotalReactions = (post: Post) => {
    return post.reactions.reduce((total: number, reaction: any) => total + reaction.count, 0);
  };


  const handleReactionMouseLeave = () => {
    reactionHideTimer = window.setTimeout(() => {
      showReactionPickerFor.value = null;
    }, 300);
  };

  const cancelReactionHideTimer = () => {
    if (reactionHideTimer) {
      clearTimeout(reactionHideTimer);
      reactionHideTimer = null;
    }
  };

  const hideReactionPicker = () => {
    reactionHideTimer = window.setTimeout(() => {
      showReactionPickerFor.value = null;
    }, 300);
  };

  const showReactionPicker = (post: Post, event?: MouseEvent) => {
    if (event) {
      const rect = (event.target as HTMLElement).getBoundingClientRect();
      reactionPickerPosition.value = {
        top: `${rect.bottom + 5}px`,
        left: `${rect.left}px`
      };
    }
    showReactionPickerFor.value = post.id;
  };

  const hasReacted = (post: Post, emoji: string) => {
    const reaction = post.reactions.find((r: any) => r.emoji === emoji);
    return reaction?.users.includes('user1');
  };

  const addReaction = (post: Post, emoji: string) => {
    let reaction = post.reactions.find((r: any) => r.emoji === emoji);
    if (reaction) {
      if (reaction.users.includes('user1')) {
        reaction.count--;
        reaction.users = reaction.users.filter((u: string) => u !== 'user1');
        if (reaction.count === 0) {
          post.reactions = post.reactions.filter((r: any) => r.emoji !== emoji);
        }
      } else {
        reaction.count++;
        reaction.users.push('user1');
      }
    } else {
      post.reactions.push({
        emoji,
        count: 1,
        users: ['user1']
      });
    }
    showReactionPickerFor.value = null;
  };
  
  // Time formatting
  const formatTime = (timestamp: string) => {
    const date = new Date(timestamp);
    const now = new Date();
    const diffMs = now.getTime() - date.getTime();
    const diffMins = Math.round(diffMs / 60000);
    if (diffMins < 1) {
      return 'Just now';
    } else if (diffMins < 60) {
      return `${diffMins}m`;
    } else if (diffMins < 1440) {
      return `${Math.floor(diffMins / 60)}h`;
    } else {
      return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
    }
  };

  const togglePostMenu = (postId: string): void => {
    activePostMenu.value = activePostMenu.value === postId ? null : postId;
  };
  
  
  // Post management actions
  const editPost = (post: Post) => {
    activePostMenu.value = null;
    notificationStore.addNotification({
      type: 'system',
      message: 'Post editing coming soon!',
      link: '/feed'
    });
  };

  const savePost = (post: Post) => {
    activePostMenu.value = null;
    notificationStore.addNotification({
      type: 'system',
      message: 'Post saved to your bookmarks',
      link: '/feed'
    });
  };

  const sharePost = (post: Post) => {
    activePostMenu.value = null;
  };

  const reportPost = (post: Post) => {
    activePostMenu.value = null;
    notificationStore.addNotification({
      type: 'system',
      message: 'Post reported. Thank you for helping keep our community safe.',
      link: '/feed'
    });
  };

  const deletePost = (post: Post) => {
    activePostMenu.value = null;
    notificationStore.addNotification({
      type: 'system',
      message: 'Post deleted successfully',
      link: '/feed'
    });
  };

  const toggleFavorite = (post: Post) => {
    post.isFavorite = !post.isFavorite;
    
    // Notify the post's author if someone favorited their post
    if (post.author.id !== authStore.user?.id && post.isFavorite) {
      notificationStore.addNotification({
        type: 'favorite',
        message: `${authStore.user?.name} favorited your post "${post.title}"`,
        link: `/post/${post.id}`,
        userId: post.author.id,
        postId: post.id,
        user: {
          name: authStore.user?.name || 'User',
          initials: authStore.user?.initials || 'U'
        }
      });
    }
  };
  
  // Create post handler
  const handleCreatePost = async (postData: Post) => {
    try {
      await feedStore.createPost({
        content: postData.content,
        imageUrl: postData.imageUrl
      });
    } catch (error) {
      console.error("Failed to create post:", error);
      notificationStore.addNotification({
        type: 'error',
        message: 'Failed to create post.',
        link: '/feed'
      });
    } finally {
      showCreateModal.value = false;
    }
  };
  
  // Handle click outside to close dropdowns
  const handleClickOutside = (event: MouseEvent): void => {
    if (activePostMenu.value) {
        const menu = document.querySelector(`[data-post-menu="${activePostMenu.value}"]`);
        const button = document.querySelector(`[data-post-menu-button="${activePostMenu.value}"]`);
        if (
        menu && 
        !menu.contains(event.target as Node) &&
        button &&
        !button.contains(event.target as Node)
        ) {
        activePostMenu.value = null;
        }
    }
  };

  const fetchPostsIfNeeded = () => {
    if (!user.value) {
      const interval = setInterval(() => {
        if (user.value) {
          clearInterval(interval);
          feedStore.fetchPosts();
        }
      }, 200);
    } else {
      feedStore.fetchPosts();
    }
  };

  
  onMounted(() => {
    document.addEventListener('click', handleClickOutside);
    fetchPostsIfNeeded();
  });
  
  onUnmounted(() => {
    document.removeEventListener('click', handleClickOutside);
  });
  onUnmounted(() => {
    if (reactionHideTimer) {
      clearTimeout(reactionHideTimer);
    }
  });

  onActivated(fetchPostsIfNeeded);
  </script>
  <style>
  .transform {
    transition: transform 0.2s ease-in-out;
  }
  .scale-125 {
    transform: scale(1.25);
  }
  .scale-110 {
    transform: scale(1.1);
  }
  </style>