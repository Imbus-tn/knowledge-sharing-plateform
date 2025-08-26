
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
         <!-- Recommended for You -->
 <div v-if="feedStore.recommendedPosts.length > 0" class="mb-8">
  <h2 class="text-2xl font-bold text-white mb-4">Recommended for You</h2>
  <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
    <div
      v-for="post in feedStore.recommendedPosts"
      :key="post.id"
      class="bg-slate-800/50 p-4 rounded-xl hover:bg-slate-700/50 transition cursor-pointer"
      @click="$router.push(`/feed/${post.id}`)"
    >
      <!-- Check if title and author exist -->
      <div v-if="post.title && post.author?.name">
        <h3 class="text-white font-semibold">{{ post.title }}</h3>
        <p class="text-slate-400 text-sm mt-1 line-clamp-2">{{ post.description }}</p>
        <div class="flex justify-between items-center mt-2 text-xs text-slate-500">
          <span>{{ post.author.name }}</span>
          <span>
            {{ formatDate(post.createdAt) }}
          </span>
        </div>
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
            <!-- 3-dot Menu Button -->
<div class="relative">
  <button
    @click="togglePostMenu(post.id)"
    class="flex items-center justify-center w-8 h-8 rounded-full hover:bg-slate-200 dark:hover:bg-slate-700 text-slate-500 dark:text-slate-400"
    :data-post-menu-button="post.id"
  >
    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
      <path d="M10 6a2 2 0 110-4 2 2 0 010 4zM10 12a2 2 0 110-4 2 2 0 010 4zM10 18a2 2 0 110-4 2 2 0 010 4z" />
    </svg>
  </button>

  <!-- Dropdown Menu -->
  <div
    v-if="activePostMenu === post.id"
    :data-post-menu="post.id"
    class="absolute right-0 mt-2 w-48 bg-white dark:bg-slate-800 rounded-lg shadow-xl border border-slate-200 dark:border-slate-700 z-50"
  >
    <button
      @click="savePost(post)"
      class="flex items-center w-full px-4 py-2 text-sm text-slate-700 dark:text-slate-200 hover:bg-slate-100 dark:hover:bg-slate-700"
    >
      <HeartIcon class="w-4 h-4 mr-2" />
      {{ post.isFavorite ? 'Remove from Favorites' : 'Save to Favorites' }}
    </button>

    <button
      @click="reportPost(post)"
      class="flex items-center w-full px-4 py-2 text-sm text-slate-700 dark:text-slate-200 hover:bg-slate-100 dark:hover:bg-slate-700"
    >
      <FlagIcon class="w-4 h-4 mr-2" />
      Report Post
    </button>

    <button
      @click="sharePost(post)"
      class="flex items-center w-full px-4 py-2 text-sm text-slate-700 dark:text-slate-200 hover:bg-slate-100 dark:hover:bg-slate-700"
    >
      <ShareIcon class="w-4 h-4 mr-2" />
      Share Post
    </button>

    <!-- Admin: Delete Post -->
    <button
      v-if="authStore.user?.role === 'ADMIN'"
      @click="deletePost(post)"
      class="flex items-center w-full px-4 py-2 text-sm text-red-600 dark:text-red-400 hover:bg-red-50 dark:hover:bg-red-900/30"
    >
      <TrashIcon class="w-4 h-4 mr-2" />
      Delete Post
    </button>
  </div>
</div>
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
                      isDark ? 'border-2 border-slate-800' : 'border-2 border-white',
                      isDark ? 'bg-slate-700' : 'bg-slate-600'
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
                    <span>{{ post.author.role || 'User' }} • {{ formatTime(post.createdAt) }}</span>
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
                        @click="editPost"
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
                       <!--<button 
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
                      </button> -->
                     <!-- <button 
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
                      </button>-->
                       <!-- Report Post -->
                      <button
                        @click="reportPost(post)"
                        class="flex items-center w-full px-4 py-2 text-sm text-slate-700 dark:text-slate-300 hover:bg-slate-100 dark:hover:bg-slate-700"
                      >
                        <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                        </svg>
                        <span>Report Post</span>
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
                    <span>{{ post.comments.length }}</span>
                  </div>
                  <div class="flex items-center">
                    <Share2 class="w-4 h-4 mr-1" />
                    <span>{{ post.shares.length }}</span>
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
    <!-- Create Post Modal -->
    <CreatePostModal 
      v-if="showCreateModal"
      @close="showCreateModal = false"
      @submit="handleCreatePost"
    />
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';
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
import type { Post } from '../types/post';
import type { Reaction } from '../types/reaction';
import { UserRole } from '../types/UserRole';
import { HeartIcon, FlagIcon, ShareIcon, TrashIcon } from '@heroicons/vue/24/outline';
const router = useRouter();
const feedStore = useFeedStore();
const authStore = useAuthStore();

