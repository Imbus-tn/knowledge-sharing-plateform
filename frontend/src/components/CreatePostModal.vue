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
  
            <!-- Media Upload -->
            <div v-if="uploadedImage" class="relative w-full h-48 rounded-lg overflow-hidden mb-4">
              <img
                :src="uploadedImage"
                alt="Uploaded Preview"
                class="w-full h-full object-cover"
              />
              <button
                @click="removeImage"
                class="absolute top-2 right-2 bg-black/50 rounded-full p-1 text-white hover:bg-black/70 transition-colors"
              >
                <X class="w-4 h-4" />
              </button>
            </div>
  
            <div class="flex items-center justify-between">
              <div class="flex items-center space-x-4">
                <label
                  for="file-upload"
                  class="cursor-pointer p-2 text-slate-300 hover:text-white hover:bg-slate-700/50 rounded-lg transition-colors"
                >
                  <Image class="w-5 h-5" />
                  <input
                    id="file-upload"
                    type="file"
                    accept="image/*"
                    class="hidden"
                    @change="handleFileUpload"
                  />
                </label>
                <button
                  class="p-2 text-slate-300 hover:text-white hover:bg-slate-700/50 rounded-lg transition-colors"
                >
                  <Video class="w-5 h-5" />
                </button>
                <button
                  class="p-2 text-slate-300 hover:text-white hover:bg-slate-700/50 rounded-lg transition-colors"
                >
                  <FileText class="w-5 h-5" />
                </button>
              </div>
            </div>
          </div>
  
          <!-- Share a Link Mode -->
          <div v-if="mode === 'share-link'">
            <div class="mb-4">
              <input
                v-model="linkUrl"
                type="text"
                placeholder="Enter URL / Choose from reading history"
                class="w-full px-4 py-3 bg-slate-700/50 border border-slate-600 rounded-xl text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-emerald-500"
              />
            </div>
  
            <!-- Preview Section -->
            <div v-if="linkPreview" class="mb-4">
              <h3 class="text-lg font-semibold text-white mb-2">Preview:</h3>
              <div class="bg-slate-700/50 rounded-lg p-4">
                <img
                  v-if="linkPreview.imageUrl"
                  :src="linkPreview.imageUrl"
                  alt="Preview Image"
                  class="w-full h-48 object-cover mb-2 rounded-lg"
                />
                <h3 class="text-xl font-bold text-white">{{ linkPreview.title }}</h3>
                <p class="text-slate-300">{{ linkPreview.description }}</p>
              </div>
            </div>
  
            <!-- Additional Notes -->
            <textarea
              v-model="additionalNotes"
              rows="4"
              placeholder="Add your thoughts..."
              class="w-full px-4 py-3 bg-slate-700/50 border border-slate-600 rounded-xl text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-emerald-500 resize-none mb-4"
            ></textarea>
          </div>
  
          <!-- Actions -->
          <div class="flex items-center justify-between">
            <div class="flex items-center space-x-4">
              <!-- Add more options if needed -->
            </div>
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
    import { X, Globe, ChevronDown, Image, Video, FileText } from 'lucide-vue-next';
    import { apiClient } from '../api';

    // Auth Store
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

    const userName = computed(() => user.value?.name || 'John Doe');

    // State
    const mode = ref<'new-post' | 'share-link'>('new-post'); // Default to "New Post"
    const content = ref('');
    const uploadedImage = ref<string | null>(null);
    const linkUrl = ref('');
    const additionalNotes = ref('');
    const linkPreview = ref<{
    title?: string;
    description?: string;
    imageUrl?: string;
    }>({});
    const errorMessage = ref<string | null>(null);

    // Computed Properties
    const isSubmitDisabled = computed(() => {
    if (mode.value === 'new-post') {
        return !content.value.trim() && !uploadedImage.value;
    }
    if (mode.value === 'share-link') {
        return !linkUrl.value.trim() || !linkPreview.value.title;
    }
    return true;
    });

    // Methods
    const handleSubmit = () => {
      if (!content.value.trim() && !uploadedImage.value) {
          errorMessage.value = "Please add content or upload an image.";
          return;
      }

      emit("submit", {
          mode: mode.value,
          content: content.value,
          imageUrl: uploadedImage.value,
          linkUrl: linkUrl.value,
          additionalNotes: additionalNotes.value
      });

      // Reset form
      content.value = "";
      uploadedImage.value = null;
      linkUrl.value = "";
      additionalNotes.value = "";
      errorMessage.value = null;
  };

    const handleFileUpload = async (event: Event) => {
      const target = event.target as HTMLInputElement;
      if (!target.files || !target.files[0]) return;

      const file = target.files[0];
      const formData = new FormData();
      formData.append("file", file);

      try {
          const response = await apiClient.post("/upload/image", formData, {
              headers: {
                  "Content-Type": "multipart/form-data"
              }
          });
          // Prepend API URL to make image accessible in template
          const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080';
          uploadedImage.value = `${apiUrl}${response.data}`; 
      } catch (error: any) {
          errorMessage.value = "Failed to upload image.";
          console.error("Image upload failed:", error);
      }
  };

    const removeImage = () => {
        uploadedImage.value = null
    };

    const fetchLinkPreview = async () => {
    try {
        // Simulate fetching metadata from the URL
        // In a real app, use a service like Open Graph or a custom API
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

    // Watch for changes in linkUrl
    watch(linkUrl, (newValue) => {
    if (newValue.trim()) {
        fetchLinkPreview();
    } else {
        linkPreview.value = {};
    }
    });

    // Emit Events
    const emit = defineEmits<{
    (e: 'close'): void;
    (e: 'submit', data: {
        mode: 'new-post' | 'share-link';
        content: string;
        imageUrl: string ;
        linkUrl: string;
        additionalNotes: string;
    }): void;
    }>();
    </script>