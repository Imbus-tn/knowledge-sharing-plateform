<template>
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">
      <!-- Profile Header -->
      <div class="bg-slate-800/50 backdrop-blur-sm rounded-2xl border border-slate-700 mb-8">
        <!-- Cover Image -->
        <div class="h-64 bg-gradient-to-r from-slate-900 via-indigo-900 to-slate-900 relative overflow-hidden">
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
                class="w-32 h-32 rounded-2xl object-cover border-4 border-slate-800 shadow-xl"
              >
              <div 
                v-else 
                class="w-32 h-32 rounded-2xl bg-slate-700 border-4 border-slate-800 flex items-center justify-center shadow-xl"
              >
                <span class="text-4xl font-bold">{{ userInitials }}</span>
              </div>
              <div 
                class="absolute -bottom-2 -right-2 w-8 h-8 rounded-lg flex items-center justify-center border-4 border-slate-800"
                :class="roleBadgeContainerStyle"
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
              <h1 class="text-3xl font-bold mb-2">{{ user?.name }}</h1>
              <p class="text-slate-300">{{ user?.email }}</p>
              <div class="mt-2 flex items-center space-x-2">
                <div v-if="user" class="mt-1 inline-flex items-center px-2 py-0.5 rounded-full text-xs" 
                    :class="roleStyle(user?.role)">
                  {{ user?.role.toLowerCase() }}
                </div>
              </div>
            </div>
  
            <!-- Edit Profile Button -->
            <router-link 
              to="/edit-profile" 
              class="px-4 py-2.5 bg-white/10 hover:bg-white/20 backdrop-blur-sm rounded-xl border border-white/20 transition-all duration-200 flex items-center space-x-2"
            >
              <Settings class="w-4 h-4" />
              <span>Edit Profile</span>
            </router-link>
          </div>
        </div>
      </div>
  
      <!-- Main Content Grid -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
        <!-- Left Column -->
        <div class="col-span-2 space-y-8">
          <!-- About Section -->
          <div class="bg-slate-800/50 rounded-2xl p-6 border border-slate-700">
            <h3 class="text-xl font-semibold text-white mb-4">About</h3>
            <p class="text-slate-300">{{ user?.bio || 'No bio provided' }}</p>
            <div class="mt-4 flex flex-wrap gap-4">
              <a 
                v-if="user?.location" 
                href="#" 
                class="flex items-center px-4 py-2 bg-slate-700/50 rounded-xl text-slate-300 hover:text-white transition-colors"
              >
                <MapPin class="w-4 h-4 mr-2" />
                {{ user.location }}
              </a>
              <a 
                v-if="user?.github" 
                :href="`https://github.com/${user.github}`" 
                target="_blank" 
                class="flex items-center px-4 py-2 bg-slate-700/50 rounded-xl text-slate-300 hover:text-white transition-colors"
              >
                <Github class="w-4 h-4 mr-2" />
                {{ user.github }}
              </a>
              <a 
                v-if="user?.linkedin" 
                :href="user.linkedin" 
                target="_blank" 
                class="flex items-center px-4 py-2 bg-slate-700/50 rounded-xl text-slate-300 hover:text-white transition-colors"
              >
                <Linkedin class="w-4 h-4 mr-2" />
                LinkedIn
              </a>
              <a 
                v-if="user?.phoneNumber" 
                href="#" 
                class="flex items-center px-4 py-2 bg-slate-700/50 rounded-xl text-slate-300 hover:text-white transition-colors"
              >
                <Phone class="w-4 h-4 mr-2" />
                {{ user.phoneNumber }}
              </a>
            </div>
          </div>
  
          <!-- Recent Activity -->
          <div class="bg-slate-800/50 rounded-2xl p-6 border border-slate-700">
            <div class="flex items-center justify-between mb-4">
              <h3 class="text-xl font-semibold text-white">Recent Activity</h3>
              <router-link 
                to="/activity" 
                class="text-emerald-500 hover:text-emerald-400 flex items-center transition-colors"
              >
                <span>View All</span>
                <ArrowRight class="w-4 h-4 ml-1" />
              </router-link>
            </div>
            <div 
              v-for="activity in recentActivity" 
              :key="activity.id" 
              class="flex items-start space-x-4 p-4 rounded-lg bg-slate-700/30 transition-colors"
            >
              <component :is="activity.icon" class="w-5 h-5 text-emerald-500 flex-shrink-0" />
              <div>
                <p class="text-white">{{ activity.description }}</p>
                <p class="text-xs text-slate-400 mt-1">{{ activity.time }}</p>
              </div>
            </div>
          </div>
        </div>
  
        <!-- Right Column (Stats) -->
        <div class="space-y-8">
          <!-- Quick Stats -->
          <div class="bg-slate-800/50 rounded-2xl p-6 border border-slate-700">
            <h3 class="text-xl font-semibold text-white mb-4">Quick Stats</h3>
            <div class="grid grid-cols-2 gap-4">
              <div 
                v-for="stat in quickStats" 
                :key="stat.label" 
                class="p-4 bg-slate-700/30 rounded-xl"
              >
                <p class="text-slate-300 mb-1">{{ stat.label }}</p>
                <p class="text-white font-medium">{{ stat.value }}</p>
              </div>
            </div>
          </div>
  
          <!-- Contribution Stats -->
          <div class="bg-slate-800/50 rounded-2xl p-6 border border-slate-700">
            <div class="flex items-center justify-between mb-4">
              <h3 class="text-xl font-semibold text-white">Contribution Stats</h3>
              <router-link 
                to="/dashboard" 
                class="text-emerald-500 hover:text-emerald-400 flex items-center transition-colors"
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
                    <component :is="stat.icon" class="w-4 h-4 text-emerald-500 mr-2" />
                    <span class="text-slate-300">{{ stat.label }}</span>
                  </div>
                  <div class="flex items-center space-x-2">
                    <span class="text-white font-medium">{{ stat.count }}</span>
                    <span class="text-slate-400">/ {{ stat.total }}</span>
                  </div>
                </div>
                <div class="h-2 bg-slate-700/50 rounded-full overflow-hidden">
                  <div 
                    class="h-full bg-emerald-500 rounded-full transition-all duration-500" 
                    :style="{ width: `${stat.percentage}%` }"
                  ></div>
                </div>
              </div>
            </div>
          </div>
  
          <!-- Activity Heatmap -->
          <div class="bg-slate-800/50 rounded-2xl p-6 border border-slate-700">
            <h3 class="text-xl font-semibold text-white mb-4">Activity Overview</h3>
            <div class="grid grid-cols-7 gap-2 mb-4">
              <div 
                v-for="n in 28" 
                :key="n" 
                class="group relative w-full pt-[100%] rounded-md cursor-pointer transition-all duration-200 hover:ring-2 hover:ring-emerald-500/50"
                :class="getActivityClass(n)"
              >
                <!-- Tooltip -->
                <div class="absolute inset-0 flex items-center justify-center">
                  <div class="invisible group-hover:visible bg-slate-700 text-white text-xs rounded-lg px-2 py-1.5 whitespace-nowrap z-10">
                    {{ getActivityTooltip(n) }}
                    <div class="absolute bottom-0 left-1/2 -translate-x-1/2 translate-y-1/2 w-2 h-2 bg-slate-700 rotate-45"></div>
                  </div>
                </div>
              </div>
            </div>
            <div class="flex items-center justify-between mt-4 text-xs text-slate-400">
              <span>Less</span>
              <div class="flex items-center space-x-1">
                <div class="w-3 h-3 rounded bg-slate-700/30"></div>
                <div class="w-3 h-3 rounded bg-emerald-500/30"></div>
                <div class="w-3 h-3 rounded bg-emerald-500/60"></div>
                <div class="w-3 h-3 rounded bg-emerald-500"></div>
              </div>
              <span>More</span>
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
    Shield, 
    Edit3,
    User,
    Settings, 
    MapPin, 
    Globe, 
    Github, 
    Linkedin, 
    Phone,  
    ArrowRight,
    FileText,
    MessageSquare,
    Heart,
    Code,
    Users
  } from 'lucide-vue-next';