const themeStore = useThemeStore();
const user = computed(() => authStore.user);
const notificationStore = useNotificationStore();
const showCreateModal = ref(false);
const activePostMenu = ref<number | null>(null);
const posts = computed(() => feedStore.posts);
const isDark = computed(() => themeStore.isDark);
const reactionPickerPosition = ref({ top: '0px', left: '0px' });
const formatDate = (dateString: string): string => {
  try {
    const date = new Date(dateString);
    if (isNaN(date.getTime())) {
      return 'Invalid Date';
    }
    return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
  } catch (e) {
    return 'Invalid Date';
  }
};
const avatarUrl = computed(() => {
  const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080';
  return user.value?.avatarUrl
    ? `${apiUrl}${user.value.avatarUrl}`
    : '';
});

const userInitials = computed(() => {
  return user.value?.name
    ? user.value.name.split(' ').map(n => n[0]).join('').toUpperCase()
    : '';
});

const getAuthorAvatar = (author: any) => {
  const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080';
  const rawUrl = author?.avatarUrl;
  if (!rawUrl) return '';
  if (rawUrl.startsWith('http')) return rawUrl;
  return `${apiUrl}${rawUrl}`;
};

const trendingTopics = [
  { tag: 'vue3', icon: Code, posts: '2.5k posts' },
  { tag: 'devops', icon: Server, posts: '1.8k posts' },
  { tag: 'cloud', icon: Cloud, posts: '1.2k posts' },
  { tag: 'database', icon: Database, posts: '956 posts' },
  { tag: 'terminal', icon: Terminal, posts: '845 posts' },
  { tag: 'security', icon: Lock, posts: '734 posts' }
];

const showReactionPickerFor = ref<number | null>(null);
const reactionEmojis = ['👍', '❤️', '😊', '🎉', '🤔', '👏', '🔥', '💯', '✨', '🙌'];
let reactionHideTimer: number | null = null;

const getTopReactions = (post: Post) => {
  return post.reactions
    .sort((a: Reaction, b: Reaction) => (b.count ?? 0) - (a.count ?? 0))
    .slice(0, 3);
};

