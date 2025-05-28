<template>
  <div :class="[
    'max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8',
    isDark ? 'text-slate-300' : 'text-slate-700'
  ]">
    <!-- Profile Header (Shared by all roles) -->
    <div :class="[
      'bg-slate-800/50 backdrop-blur-sm rounded-2xl border shadow-xl mb-8',
      isDark ? 'border-slate-700' : 'border-slate-300'
    ]">
      <!-- Cover Image -->
      <div class="h-64 bg-gradient-to-r from-slate-900 via-indigo-900 to-slate-900 relative overflow-hidden rounded-2xl">
        <div class="absolute inset-0 bg-gradient-to-b from-transparent via-black/30 to-black/60"></div>
        <div class="absolute inset-0 bg-[radial-gradient(circle_at_20%_30%,rgba(255,255,255,0.05),transparent)]"></div>
      </div>
      <!-- Profile Info -->
      <div class="absolute bottom-0 left-0 right-0 p-8 text-white">
        <div class="flex items-end space-x-6">
          <!-- Avatar -->
          <div class="relative">
            <img 
              v-if="avatarUrl" 
              :src="avatarUrl" 
              alt="User Avatar"
              class="w-32 h-32 rounded-2xl object-cover border-4"
              :class="isDark ? 'border-slate-800' : 'border-white'"
            >
            <div 
              v-else 
              :class="[
                'w-32 h-32 rounded-2xl border-4 flex items-center justify-center shadow-xl',
                isDark ? 'bg-slate-700 border-slate-800' : 'bg-slate-200 border-white'
              ]"
            >
              <span 
                class="text-4xl font-bold"
                :class="isDark ? 'text-white' : 'text-slate-900'"
              >
                {{ userInitials }}
              </span>
            </div>
            <div 
              class="absolute -bottom-2 -right-2 w-8 h-8 rounded-lg flex items-center justify-center border-4"
              :class="[
                isDark ? 'border-slate-800' : 'border-white',
                roleBadgeContainerStyle
              ]"
            >
              <component 
                :is="roleBadgeIcon" 
                class="w-4 h-4"
                :class="roleBadgeIconColor"
              />
            </div>
          </div>
          <!-- User Details -->
          <div class="flex-1 pb-2">
            <h1 class="text-3xl font-bold mb-2 text-white">
              {{ user?.name }}
            </h1>
            <p class="text-slate-300">
              {{ user?.email }}
            </p>
            <div class="mt-2 flex items-center space-x-2">
              <div v-if="user" class="mt-1">
                <div
                  class="inline-flex items-center px-2 py-0.5 rounded-full text-xs"
                  :class="roleStyle(user?.role ?? UserRole.USER)"
                >
                  {{ user?.role?.toLowerCase() ?? UserRole.USER }}
                </div>
              </div>
            </div>
          </div>
          <!-- Edit Profile Button -->
          <router-link 
            to="/edit-profile" 
            :class="[
              'px-4 py-2.5 rounded-xl border transition-all duration-200 flex items-center space-x-2',
              isDark 
                ? 'bg-white/10 hover:bg-white/20 border-white/20 text-white' 
                : 'bg-slate-100/90 hover:bg-slate-200 border-slate-200 text-slate-800'
            ]"
          >
            <Settings 
              :class="isDark ? 'text-slate-300' : 'text-slate-600'" 
              class="w-4 h-4" 
            />
            <span>Edit Profile</span>
          </router-link>
        </div>
      </div>
    </div>
    
    <!-- Main Content Grid -->
    <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
      <!-- Left Column (Shared by all roles) -->
      <div class="col-span-2 space-y-8">
        <!-- About Section (Shared by all roles) -->
        <div :class="[
          'rounded-2xl p-6 border shadow-xl',
          isDark 
            ? 'bg-slate-800/50 border-slate-700' 
            : 'bg-slate-100/90 border-slate-300'
        ]">
          <h3 :class="[
            'text-xl font-semibold mb-4',
            isDark ? 'text-white' : 'text-slate-900'
          ]">
            About
          </h3>
          <p :class="isDark ? 'text-slate-300' : 'text-slate-700'">
            {{ user?.bio || 'No bio provided' }}
          </p>
          <div class="mt-4 flex flex-wrap gap-4">
            <a 
              v-if="user?.location" 
              href="#" 
              :class="[
                'flex items-center px-4 py-2 rounded-xl transition-colors',
                isDark 
                  ? 'bg-slate-700/50 text-slate-300 hover:text-white' 
                  : 'bg-slate-200/70 text-slate-700 hover:text-slate-900'
              ]"
            >
              <MapPin 
                :class="isDark ? 'text-slate-300' : 'text-slate-600'" 
                class="w-4 h-4 mr-2" 
              />
              {{ user.location }}
            </a>
            <a 
              v-if="user?.github" 
              :href="`https://github.com/ ${user.github}`" 
              target="_blank" 
              :class="[
                'flex items-center px-4 py-2 rounded-xl transition-colors',
                isDark 
                  ? 'bg-slate-700/50 text-slate-300 hover:text-white' 
                  : 'bg-slate-200/70 text-slate-700 hover:text-slate-900'
              ]"
            >
              <Github 
                :class="isDark ? 'text-slate-300' : 'text-slate-600'" 
                class="w-4 h-4 mr-2" 
              />
              {{ user.github }}
            </a>
            <a 
              v-if="user?.linkedin" 
              :href="user.linkedin" 
              target="_blank" 
              :class="[
                'flex items-center px-4 py-2 rounded-xl transition-colors',
                isDark 
                  ? 'bg-slate-700/50 text-slate-300 hover:text-white' 
                  : 'bg-slate-200/70 text-slate-700 hover:text-slate-900'
              ]"
            >
              <Linkedin 
                :class="isDark ? 'text-slate-300' : 'text-slate-600'" 
                class="w-4 h-4 mr-2" 
              />
              LinkedIn
            </a>
            <a 
              v-if="user?.phoneNumber" 
              href="#" 
              :class="[
                'flex items-center px-4 py-2 rounded-xl transition-colors',
                isDark 
                  ? 'bg-slate-700/50 text-slate-300 hover:text-white' 
                  : 'bg-slate-200/70 text-slate-700 hover:text-slate-900'
              ]"
            >
              <Phone 
                :class="isDark ? 'text-slate-300' : 'text-slate-600'" 
                class="w-4 h-4 mr-2" 
              />
              {{ user.phoneNumber }}
            </a>
          </div>
        </div>
        
        <!-- Conditional Rendering Based on Role -->
        <div v-if="authStore.isAdmin || authStore.isContributor">
          <!-- Recent Activity (Admin/Contributor) -->
          <div :class="[
            'rounded-2xl p-6 border shadow-xl',
            isDark 
              ? 'bg-slate-800/50 border-slate-700' 
              : 'bg-slate-100/90 border-slate-300'
          ]">
            <div class="flex items-center justify-between mb-4">
              <h3 :class="[
                'text-xl font-semibold',
                isDark ? 'text-white' : 'text-slate-900'
              ]">
                Recent Activity
              </h3>
              <router-link 
                to="/activity" 
                :class="[
                  'flex items-center transition-colors',
                  isDark ? 'text-emerald-500 hover:text-emerald-400' : 'text-emerald-600 hover:text-emerald-500'
                ]"
              >
                <span>View All</span>
                <ArrowRight class="w-4 h-4 ml-1" />
              </router-link>
            </div>
            <div 
              v-for="activity in recentActivity" 
              :key="activity.id" 
              :class="[
                'flex items-start space-x-4 p-4 rounded-lg mb-3',
                isDark 
                  ? 'bg-slate-700/30 hover:bg-slate-700/50' 
                  : 'bg-slate-200/70 border-slate-300'
              ]"
            >
              <component 
                :is="activity.icon" 
                :class="isDark ? 'text-emerald-500' : 'text-emerald-600'" 
                class="w-5 h-5 flex-shrink-0" 
              />
              <div>
                <p :class="isDark ? 'text-white' : 'text-slate-900'">
                  {{ activity.description }}
                </p>
                <p :class="isDark ? 'text-xs text-slate-400 mt-1' : 'text-xs text-slate-500 mt-1'">
                  {{ activity.time }}
                </p>
              </div>
            </div>
          </div>
        </div>
        
        <div v-else>
        <!-- Recent Favorites (Regular User) -->
        <div class="mt-8">
          <div :class="[
            'rounded-2xl p-6 border shadow-xl',
            isDark ? 'bg-slate-800/50 border-slate-700' : 'bg-slate-100/90 border-slate-300'
          ]">
            <div class="flex items-center justify-between mb-6">
              <h2 class="text-xl font-semibold transition-colors duration-200"
                  :class="isDark ? 'text-white' : 'text-slate-900'">
                Recent Favorites
              </h2>
              <router-link 
                to="/favorites" 
                :class="[
                  'flex items-center transition-colors',
                  isDark ? 'text-emerald-500 hover:text-emerald-400' : 'text-emerald-600 hover:text-emerald-500'
                ]"
              >
                <span>View All</span>
                <ArrowRight class="w-4 h-4 ml-1" />
              </router-link>
            </div>
            <div class="space-y-6">
              <div 
              v-for="favorite in recentFavorites" 
              :key="favorite.id" 
              class="flex items-start space-x-4 p-4 rounded-xl group cursor-pointer transition-colors"
              :class="[
                isDark 
                  ? 'bg-slate-700/30 hover:bg-slate-700/50' 
                  : 'bg-slate-200/70 hover:bg-slate-300/50'
              ]"
              @click="router.push('/favorites')"
            >
                <div class="w-16 h-16 rounded-lg bg-slate-800 overflow-hidden flex-shrink-0">
                  <img 
                    :src="favorite.image" 
                    :alt="favorite.title" 
                    class="w-full h-full object-cover" 
                  />
                </div>
                <div class="flex-1 min-w-0">
                  <div class="flex items-center space-x-2 mb-1">
                    <span class="px-2 py-0.5 text-xs rounded-full" :class="favorite.tagColor">
                      {{ favorite.tag }}
                    </span>
                    <span class="text-sm"
                          :class="isDark ? 'text-slate-400' : 'text-slate-500'">
                      {{ favorite.date }}
                    </span>
                  </div>
                  <h3 class="font-medium mb-1 transition-colors duration-200
                        group-hover:text-emerald-600"
                        :class="isDark ? 'text-white' : 'text-slate-900'">
                    {{ favorite.title }}
                  </h3>
                  <p class="text-sm line-clamp-1"
                    :class="isDark ? 'text-slate-400' : 'text-slate-600'">
                    {{ favorite.description }}
                  </p>
                </div>
                <Star class="w-5 h-5 text-amber-400 fill-current" />
              </div>
            </div>
          </div>
        </div>
      </div>
      </div>
      
      <!-- Right Column (Conditional Content) -->
      <div class="space-y-8">
        <!-- Quick Stats (Shared by all roles) -->
        <div :class="[
          'rounded-2xl p-6 border shadow-xl',
          isDark 
            ? 'bg-slate-800/50 border-slate-700' 
            : 'bg-slate-100/90 border-slate-300'
        ]">
          <h3 :class="[
            'text-xl font-semibold mb-4',
            isDark ? 'text-white' : 'text-slate-900'
          ]">
            Quick Stats
          </h3>
          <div class="grid grid-cols-2 gap-4">
            <div 
              v-for="stat in displayedQuickStats" 
              :key="stat.label" 
              :class="[
                'p-4 rounded-xl',
                isDark 
                  ? 'bg-slate-700/30' 
                  : 'bg-slate-200/70'
              ]"
            >
              <p :class="isDark ? 'text-slate-300' : 'text-slate-600'">
                {{ stat.label }}
              </p>
              <p :class="[
                'font-medium',
                isDark ? 'text-white' : 'text-slate-900'
              ]">
                {{ stat.value }}
              </p>
            </div>
          </div>
        </div>
        
        <!-- Conditional Rendering Based on Role -->
        <div v-if="authStore.isAdmin || authStore.isContributor">
          <!-- Contribution Stats (Admin/Contributor) -->
          <div :class="[
            'rounded-2xl p-6 border shadow-xl',
            isDark 
              ? 'bg-slate-800/50 border-slate-700' 
              : 'bg-slate-100/90 border-slate-300'
          ]">
            <div class="flex items-center justify-between mb-4">
              <h3 :class="[
                'text-xl font-semibold',
                isDark ? 'text-white' : 'text-slate-900'
              ]">
                Contribution Stats
              </h3>
              <router-link 
                to="/dashboard" 
                :class="[
                  'flex items-center transition-colors',
                  isDark ? 'text-emerald-500 hover:text-emerald-400' : 'text-emerald-600 hover:text-emerald-500'
                ]"
              >
                <span>View Dashboard</span>
                <ArrowRight class="w-4 h-4 ml-1" />
              </router-link>
            </div>
            <div class="space-y-4">
              <div 
                v-for="stat in contributionStats" 
                :key="stat.label" 
                class="relative"
              >
                <div class="flex items-center justify-between text-sm mb-2">
                  <div class="flex items-center">
                    <component 
                      :is="stat.icon" 
                      :class="isDark ? 'text-emerald-500' : 'text-emerald-600'" 
                      class="w-4 h-4 mr-2" 
                    />
                    <span :class="isDark ? 'text-slate-300' : 'text-slate-600'">
                      {{ stat.label }}
                    </span>
                  </div>
                  <div class="flex items-center space-x-2">
                    <span :class="isDark ? 'text-white' : 'text-slate-900'">
                      {{ stat.count }}
                    </span>
                    <span :class="isDark ? 'text-slate-400' : 'text-slate-500'">
                      / {{ stat.total }}
                    </span>
                  </div>
                </div>
                <div :class="isDark ? 'bg-slate-700/50' : 'bg-slate-200/50'" class="h-2 rounded-full overflow-hidden">
                  <div 
                    class="h-full rounded-full transition-all duration-500"
                    :class="isDark ? 'bg-emerald-500' : 'bg-emerald-600'"
                    :style="{ width: `${stat.percentage}%` }"
                  ></div>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <div v-else>
          <!-- Top Interests -->
          <div :class="[
            'rounded-2xl p-6 border shadow-xl',
            isDark 
              ? 'bg-slate-800/50 border-slate-700' 
              : 'bg-slate-100/90 border-slate-300'
          ]">
            <h2 class="text-xl font-semibold mb-6 transition-colors duration-200" 
                :class="isDark ? 'text-white' : 'text-slate-900'">
              Top Interests
            </h2>
            <div class="space-y-4">
              <div class="flex items-center justify-between group">
                <div class="flex items-center space-x-3">
                  <div class="w-8 h-8 rounded-lg flex items-center justify-center"
                      :class="isDark ? 'bg-blue-500/10' : 'bg-blue-500/10'">
                    <Database class="w-4 h-4" 
                              :class="isDark ? 'text-blue-500' : 'text-blue-500'" />
                  </div>
                  <span class="transition-colors duration-200 group-hover:text-emerald-500"
                        :class="isDark ? 'text-slate-300' : 'text-slate-700'">
                    Database Design
                  </span>
                </div>
                <span :class="isDark ? 'text-slate-400' : 'text-slate-500'">
                  42 reads
                </span>
              </div>
              <div class="flex items-center justify-between group">
                <div class="flex items-center space-x-3">
                  <div class="w-8 h-8 rounded-lg flex items-center justify-center"
                      :class="isDark ? 'bg-purple-500/10' : 'bg-purple-500/10'">
                    <Code class="w-4 h-4" 
                          :class="isDark ? 'text-purple-500' : 'text-purple-500'" />
                  </div>
                  <span class="transition-colors duration-200 group-hover:text-emerald-500"
                        :class="isDark ? 'text-slate-300' : 'text-slate-700'">
                    Frontend Development
                  </span>
                </div>
                <span :class="isDark ? 'text-slate-400' : 'text-slate-500'">
                  35 reads
                </span>
              </div>
              <div class="flex items-center justify-between group">
                <div class="flex items-center space-x-3">
                  <div class="w-8 h-8 rounded-lg flex items-center justify-center"
                      :class="isDark ? 'bg-emerald-500/10' : 'bg-emerald-500/10'">
                    <Cloud class="w-4 h-4" 
                          :class="isDark ? 'text-emerald-500' : 'text-emerald-500'" />
                  </div>
                  <span class="transition-colors duration-200 group-hover:text-emerald-500"
                        :class="isDark ? 'text-slate-300' : 'text-slate-700'">
                    Cloud Computing
                  </span>
                </div>
                <span :class="isDark ? 'text-slate-400' : 'text-slate-500'">
                  28 reads
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue';
import { useAuthStore } from '../stores/auth';
import { 
  Shield, Edit3, User, Settings, MapPin, 
  Github, Linkedin, Phone, ArrowRight, FileText, Code, Users, Star, Database, Cloud
} from 'lucide-vue-next';
import { useThemeStore } from '../stores/theme';
import { useRouter } from 'vue-router';
import { UserRole } from '../types/UserRole';

