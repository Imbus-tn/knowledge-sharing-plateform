<template>
  <div class="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center p-4 z-50">
    <div
      class="bg-slate-800 rounded-2xl shadow-2xl max-w-2xl w-full overflow-hidden transform transition-all duration-300 ease-in-out"
      @click.stop
    >
      <!-- Header -->
      <div class="flex justify-between items-center px-6 py-4 border-b border-slate-700">
        <h2 class="text-xl font-semibold text-white">Create a Post</h2>
        <button
          @click="$emit('close')"
          class="text-slate-400 hover:text-white focus:outline-none transition-colors"
        >
          <X class="w-5 h-5" />
        </button>
      </div>

      <!-- Tabs -->
      <div class="px-6 py-4 border-b border-slate-700">
        <ul class="flex space-x-4 mb-4">
          <li
            :class="{
              'text-emerald-500 border-b-2 border-emerald-500': mode === 'new-post',
              'text-slate-300 border-b-2 border-transparent': mode !== 'new-post'
            }"
            @click="mode = 'new-post'"
            class="cursor-pointer hover:text-emerald-500 transition-colors"
          >
            New Post
          </li>
          <li
            :class="{
              'text-emerald-500 border-b-2 border-emerald-500': mode === 'share-link',
              'text-slate-300 border-b-2 border-transparent': mode !== 'share-link'
            }"
            @click="mode = 'share-link'"
            class="cursor-pointer hover:text-emerald-500 transition-colors"
          >
            Share a Link
          </li>
        </ul>
      </div>

      <!-- Content -->
      <div class="px-6 py-5 space-y-4">
        <!-- User Info -->
        <div class="flex items-center space-x-3">
          <img
            v-if="avatarUrl"
            :src="avatarUrl"
            alt="User Avatar"
            class="w-12 h-12 rounded-full object-cover border-2 border-slate-800"
          />
          <div
            v-else
            class="w-12 h-12 rounded-full bg-emerald-500 border-2 border-slate-800 flex items-center justify-center"
          >
            <span class="text-white font-medium">{{ userInitials }}</span>
          </div>
          <div>
            <p class="text-white font-medium">{{ userName }}</p>
            <button
              class="flex items-center space-x-1 px-2 py-1 bg-slate-700/50 rounded-lg text-sm text-slate-300 hover:bg-slate-700/70 transition-colors"
            >
              <Globe class="w-4 h-4" />
              <span>Public</span>
              <ChevronDown class="w-4 h-4" />
            </button>
          </div>
        </div>

        <!-- New Post Mode -->
        <div v-if="mode === 'new-post'">
          <textarea
            v-model="content"
            rows="4"
            placeholder="What do you want to share?"
            class="w-full px-4 py-3 bg-slate-700/50 border border-slate-600 rounded-xl text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-emerald-500 resize-none mb-4"
          ></textarea>

          <!-- Tag Selection -->
          <div class="mb-4">
            <label class="block text-sm font-medium text-slate-300 mb-2">Add tags</label>
            <div class="relative">
              <input
                v-model="tagInput"
                @keydown.enter.prevent="addTag"
                @input="filterTags"
                type="text"
                placeholder="Type to search tags..."
                class="w-full px-4 py-2 bg-slate-700/50 border border-slate-600 rounded-lg text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-emerald-500"
              />
              <div
                v-if="showTagSuggestions && filteredTags.length > 0"
                class="absolute top-full left-0 right-0 mt-1 bg-slate-700 rounded-lg shadow-lg border border-slate-600 max-h-40 overflow-y-auto z-10"
              >
                <button
                  v-for="tag in filteredTags"
                  :key="tag"
                  @click="selectTag(tag)"
                  class="w-full px-4 py-2 text-left text-white hover:bg-slate-600 transition-colors"
                >
                  {{ tag }}
                </button>
              </div>
            </div>
            <div v-if="selectedTags.length > 0" class="flex flex-wrap gap-2 mt-3">
              <span
                v-for="tag in selectedTags"
                :key="tag"
                class="inline-flex items-center px-3 py-1 rounded-full text-sm bg-emerald-500/10 text-emerald-500 border border-emerald-500/20"
              >
                {{ tag }}
                <button @click="removeTag(tag)" class="ml-2 text-emerald-400 hover:text-emerald-300">
                  <X class="w-3 h-3" />
                </button>
              </span>
            </div>
          </div>

          <!-- Media Upload -->
          <div v-if="uploadedImage" class="relative w-full h-48 rounded-lg overflow-hidden mb-4">
            <img :src="uploadedImage" alt="Uploaded Preview" class="w-full h-full object-cover" />
            <button
              @click="removeImage"
              class="absolute top-2 right-2 bg-black/50 rounded-full p-1 text-white hover:bg-black/70 transition-colors"
            >
              <X class="w-4 h-4" />
            </button>
          </div>

          <div class="flex items-center space-x-4">
            <label for="file-upload" class="cursor-pointer p-2 text-slate-300 hover:text-white hover:bg-slate-700/50 rounded-lg transition-colors">
              <Image class="w-5 h-5" />
              <input id="file-upload" type="file" accept="image/*" class="hidden" @change="handleFileUpload" />
            </label>
          </div>
        </div>

        <!-- Share a Link Mode -->
        <div v-if="mode === 'share-link'">
          <input
            v-model="linkUrl"
            type="text"
            placeholder="Enter URL"
            class="w-full px-4 py-3 bg-slate-700/50 border border-slate-600 rounded-xl text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-emerald-500 mb-4"
          />
          <div v-if="linkPreview" class="mb-4">
            <div class="bg-slate-700/50 rounded-lg p-4">
              <img v-if="linkPreview.imageUrl" :src="linkPreview.imageUrl" alt="Preview Image" class="w-full h-48 object-cover mb-2 rounded-lg" />
              <h3 class="text-xl font-bold text-white">{{ linkPreview.title }}</h3>
              <p class="text-slate-300">{{ linkPreview.description }}</p>
            </div>
          </div>
          <textarea
            v-model="additionalNotes"
            rows="4"
            placeholder="Add your thoughts..."
            class="w-full px-4 py-3 bg-slate-700/50 border border-slate-600 rounded-xl text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-emerald-500"
          ></textarea>
        </div>

        <!-- Actions -->
        <div class="flex justify-end">
          <button
            @click="handleSubmit"
            :disabled="isSubmitDisabled"
            class="px-6 py-2 bg-emerald-500 text-white rounded-lg hover:bg-emerald-600 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
          >
            Post
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue';
import { useAuthStore } from '../stores/auth';
import { X, Globe, ChevronDown, Image } from 'lucide-vue-next';
import { apiClient } from '../api';

