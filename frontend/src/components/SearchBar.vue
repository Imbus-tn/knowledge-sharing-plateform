<template>
  <div class="relative max-w-2xl mx-auto group" ref="searchContainerRef">
    <!-- Search Input -->
    <SearchIcon
      class="absolute left-3 top-1/2 transform -translate-y-1/2 text-slate-400 group-focus-within:text-emerald-500 transition-colors cursor-pointer"
      @click="handleSearch"
    />
    <input
      v-model="query"
      type="text"
      placeholder="Search for tutorials, guides, or discussions..."
      class="w-full pl-10 pr-12 py-3.5 bg-slate-800/50 border border-slate-700 rounded-xl text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-emerald-500/50 focus:border-emerald-500 transition-all duration-200"
      @keydown.enter="handleEnter"
      @keydown.down.prevent="moveSelection(1)"
      @keydown.up.prevent="moveSelection(-1)"
      @input="debouncedFetch"
    />

    <!-- Dropdown -->
    <div
      v-if="query && results.length > 0 && dropdownOpen"
      class="absolute top-full mt-2 w-full bg-slate-800/90 backdrop-blur-md border border-slate-700 rounded-xl shadow-2xl z-50 max-h-96 overflow-y-auto"
    >
      <div class="p-2 text-xs text-slate-400 border-b border-slate-700">Recommended</div>
      <div
        v-for="(item, index) in results"
        :key="item.id"
        :class="['p-3 cursor-pointer border-b border-slate-700/50 last:border-0', selectedIndex === index ? 'bg-slate-700/50' : '']"
        @click="selectItem(item)"
      >
        <router-link :to="`/feed/${item.id}`" class="block">
          <h4 class="font-medium text-white">{{ item.title }}</h4>
          <p class="text-slate-400 text-sm line-clamp-2">{{ item.description }}</p>
          <span class="inline-block mt-1 px-2 py-1 text-xs rounded-full bg-emerald-500/20 text-emerald-400">
            {{ item.category }}
          </span>
        </router-link>
      </div>
    </div>

    <!-- Loading -->
    <div v-else-if="loading && dropdownOpen" class="absolute top-full mt-2 w-full bg-slate-800/90 backdrop-blur-md border border-slate-700 rounded-xl shadow-2xl z-50 p-4">
      <p class="text-slate-400 text-sm">Searching...</p>
    </div>

    <!-- No Results -->
    <div v-else-if="query && !loading && results.length === 0 && dropdownOpen" class="absolute top-full mt-2 w-full bg-slate-800/90 backdrop-blur-md border border-slate-700 rounded-xl shadow-2xl z-50 p-4">
      <p class="text-slate-400 text-sm">No results found.</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, watch } from 'vue';
import { Search as SearchIcon } from 'lucide-vue-next';
import { useRouter } from 'vue-router';

const query = ref('');
const results = ref<any[]>([]);
const loading = ref(false);
const dropdownOpen = ref(false);
const selectedIndex = ref(-1);
const router = useRouter();
const searchContainerRef = ref<HTMLElement | null>(null);
const API_BASE = import.meta.env.VITE_API_URL || 'http://localhost:8080';

// Fetch results from recommendation API
const fetchResults = async () => {
  console.log('🔍 Searching for:', query.value);

  const trimmed = query.value.trim();
  if (!trimmed) {
    results.value = [];
    dropdownOpen.value = false;
    return;
  }

  loading.value = true;
  results.value = [];
  dropdownOpen.value = true;

  try {
    const response = await fetch(
      `${API_BASE}/api/recommend/search?title=${encodeURIComponent(trimmed)}&top_n=5`
    );

    if (!response.ok) throw new Error(`HTTP ${response.status}`);
    const data = await response.json();

    // Handle array or object with recommendations
    results.value = Array.isArray(data)
      ? data
      : (data.recommendations || []);
    selectedIndex.value = -1;
  } catch (error) {
    console.error('❌ Search failed:', error);
    results.value = [];
  } finally {
    loading.value = false;
  }
};

// ✅ Add this missing method
const handleSearch = () => {
  fetchResults();
};

// Debounce
const debounce = (fn: Function, delay: number) => {
  let timeoutId: ReturnType<typeof setTimeout>;
  return (...args: any[]) => {
    clearTimeout(timeoutId);
    timeoutId = setTimeout(() => fn(...args), delay);
  };
};

const debouncedFetch = debounce(fetchResults, 300);

// Handle Enter key
const handleEnter = () => {
  if (selectedIndex.value >= 0) {
    selectItem(results.value[selectedIndex.value]);
  } else {
    fetchResults(); // Force search
  }
};

// Keyboard navigation
const moveSelection = (direction: number) => {
  if (results.value.length === 0) return;
  selectedIndex.value = (selectedIndex.value + direction + results.value.length) % results.value.length;
};

// Select item
const selectItem = (item: any) => {
  dropdownOpen.value = false;
  router.push(`/feed/${item.id}`);
};

// Click outside to close
const handleClickOutside = (event: MouseEvent) => {
  if (searchContainerRef.value && !searchContainerRef.value.contains(event.target as Node)) {
    dropdownOpen.value = false;
  }
};

onMounted(() => document.addEventListener('click', handleClickOutside));
onBeforeUnmount(() => document.removeEventListener('click', handleClickOutside));
watch(query, debouncedFetch);
</script>