// Auth and Theme Stores
const router = useRouter ();
const themeStore = useThemeStore();
const isDark = computed(() => themeStore.isDark);
const authStore = useAuthStore();
const user = computed(() => authStore.user);

// Avatar handling
const avatarUrl = computed(() => {
  const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080'; 
  return user.value?.avatarUrl
    ? `${apiUrl}${user.value.avatarUrl}`
    : '';
});

// User initials for avatar fallback
const userInitials = computed(() => {
  return user.value?.name
    ? user.value.name.split(' ').map(n => n[0]).join('').toUpperCase()
    : '';
});

const roleStyle = (role: UserRole) => {
  return {
    'bg-emerald-500/10 text-emerald-500': role === 'ADMIN',
    'bg-orange-500/10 text-orange-500': role === 'CONTRIBUTOR',
    'bg-blue-500/10 text-blue-500': role === 'USER'
  };
};

// Role badge styling
const roleBadgeContainerStyle = computed(() => {
  switch(user.value?.role) {
    case 'ADMIN':
      return 'bg-emerald-500';
    case 'CONTRIBUTOR':
      return 'bg-orange-500';
    default:
      return 'bg-blue-500';
  }
});

const roleBadgeIcon = computed(() => {
  switch(user.value?.role) {
    case 'ADMIN': return Shield;
    case 'CONTRIBUTOR': return Edit3;
    default: return User;
  }
});

