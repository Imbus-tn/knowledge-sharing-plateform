<template>
  <div class="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
    <div class="bg-white dark:bg-slate-800 rounded-xl w-full max-w-2xl max-h-[90vh] overflow-y-auto">
      <!-- Header -->
      <div class="p-6 border-b dark:border-slate-700">
        <h2 class="text-xl font-bold">
          {{ mode === 'new-post' ? 'Create Post' : 'Share a Link' }}
        </h2>
      </div>

      <!-- Form -->
      <div class="p-6 space-y-6">
        <!-- Mode Toggle -->
        <div class="flex space-x-2">
          <button
            @click="mode = 'new-post'"
            :class="mode === 'new-post' ? 'bg-blue-600 text-white' : 'bg-slate-200 dark:bg-slate-700'"
            class="px-4 py-2 rounded-lg"
          >
            New Post
          </button>
          <button
            @click="mode = 'share-link'"
            :class="mode === 'share-link' ? 'bg-blue-600 text-white' : 'bg-slate-200 dark:bg-slate-700'"
            class="px-4 py-2 rounded-lg"
          >
            Share a Link
          </button>
        </div>

        <!-- Content Input (New Post Only) -->
        <div v-if="mode === 'new-post'" class="space-y-2">
          <label class="text-sm font-medium">Content</label>
          <textarea
            v-model="content"
            class="w-full p-3 border rounded-lg dark:bg-slate-700 dark:border-slate-600"
            rows="4"
            placeholder="What's on your mind?"
          />
        </div>

        <!-- Link Input (Share Link Only) -->
        <div v-if="mode === 'share-link'" class="space-y-2">
          <label class="text-sm font-medium">Link URL</label>
          <input
            v-model="linkUrl"
            @input="fetchLinkPreview"
            class="w-full p-3 border rounded-lg dark:bg-slate-700 dark:border-slate-600"
            placeholder="https://example.com"
          />

          <!-- Link Preview -->
          <div v-if="linkPreview" class="mt-4 p-4 border rounded-lg bg-slate-50 dark:bg-slate-700">
            <img v-if="linkPreview.imageUrl" :src="linkPreview.imageUrl" class="w-full h-32 object-cover rounded" />
            <h3 class="font-medium mt-2">{{ linkPreview.title }}</h3>
            <p class="text-sm text-slate-600 dark:text-slate-400 line-clamp-2">{{ linkPreview.description }}</p>
          </div>
        </div>

        <!-- Tags Input (Both Modes) -->
        <div class="space-y-2">
          <label class="text-sm font-medium">Tags</label>
          <div class="flex flex-wrap gap-2 mb-2">
            <span
              v-for="tag in tags"
              :key="tag"
              class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800"
            >
              {{ tag }}
              <button @click="tags = tags.filter(t => t !== tag)" class="ml-1">✕</button>
            </span>
          </div>
          <input
            v-model="tagInput"
            @keypress.enter="addTag"
            placeholder="Press Enter to add tag"
            class="w-full px-3 py-2 border rounded-lg dark:bg-slate-700 dark:border-slate-600"
          />
        </div>

        <!-- Image Upload -->
        <div class="space-y-2">
          <label class="text-sm font-medium">Image (Optional)</label>
          <input
            type="file"
            @change="handleImageUpload"
            accept="image/*"
            class="w-full"
          />
          <div v-if="uploadedImage" class="mt-2">
            <img :src="uploadedImage" class="w-32 h-32 object-cover rounded" />
          </div>
        </div>

        <!-- Additional Notes (Share Link Only) -->
        <div v-if="mode === 'share-link'" class="space-y-2">
          <label class="text-sm font-medium">Additional Notes</label>
          <textarea
            v-model="additionalNotes"
            class="w-full p-3 border rounded-lg dark:bg-slate-700 dark:border-slate-600"
            rows="2"
            placeholder="Add your thoughts..."
          />
        </div>
      </div>

      <!-- Footer -->
      <div class="p-6 border-t dark:border-slate-700 flex justify-end space-x-3">
        <button
          @click="$emit('close')"
          class="px-4 py-2 text-slate-600 dark:text-slate-300 hover:bg-slate-100 dark:hover:bg-slate-700 rounded-lg"
        >
          Cancel
        </button>
        <button
          @click="handleSubmit"
          class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
        >
          Post
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, defineEmits } from 'vue';
import { apiClient } from '../api';
import { useFeedStore } from '../stores/feed';
// Props & Emits
const emit = defineEmits(['close', 'submit']);
const feedStore = useFeedStore();
// Form state
const mode = ref<'new-post' | 'share-link'>('new-post');
const content = ref('');
const linkUrl = ref('');
const additionalNotes = ref('');
const tagInput = ref('');
const tags = ref<string[]>([]);
const uploadedImage = ref<string | null>(null);
const linkPreview = ref<any>(null);

