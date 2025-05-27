<template>
    <div class="max-w-7xl mx-auto px-4 sm:px-6 py-6">
      <div class="grid grid-cols-1 lg:grid-cols-4 gap-6">
        <!-- Left Sidebar - User Profile -->
        <div class="lg:col-span-1">
          <div :class="[
            'backdrop-blur-sm rounded-xl border shadow-xl overflow-hidden transition-colors duration-200',
            isDark ? 'bg-slate-800/50 border-slate-700' : 'bg-slate-100/90 border-slate-300'
          ]">
            <!-- Cover Image -->
            <div class="h-24 bg-gradient-to-r from-slate-900 via-indigo-900 to-slate-900 relative overflow-hidden">
              <div class="absolute inset-0 bg-gradient-to-b from-transparent via-black/30 to-black/60"></div>
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
                />
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
        </div>
  
        <!-- Main Content -->
        <div class="lg:col-span-3">
          <!-- Search Bar -->
          <div class="mb-6">
            <SearchBar v-model="searchQuery" />
          </div>
  
          <!-- Empty State -->
          <div v-if="favorites.length === 0" class="text-center py-16">
            <div class="inline-flex items-center justify-center w-16 h-16 rounded-full bg-slate-800 mb-4">
              <Star class="w-8 h-8 text-slate-600" />
            </div>
            <h3 class="text-xl font-medium text-white mb-2">No favorites yet</h3>
            <p class="text-slate-400 max-w-md mx-auto mb-6">
              Start adding content to your favorites by clicking the star icon on any article, tutorial, or video.
            </p>
            <router-link to="/feed" class="inline-flex items-center px-4 py-2 bg-emerald-500 text-white rounded-lg hover:bg-emerald-600 transition-colors">
              Browse Content
            </router-link>
          </div>
  
          <!-- Favorites Grid -->
          <div v-else class="space-y-6">
            <div 
              v-for="item in filteredFavorites" 
              :key="item.id"
              class="backdrop-blur-sm rounded-xl border shadow-xl overflow-hidden"
              :class="isDark ? 'bg-slate-800/50 border-slate-700' : 'bg-slate-100/90 border-slate-300'"
            >
              <!-- Post Header -->
              <div class="p-4">
                <div class="flex items-center space-x-3">
                  <!-- Author Info -->
                  <div class="flex-shrink-0">
                    <div class="w-12 h-12 rounded-full bg-slate-700 flex items-center justify-center">
                      <span class="text-white font-medium">{{ getAuthorInitials(item.authorId) }}</span>
                    </div>
                  </div>
                  <div class="flex-1 min-w-0">
                    <p :class="[
                      'font-medium transition-colors',
                      isDark ? 'text-white' : 'text-slate-900'
                    ]">
                      {{ getAuthorName(item.authorId) }}
                    </p>
                    <p :class="[
                      'text-xs transition-colors',
                      isDark ? 'text-slate-500' : 'text-slate-400'
                    ]">
                      {{ formatDate(item.createdAt) }}
                    </p>
                  </div>
                  <!-- Favorite Button -->
                  <div>
                    <FavoriteButton :item="item" />
                  </div>
                </div>
                <p 
                  class="mt-3 transition-colors"
                  :class="isDark ? 'text-slate-300' : 'text-slate-700'"
                >
                  {{ item.description }}
                </p>
              </div>
              <!-- Post Image -->
              <img 
                v-if="item.coverImage"
                :src="item.coverImage" 
                :alt="item.title"
                class="w-full h-96 object-cover"
              />
              <!-- Post Stats -->
              <div class="px-4 py-2">
                <div class="flex items-center justify-between">
                  <div class="flex items-center space-x-1">
                    <div class="flex -space-x-1">
                      <div class="w-7 h-7 rounded-full bg-slate-700/50 flex items-center justify-center text-lg ring-2 ring-slate-800 transform hover:scale-110 transition-transform cursor-pointer">
                        ❤️
                      </div>
                    </div>
                    <span class="text-slate-400 text-sm ml-2">{{ formatNumber(item.likes) }}</span>
                  </div>
                  <div class="flex items-center space-x-4 text-sm text-slate-400">
                    <div class="flex items-center">
                      <MessageSquare class="w-4 h-4 mr-1" />
                      <span>{{ formatNumber(item.comments) }}</span>
                    </div>
                    <div class="flex items-center">
                      <Share2 class="w-4 h-4 mr-1" />
                      <span>{{ formatNumber(item.shares) }}</span>
                    </div>
                  </div>
                </div>
              </div>
              <!-- Post Actions -->
              <div class="px-4 py-2">
                <div class="flex items-center justify-between">
                  <button 
                    class="flex items-center space-x-2 px-4 py-2 rounded-lg transition-colors"
                    :class="[
                      isDark 
                        ? 'text-slate-300 hover:bg-slate-700/50' 
                        : 'text-slate-600 hover:bg-slate-200/50'
                    ]"
                  >
                    <ThumbsUp class="w-5 h-5" />
                    <span>Like</span>
                  </button>
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
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </template>
  <script setup lang="ts">
  import { ref, computed, onMounted } from 'vue';
  import { useAuthStore } from '../stores/auth';
  import { useThemeStore } from '../stores/theme';
 
  import { User, LayoutDashboard, Bookmark, MessageSquare, Share2, ThumbsUp, Star } from 'lucide-vue-next';
  import SearchBar from '../components/SearchBar.vue';
  import FavoriteButton from '../components/FavoriteButton.vue';
  
  const authStore = useAuthStore();
  const themeStore = useThemeStore();
  const favoritesStore = useFavoritesStore();
  
  const user = computed(() => authStore.user);
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
  
  // Quick stats
  const quickStats = computed(() => ({
    articlesPublished: user.value?.articlesCount || 24,
    totalViews: user.value?.totalViews || '12.4K',
    contributions: user.value?.contributions || 156
  }));
  
  // Trending topics data
  const trendingTopics = [
    { tag: 'vue3', icon: 'Code', posts: '2.5k posts' },
    { tag: 'devops', icon: 'Server', posts: '1.8k posts' },
    { tag: 'cloud', icon: 'Cloud', posts: '1.2k posts' },
    { tag: 'database', icon: 'Database', posts: '956 posts' },
    { tag: 'terminal', icon: 'Terminal', posts: '845 posts' },
    { tag: 'security', icon: 'Lock', posts: '734 posts' }
  ];
  
  const favorites = computed(() => favoritesStore.items);
  const searchQuery = ref('');
  
  const filteredFavorites = computed(() => {
    let result = [...favorites.value];
    if (searchQuery.value) {
      const query = searchQuery.value.toLowerCase();
      result = result.filter(item => 
        item.title.toLowerCase().includes(query) || 
        item.description.toLowerCase().includes(query)
      );
    }
    return result;
  });
  
  const formatDate = (date: string) => {
    return new Date(date).toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric',
      year: 'numeric'
    });
  };
  
  const formatNumber = (num: number) => {
    return new Intl.NumberFormat('en-US', { notation: 'compact' }).format(num);
  };
  
  const getAuthorInitials = (authorId: string) => {
    return 'JD';
  };
  
  const getAuthorName = (authorId: string) => {
    return 'John Doe';
  };
  
  onMounted(() => {
    if (favorites.value.length === 0) {
      favoritesStore.generateMockFavorites();
    }
  });
  </script>