const roleBadgeIconColor = computed(() => 'text-white');


// Quick Stats (Admin/Contributor)
const quickStats = computed(() => [
  { label: 'Articles Published', value: user.value?.articlesCount || 24 },
  { label: 'Total Views', value: user.value?.totalViews || '12.4K' },
  { label: 'Contributions', value: user.value?.contributions || 156 },
  { label: 'Member Since', value: user.value?.memberSince || 'Feb 2024' }
]);

// Contribution stats (Admin/Contributor)
const contributionStats = computed(() => [
  { 
    label: 'Technical Articles', 
    count: user.value?.articlesCount || 24, 
    total: 30,
    percentage: Math.round((user.value?.articlesCount || 24) / 30 * 100),
    icon: FileText 
  },
  { 
    label: 'Code Reviews', 
    count: user.value?.reviewsCount || 156, 
    total: 180,
    percentage: Math.round((user.value?.reviewsCount || 156) / 180 * 100),
    icon: Code 
  },
  { 
    label: 'Knowledge Shares', 
    count: user.value?.sharesCount || 42, 
    total: 50,
    percentage: Math.round((user.value?.sharesCount || 42) / 50 * 100),
    icon: Users 
  }
]);

// Recent Favorites Data (Regular User)
const recentFavorites = [
  {
    id: 1,
    title: "Vue.js Performance Optimization Techniques",
    description: "Learn how to optimize your Vue.js applications for better performance",
    tag: "Frontend",
    tagColor: "bg-blue-500/10 text-blue-500",
    date: "2h ago",
    image: "https://images.unsplash.com/photo-1633356122544-f134324a6cee?auto=format&fit=crop&w=300&q=80 "
  },
  {
    id: 2,
    title: "Docker Deployment Strategies",
    description: "A comprehensive guide to deploying applications using Docker",
    tag: "DevOps",
    tagColor: "bg-purple-500/10 text-purple-500",
    date: "1d ago",
    image: "https://images.unsplash.com/photo-1605745341112-85968b19335b?auto=format&fit=crop&w=300&q=80 "
  },
  {
    id: 3,
    title: "GraphQL API Design Patterns",
    description: "Best practices for designing scalable GraphQL APIs",
    tag: "Backend",
    tagColor: "bg-emerald-500/10 text-emerald-500",
    date: "3d ago",
    image: "https://images.unsplash.com/photo-1555066931-4365d14bab8c?auto=format&fit=crop&w=300&q=80 "
  }
];

