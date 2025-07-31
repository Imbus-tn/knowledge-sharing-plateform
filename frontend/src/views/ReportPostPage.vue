<template>
  <div class="max-w-4xl mx-auto px-4 sm:px-6 py-8">
    <h1 class="text-2xl font-bold mb-6 text-white">Reported Posts</h1>

    <div v-if="loading" class="text-center text-slate-400 py-12">
      Loading reported posts...
    </div>

    <div v-else-if="reportedPosts.length === 0" class="text-center text-slate-400 py-12">
      No reported posts at the moment.
    </div>

    <div v-else class="space-y-6">
      <div
        v-for="report in reportedPosts"
        :key="report.id"
        class="backdrop-blur-sm rounded-lg border p-6 shadow-xl transition-colors"
        :class="isDark ? 'bg-slate-800/50 border-slate-700' : 'bg-slate-100/90 border-slate-300'"
      >
        <!-- Post Title + Warning Button -->
        <div class="flex justify-between items-start mb-3">
          <h2 class="text-lg font-semibold text-white">
            {{ report.post.title || 'Untitled Post' }}
          </h2>
          <button
            @click="sendWarning(report.post)"
            class="text-sm px-3 py-1 bg-red-600 hover:bg-red-700 text-white rounded-lg shadow flex items-center gap-1"
          >
            <AlertTriangle class="w-4 h-4" />
            Send Warning
          </button>
        </div>

        <!-- Report Details -->
        <p class="text-slate-300">
          <strong>Reason:</strong> {{ report.reason }}
        </p>
        <p class="text-sm text-slate-400 mt-1">
          <strong>Reported by:</strong> {{ report.reporter.name }}
        </p>
        <p class="text-sm text-slate-400">
          <strong>Date:</strong> {{ formatTime(report.reportedAt) }}
        </p>

        <!-- Post Preview -->
        <div class="mt-4 p-3 bg-slate-700/30 rounded-lg text-sm text-slate-300 line-clamp-3">
          {{ report.post.content }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { apiClient } from '@/api'
import { useThemeStore } from '@/stores/theme'
import { AlertTriangle } from 'lucide-vue-next'
import type { ReportedPost } from '@/types/post'

const themeStore = useThemeStore()
const isDark = computed(() => themeStore.isDark)

const reportedPosts = ref<ReportedPost[]>([])
const loading = ref<boolean>(true)

// Fetch reported posts
const fetchReportedPosts = async (): Promise<void> => {
  try {
    const response = await apiClient.get('/content/posts/reports')
    reportedPosts.value = response.data
  } catch (err: any) {
    console.error('Failed to load reported posts', err.response?.data || err)
    alert('Failed to load reported posts: ' + (err.response?.data?.message || err.message))
  } finally {
    loading.value = false
  }
}

// Send warning to post author
const sendWarning = async (post: any): Promise<void> => {
  const message = prompt(
    `Send warning to ${post.author.name}?`,
    `Your post "${post.title || 'Untitled'}" has been reported for: ${reportedPosts.value.find(r => r.post.id === post.id)?.reason}. Please review our community guidelines.`
  )
  if (!message) return

  try {
    await apiClient.post(`/content/posts/reports/warn/${post.id}`, { message })
    alert('Warning sent successfully!')
  } catch (err) {
    console.error('Failed to send warning', err)
    alert('Failed to send warning.')
  }
}

// Format date
const formatTime = (timestamp: string): string => {
  return new Date(timestamp).toLocaleString()
}

onMounted(() => {
  fetchReportedPosts()
})
</script>

<style scoped>
.line-clamp-3 {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>