import type { UserRole } from '../types/UserRole';
  
  const authStore = useAuthStore();
  const user = computed(() => authStore.user);
  
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

  const roleBadgeIconColor = computed(() => {
    switch(user.value?.role) {
      case 'ADMIN': return 'text-white';
      case 'CONTRIBUTOR': return 'text-white';
      default: return 'text-white';
    }
  });

  const roleStyle = (role : UserRole) => {
    return {
      'bg-emerald-500/10 text-emerald-500': role === 'ADMIN',
      'bg-orange-500/10 text-orange-500': role === 'CONTRIBUTOR',
      'bg-blue-500/10 text-blue-500': role === 'USER'
    };
  };
  
  
  const quickStats = computed(() => [
    { label: 'Articles Published', value: user.value?.articlesCount || 24 },
    { label: 'Total Views', value: user.value?.totalViews || '12.4K' },
    { label: 'Contributions', value: user.value?.contributions || 156 },
    { label: 'Member Since', value: user.value?.memberSince || 'Feb 2024' }
  ]);
  
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
  
  // Activity heatmap styling
  const getActivityClass = (n: number) => {
    const date = new Date();
    date.setDate(date.getDate() - (28 - n));
    const dayOfWeek = date.getDay();
    const random = Math.random();
    
    if (dayOfWeek > 0 && dayOfWeek < 6) { // Weekdays
      if (random < 0.4) return 'bg-emerald-500';
      if (random < 0.7) return 'bg-emerald-500/60';
      if (random < 0.9) return 'bg-emerald-500/30';
    }
    return 'bg-slate-700/30';
  };
  
  // Activity tooltip content
  const getActivityTooltip = (n: number) => {
    const date = new Date();
    date.setDate(date.getDate() - (28 - n));
    const formattedDate = date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
    const activityClass = getActivityClass(n);
    let contributions = 0;
    
    switch (activityClass) {
      case 'bg-emerald-500':
        contributions = Math.floor(Math.random() * 8) + 8; // 8-15 contributions
        break;
      case 'bg-emerald-500/60':
        contributions = Math.floor(Math.random() * 5) + 3; // 3-7 contributions
        break;
      case 'bg-emerald-500/30':
        contributions = Math.floor(Math.random() * 2) + 1; // 1-2 contributions
        break;
    }
    
    return `${contributions} contributions on ${formattedDate}`;
  };
  
  // Fetch user data on mount
  onMounted(async () => {
    try {
      await authStore.fetchUser();
    } catch (error) {
      console.error('Failed to load profile:', error);
    }
  });
  </script>