// User-specific Quick Stats (Regular User)
const userQuickStats = [
  { label: 'Discussions', value: user.value?.discussionsCount || 156 },
  { label: 'Comments', value: user.value?.commentsCount || 324 },
  { label: 'Reactions', value: user.value?.reactionsCount || 892 },
  { label: 'Member Since', value: user.value?.memberSince || 'Feb 2024' }
];

// Conditional Quick Stats
const displayedQuickStats = computed(() => {
  return (authStore.isAdmin || authStore.isContributor) 
    ? quickStats.value 
    : userQuickStats;
});

// Recent Activity (Admin/Contributor)
const recentActivity = computed(() => [
  {
    id: 1,
    icon: FileText,
    description: 'Published article: "Vue 3 Composition API Patterns"',
    time: '2 hours ago'
  },
  {
    id: 2,
    icon: Code,
    description: 'Submitted Spring Boot microservice optimization PR #456',
    time: '5 hours ago'
  },
  {
    id: 3,
    icon: Users,
    description: 'Shared knowledge base: REST API design best practices',
    time: '1 day ago'
  },
  {
    id: 4,
    icon: FileText,
    description: 'Updated documentation: Spring Boot 3 migration guide',
    time: '3 days ago'
  },
  {
    id: 5,
    icon: Code,
    description: 'Merged Vue 3 component refactoring PR #321',
    time: '4 days ago'
  }
]);

onMounted(async () => {
  try {
    await authStore.fetchUser();
  } catch (error) {
    console.error('Failed to load profile:', error);
  }
});
</script>