const authStore = useAuthStore();
const user = computed(() => authStore.user);
const avatarUrl = computed(() => {
  const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080';
  return user.value?.avatarUrl ? `${apiUrl}${user.value.avatarUrl}` : '';
});
const userInitials = computed(() => user.value?.name?.split(' ').map(n => n[0]).join('').toUpperCase() || '');
const userName = computed(() => user.value?.name || 'John Doe');

const mode = ref<'new-post' | 'share-link'>('new-post');
const content = ref('');
const uploadedImage = ref<string | null>(null);
const linkUrl = ref('');
const additionalNotes = ref('');
const linkPreview = ref<{ title?: string; description?: string; imageUrl?: string }>({});
const errorMessage = ref<string | null>(null);

const tagInput = ref('');
const selectedTags = ref<string[]>([]);
const showTagSuggestions = ref(false);
const availableTags = ['Vue', 'React', 'JavaScript', 'TypeScript', 'Python', 'Node.js', 'AI', 'Data Science'];

const filteredTags = computed(() => {
  if (!tagInput.value) return [];
  return availableTags.filter(tag =>
    tag.toLowerCase().includes(tagInput.value.toLowerCase()) &&
    !selectedTags.value.includes(tag)
  ).slice(0, 8);
});

const filterTags = () => {
  showTagSuggestions.value = tagInput.value.length > 0;
};

const selectTag = (tag: string) => {
  if (!selectedTags.value.includes(tag)) selectedTags.value.push(tag);
  tagInput.value = '';
  showTagSuggestions.value = false;
};

const addTag = () => {
  const tag = tagInput.value.trim();
  if (tag && !selectedTags.value.includes(tag)) selectedTags.value.push(tag);
  tagInput.value = '';
  showTagSuggestions.value = false;
};

const removeTag = (tag: string) => {
  selectedTags.value = selectedTags.value.filter(t => t !== tag);
};

const isSubmitDisabled = computed(() => {
  return mode.value === 'new-post'
    ? !content.value.trim() && !uploadedImage.value
    : !linkUrl.value.trim() || !linkPreview.value.title;
});

const handleFileUpload = async (event: Event) => {
  const target = event.target as HTMLInputElement;
  if (!target.files || !target.files[0]) return;
  const file = target.files[0];
  const formData = new FormData();
  formData.append('file', file);
  try {
    const response = await apiClient.post('/upload/image', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080';
    uploadedImage.value = `${apiUrl}${response.data}`;
  } catch (error) {
    console.error('Image upload failed:', error);
    errorMessage.value = 'Failed to upload image.';
  }
};

const removeImage = () => {
  uploadedImage.value = null;
};

const fetchLinkPreview = async () => {
  try {
    const response = await fetch(`/api/link-preview?url=${encodeURIComponent(linkUrl.value)}`);
    const data = await response.json();
    linkPreview.value = {
      title: data.title,
      description: data.description,
      imageUrl: data.image,
    };
  } catch (error) {
    console.error('Failed to fetch link preview:', error);
    linkPreview.value = {};
  }
};

watch(linkUrl, (newValue) => {
  if (newValue.trim()) fetchLinkPreview();
  else linkPreview.value = {};
});

const emit = defineEmits<{
  (e: 'close'): void;
  (e: 'submit', data: {
    mode: 'new-post' | 'share-link';
    content: string;
    imageUrl: string | null;
    linkUrl: string;
    additionalNotes: string;
    tags: string[];
  }): void;
}>();

const handleSubmit = () => {
  emit('submit', {
    mode: mode.value,
    content: content.value,
    imageUrl: uploadedImage.value,
    linkUrl: linkUrl.value,
    additionalNotes: additionalNotes.value,
    tags: selectedTags.value,
  });
  content.value = '';
  uploadedImage.value = null;
  linkUrl.value = '';
  additionalNotes.value = '';
  selectedTags.value = [];
  errorMessage.value = null;
};
</script>

<style scoped>
/* Add any custom styles if needed */
</style>