const getTotalReactions = (post: Post) => {
  return post.reactions.reduce((total, r: Reaction) => total + (r.count ?? 0), 0);
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

const hasReacted = (post: Post, emoji: string): boolean => {
  const currentUserId = String(authStore.user?.id);
  if (!currentUserId) return false;
  const reaction = post.reactions.find((r: Reaction) => r.emoji === emoji);
  return reaction?.users?.includes(currentUserId) ?? false;
};

const addReaction = async (post: Post, emoji: string) => {
  const currentUser = authStore.user;
  const currentUserId = String(currentUser?.id);

  if (!currentUserId || !currentUser) {
    console.warn("User not logged in");
    return;
  }

  try {
    await feedStore.reactToPost(post.id, emoji);
    let reaction = post.reactions.find((r: Reaction) => r.emoji === emoji);

    if (reaction) {
      if (reaction.users?.includes(currentUserId)) {
        reaction.count = (reaction.count ?? 1) - 1;
        reaction.users = reaction.users.filter(u => u !== currentUserId);
        if ((reaction.count ?? 0) <= 0) {
          post.reactions = post.reactions.filter(r => r.emoji !== emoji);
        }
      } else {
        reaction.count = (reaction.count ?? 0) + 1;
        reaction.users = [...(reaction.users ?? []), currentUserId];
      }
    } else {
      post.reactions.push({
        emoji,
        count: 1,
        users: [currentUserId],
        user: {
          id: Number(currentUserId),
          name: currentUser.name ?? 'Anonymous',
          email: currentUser.email ?? '',
          role: currentUser.role ?? UserRole.USER
        },
        createdAt: new Date().toISOString()
      });
    }
  } catch (error) {
    console.error('Failed to add reaction:', error);
  }

  showReactionPickerFor.value = null;
};

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

const togglePostMenu = (postId: number): void => {
  activePostMenu.value = activePostMenu.value === postId ? null : postId;
};

const editPost = () => {
  activePostMenu.value = null;
  notificationStore.addNotification({
    type: 'system',
    message: 'Post editing coming soon!',
    link: '/feed'
  });
};

const savePost = async (post: Post) => {
  activePostMenu.value = null;
  try {
    await feedStore.toggleFavorite(post.id);
    notificationStore.addNotification({
      type: 'system',
      message: 'Post saved to your bookmarks',
      link: '/favorites'
    });
  } catch (error) {
    console.error('Failed to save post:', error);
    notificationStore.addNotification({
      type: 'system',
      message: 'Failed to save post.',
      link: '/feed'
    });
  }
};

const reportPost = async (post: Post) => {
  activePostMenu.value = null
  const reason = prompt('Why are you reporting this post?', 'Inappropriate content')
  if (!reason) return

  try {
    await feedStore.reportPost(post.id, { reason })
    notificationStore.addNotification({
      type: 'system',
      message: 'Post reported. Thank you for helping keep our community safe.',
      link: '/feed'
    })
  } catch (error) {
    notificationStore.addNotification({
      type: 'error',
      message: 'Failed to report post.',
      link: '/feed'
    })
  }
}

const sharePost = async (post: Post) => {
  activePostMenu.value = null
  try {
    await feedStore.sharePost(post.id)
    notificationStore.addNotification({
      type: 'system',
      message: 'Post shared successfully!',
      link: '/feed'
    })
  } catch (error) {
    notificationStore.addNotification({
      type: 'error',
      message: 'Failed to share post.',
      link: '/feed'
    })
  }
}




const deletePost = async (post: Post) => {
  activePostMenu.value = null;
  try {
    await feedStore.deletePost(post.id);
    notificationStore.addNotification({
      type: 'system',
      message: 'Post deleted successfully',
      link: '/feed'
    });
  } catch (error) {
    console.error('Failed to delete post:', error);
    notificationStore.addNotification({
      type: 'system',
      message: 'Failed to delete post.',
      link: '/feed'
    });
  }
};

const toggleFavorite = async (post: Post) => {
  try {
    await feedStore.toggleFavorite(post.id);
    if (post.author.id !== authStore.user?.id && post.isFavorite) {
      notificationStore.addNotification({
        type: 'favorite',
        message: `${authStore.user?.name} favorited your post "${post.title}"`,
        link: `/post/${post.id}`,
        userId: String(post.author.id),
        postId: String(post.id),
        user: {
          name: authStore.user?.name || 'User',
          initials: authStore.user?.initials || 'U'
        }
      });
    }
  } catch (error) {
    console.error('Failed to toggle favorite:', error);
    notificationStore.addNotification({
      type: 'system',
      message: 'Failed to toggle favorite.',
      link: '/feed'
    });
  }
};

const handleCreatePost = async (data: {
  mode: "new-post" | "share-link";
  content: string;
  imageUrl: string | undefined;
  linkUrl: string;
  additionalNotes: string;
  tags: string[];
}) => {
  try {
    //  Convert undefined → null
    const imageUrl: string | null = data.imageUrl ?? null;

    await feedStore.createPost({
      content: data.content,
      imageUrl,     //  Now string | null
      tags: data.tags
    });

    notificationStore.addNotification({
      type: 'system',
      message: 'Post created successfully',
      link: '/feed'
    });
  } catch (error) {
    console.error("Failed to create post:", error);
    notificationStore.addNotification({
      type: 'system',
      message: 'Failed to create post.',
      link: '/feed'
    });
  } finally {
    showCreateModal.value = false;
  }
};

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

onMounted(async () => {
  try {
    await feedStore.fetchPosts();
    await feedStore.fetchRecommendedPosts();
  } catch (error) {
    console.error('Failed to load feed:', error);
  }
});

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside);
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
```