const handleCreatePost = async (data: {
  content: string;
  imageFile?: File;
  tags: string[];
}) => {
  let imageUrl: string | null = null;

  if (data.imageFile) {
    const formData = new FormData();
    formData.append('file', data.imageFile);
    const response = await apiClient.post('/content/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });
    imageUrl = response.data.imageUrl; // e.g., "/content/images/abc.jpg"
  }

  await feedStore.createPost({
    content: data.content,
    imageUrl: imageUrl,
    tags: data.tags
  });
};
// Add tag
const addTag = () => {
  const value = tagInput.value.trim();
  if (value && !tags.value.includes(value)) {
    tags.value.push(value);
    tagInput.value = '';
  }
};

// Handle image upload
const handleImageUpload = (event: Event) => {
  const input = event.target as HTMLInputElement;
  if (input.files && input.files[0]) {
    const file = input.files[0];
    const reader = new FileReader();
    reader.onload = (e) => {
      uploadedImage.value = e.target?.result as string;
    };
    reader.readAsDataURL(file);
  }
};

// Fetch link preview via backend proxy
const fetchLinkPreview = async () => {
  if (!linkUrl.value.trim()) {
    linkPreview.value = null;
    return;
  }

  try {
    const response = await apiClient.get('/link-preview', {
      params: { url: linkUrl.value }
    });
    linkPreview.value = response.data;
  } catch (error) {
    console.error('Failed to fetch link preview:', error);
    linkPreview.value = {
      title: 'No preview available',
      description: '',
      imageUrl: null
    };
  }
};

// Submit handler
const handleSubmit = async () => {
  try {
    let imageUrl: string | null = null;

    // ✅ Upload image if exists
    if (uploadedImage.value && uploadedImage.value.startsWith('data:')) {
      const blob = await fetch(uploadedImage.value).then(r => r.blob());
      const formData = new FormData();
      formData.append('file', blob, 'upload.jpg');

      const response = await apiClient.post('/api/content/upload', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      });
      imageUrl = response.data.imageUrl; // e.g., "/content/images/abc.jpg"
    }

    // ✅ Create post via store
    if (mode.value === 'new-post') {
      await feedStore.createPost({
        content: content.value,
        imageUrl: imageUrl,
        tags: tags.value
      });
    } else {
      // For share-link mode, you might handle differently
      // For now, just emit
      emit('submit', {
        mode: mode.value,
        content: additionalNotes.value,
        imageUrl: imageUrl,
        linkUrl: linkUrl.value,
        additionalNotes: additionalNotes.value,
        tags: tags.value
      });
    }

    // ✅ Reset form
    content.value = '';
    linkUrl.value = '';
    additionalNotes.value = '';
    tags.value = [];
    uploadedImage.value = null;
    linkPreview.value = null;
    tagInput.value = '';

    // ✅ Close modal
    emit('close');
  } catch (err: any) {
    console.error('Error creating post:', err);
    alert('Failed to create post: ' + err.message);
  }
};
</script>