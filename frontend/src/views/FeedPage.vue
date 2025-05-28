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
                <div class="flex items-center justify-between">
                  <!-- Author Info -->
                  <div class="flex items-center space-x-3">
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
                          isDark ? 'bg-emerald-500' : 'bg-emerald-600'
                        ]"
                      >
                        <span class="text-white font-medium">{{ post.author.initials }}</span>
                      </div>
                    </div>
                    <div class="min-w-0">
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
                  </div>

                  <!-- Tags & More Menu -->
                  <div class="flex items-center space-x-2">
                    <!-- Post Tags -->
                    <div class="flex flex-wrap gap-1">
                      <div 
                        v-for="tag in post.tags" 
                        :key="tag.name" 
                        :class="[
                          'px-2 py-1 text-xs rounded-full transition-colors',
                          tag.color
                        ]"
                      >
                        {{ tag.name }}
                      </div>
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
                      <div 
                        v-if="activePostMenu === post.id"
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
                            @click="openDeleteConfirmation(post)"
                            :class="[
                              'flex items-center w-full px-4 py-2 text-sm transition-colors',
                              isDark ? 'text-red-400 hover:bg-slate-700' : 'text-red-600 hover:bg-slate-100'
                            ]"
                          >
                            <Trash2 :class="isDark ? 'text-red-400' : 'text-red-600'" class="w-4 h-4 mr-2" />
                            Delete Post
                          </button>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>

                <!-- Post Title & Content -->
                <h3 :class="[
                  'text-xl font-semibold mt-4 mb-2 transition-colors',
                  isDark ? 'text-white' : 'text-slate-900'
                ]">
                  {{ post.title }}
                </h3>
                <p :class="[
                  'transition-colors',
                  isDark ? 'text-slate-300' : 'text-slate-700'
                ]">
                  {{ post.content }}
                </p>
              </div>

              <!-- Post Media -->
              <div v-if="post.imageUrl || post.videoUrl" class="relative">
                <img 
                  v-if="post.imageUrl"
                  :src="post.imageUrl" 
                  :alt="post.title"
                  class="w-full h-96 object-cover"
                />
                <div v-if="post.videoUrl" class="relative">
                  <img 
                    :src="post.videoUrl.thumbnail" 
                    :alt="post.title"
                    class="w-full h-96 object-cover"
                  />
                  <div class="absolute inset-0 flex items-center justify-center bg-black/50">
                    <button class="w-16 h-16 rounded-full bg-white/20 flex items-center justify-center hover:bg-white/30 transition-colors">
                      <Play class="w-8 h-8 text-white" />
                    </button>
                  </div>
                </div>
              </div>

              <!-- Post Stats (Original) -->
              <div class="px-4 py-2">
                <div class="flex items-center justify-between">
                  <div class="flex items-center space-x-1">
                    <div class="flex -space-x-1">
                      <div 
                        v-for="reaction in getTopReactions(post)" 
                        :key="reaction.emoji"
                        :class="[
                          'w-7 h-7 rounded-full flex items-center justify-center text-lg transform hover:scale-110 transition-transform cursor-pointer',
                          isDark ? 'bg-slate-700/50' : 'bg-slate-200/50' // Conditional background
                        ]"
                        :title="`${reaction.count} ${reaction.emoji}`"
                      >
                        {{ reaction.emoji }}
                      </div>
                    </div>
                    <span :class="[
                      'text-sm ml-2 transition-colors',
                      isDark ? 'text-slate-400' : 'text-slate-500'
                    ]">
                      {{ getTotalReactions(post) }}
                    </span>
                  </div>
                  <div class="flex items-center space-x-4 text-sm" 
                    :class="isDark ? 'text-slate-400' : 'text-slate-500'">
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

              <!-- Post Actions (Original) -->
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
        :is-open="showCreateModal"
        @close="showCreateModal = false"
        @submit="handleCreatePost"
      />

      <ConfirmModal
        :is-open="isDeleteConfirmOpen"
        title="Delete Post"
        :message="`Are you sure you want to delete this post? This action cannot be undone.`"
        @confirm="confirmDeletePost"
        @cancel="closeDeleteConfirmation"
      />

      <ReportModal
        :is-open="showReportModal"
        entity-type="post"
        @close="showReportModal = false"
        @submit="handleReport"
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
  Edit, Flag, Trash2, Play
} from 'lucide-vue-next'; // Added Play icon for video posts
import CreatePostModal from '../components/CreatePostModal.vue';
import { useNotificationStore } from '../stores/notification';
import ConfirmModal from '../components/ConfirmModal.vue';
import ReportModal from '../components/ReportModal.vue';

// Mock data structure
interface Post {
  id: string;
  title: string;
  content: string;
  imageUrl: string | null;
  videoUrl?: {
    url: string;
    thumbnail: string;
  };
  author: {
    id: number;
    name: string;
    initials: string;
    avatarUrl?: string;
  };
  createdAt: string;
  comments: number;
  shares: number;
  isFavorite: boolean;
  reactions: {
    emoji: string;
    count: number;
    users: string[];
  }[];
  tags: {
    name: string;
    color: string;
  }[];
}


// Use mock data in development
const useMockData = import.meta.env.VITE_USE_MOCK_DATA === 'true';

// Mock posts data
const mockPosts = ref<Post[]>([
  {
    id: '1',
    title: "Building Scalable Microservices with Kubernetes",
    content: "In this comprehensive guide, we'll explore best practices for designing and deploying microservices architecture using Kubernetes...",
    imageUrl: "https://images.unsplash.com/photo-1667372393119-3d4c48d07fc9?auto=format&fit=crop&w=1600&q=80 ",
    author: {
      id: 1,
      name: "Habib ben hassine",
      initials: "HB",
      avatarUrl: new URL('../assets/kakashi.png', import.meta.url).href 
    },
    createdAt: "2025-05-10T08:30:00.000Z",
    comments: 12,
    shares: 8,
    isFavorite: false,
    reactions: [
      { emoji: '👍', count: 24, users: ['user1', 'user2'] },
      { emoji: '🚀', count: 15, users: ['user3'] },
      { emoji: '💡', count: 10, users: ['user4'] }
    ],
    tags: [
      { name: "DevOps", color: "bg-purple-500/10 text-purple-500" },
      { name: "Kubernetes", color: "bg-blue-500/10 text-blue-500" }
    ]
  },
  {
    id: '2',
    title: "Vue 3 Performance Optimization Techniques",
    content: "Learn advanced techniques for optimizing your Vue 3 applications, including Composition API best practices and rendering strategies...",
    imageUrl: null,
    videoUrl: {
      url: "https://example.com/videos/vue3-performance.mp4 ",
      thumbnail: "https://images.unsplash.com/photo-1633356122544-f134324a6cee?auto=format&fit=crop&w=1600&q=80 "
    },
    author: {
      id: 2,
      name: "Dhaker bellil",
      initials: "DB",
      avatarUrl: new URL('../assets/obito.png', import.meta.url).href 
    },
    createdAt: "2025-05-09T15:45:00.000Z",
    comments: 18,
    shares: 12,
    isFavorite: true,
    reactions: [
      { emoji: '👍', count: 32, users: ['user5', 'user6'] },
      { emoji: '🔥', count: 20, users: ['user7'] },
      { emoji: '❤️', count: 15, users: ['user8'] }
    ],
    tags: [
      { name: "Vue.js", color: "bg-emerald-500/10 text-emerald-500" },
      { name: "Performance", color: "bg-amber-500/10 text-amber-500" }
    ]
  },
  {
    id: '3',
    title: "The Future of Web Development in 2025",
    content: "Exploring upcoming trends and technologies that will shape the web development landscape in the coming years, from AI integration to new frameworks...",
    imageUrl: null,
    author: {
      id: 3,
      name: "roua ben hassine",
      initials: "RB",
      avatarUrl: new URL('../assets/mikasa.png', import.meta.url).href 
    },
    createdAt: "2025-05-09T10:15:00.000Z",
    comments: 25,
    shares: 15,
    isFavorite: false,
    reactions: [
      { emoji: '👍', count: 18, users: ['user11', 'user12'] },
      { emoji: '🤔', count: 9, users: ['user13'] }
    ],
    tags: [
      { name: "Web Development", color: "bg-green-500/10 text-green-500" },
      { name: "Trends", color: "bg-pink-500/10 text-pink-500" }
    ]
  }
]);

// Auth and theme stores
const authStore = useAuthStore();
const themeStore = useThemeStore();
const user = computed(() => authStore.user);
const notificationStore = useNotificationStore();
const showCreateModal = ref(false);
const activePostMenu = ref<string | null>(null);
const isDark = computed(() => themeStore.isDark);

// Use mock data
const posts = computed(() => mockPosts.value);

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

// Get author avatar URL
const getAuthorAvatar = (author: any) => {
  const rawUrl = author?.avatarUrl;

  if (!rawUrl) return ''; // Fallback if no avatar

  // Use raw URL if it's already absolute (http/https) or a local path (/...)
  if (rawUrl.startsWith('http') || rawUrl.startsWith('/')) {
    return rawUrl;
  }

  // Otherwise, prepend API base URL (for future remote images)
  const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080';
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

// Reaction state
const showReactionPickerFor = ref<string | null>(null);
const reactionPickerPosition = ref({ top: '0px', left: '0px' });
const reactionEmojis = ['👍', '❤️', '😊', '🎉', '🤔', '👏', '🔥', '💯', '✨', '🙌'];
let reactionHideTimer: number | null = null;

const isDeleteConfirmOpen = ref(false);
const selectedPostToDelete = ref<Post | null>(null);

const openDeleteConfirmation = (post: Post) => {
  selectedPostToDelete.value = post;
  activePostMenu.value = null;
  isDeleteConfirmOpen.value = true;
};

const closeDeleteConfirmation = () => {
  isDeleteConfirmOpen.value = false;
  selectedPostToDelete.value = null;
};

const confirmDeletePost = () => {
  if (!selectedPostToDelete.value) return;
  
  if (useMockData) {
    // For mock data
    mockPosts.value = mockPosts.value.filter(
      post => post.id !== selectedPostToDelete.value?.id
    );
  } else {
    // For production: call your API
    // const feedStore = useFeedStore();
    // await feedStore.deletePost(selectedPostToDelete.value.id);
  }
  
  notificationStore.addNotification({
    type: 'system',
    message: 'Post deleted successfully',
    link: '/feed'
  });
  
  closeDeleteConfirmation();
};

// Get top reactions for a post
const getTopReactions = (post: Post) => {
  return post.reactions
    .sort((a: any, b: any) => b.count - a.count)
    .slice(0, 3);
};

// Get total reactions for a post
const getTotalReactions = (post: Post) => {
  return post.reactions.reduce((total: number, reaction: any) => total + reaction.count, 0);
};

// Reaction handlers
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
  return reaction?.users.includes('user1') || false;
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

// Post menu toggle
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


// Show/Hide Report Modal
const showReportModal = ref(false);

// Method to open the Report Modal
const reportPost = (post: Post) => {
  showReportModal.value = true;
};

// Handle report submission
const handleReport = (reportData:any) => {
  console.log('Report submitted:', reportData);
  
  // Notify admin or send to backend
  notificationStore.addNotification({
    type: 'system',
    message: `You've reported this post successfully.`,
    link: '/feed'
  });

  showReportModal.value = false;
};

// Favorite toggle
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

// Create post handler with mock data support
const handleCreatePost = (newPost: Post) => {
  mockPosts.value.unshift(newPost);
  showCreateModal.value = false;
}

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

// Lifecycle hooks
onMounted(() => {
  document.addEventListener('click', handleClickOutside);
});

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside);
  if (reactionHideTimer) {
    clearTimeout(reactionHideTimer);
  }
});

onActivated(() => {});
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