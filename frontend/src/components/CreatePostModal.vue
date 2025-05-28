<template>
  <div v-if="showCreateModal" class="fixed inset-0 z-50 overflow-y-auto">
    <!-- Overlay -->
    <div class="fixed inset-0 bg-black/50 backdrop-blur-sm" @click="closeModal"></div>

    <!-- Modal Content -->
    <div class="flex items-center justify-center min-h-screen px-4">
      <div 
        :class="[
          'relative max-w-2xl w-full rounded-2xl shadow-xl p-6',
          isDark ? 'bg-slate-800/90 border border-slate-700' : 'bg-white border border-slate-200'
        ]"
      >
        <!-- Close Button -->
        <button 
          @click="closeModal"
          :class="[
            'absolute top-3 right-3 transition-colors',
            isDark ? 'text-slate-400 hover:text-slate-300' : 'text-slate-500 hover:text-slate-600'
          ]"
          class="p-1 rounded-full"
          aria-label="Close modal"
        >
          <X class="w-5 h-5" />
        </button>

        <!-- Header -->
        <div class="flex items-center mb-6">
          <h3 
            :class="[
              'font-semibold text-xl transition-colors duration-200',
              isDark ? 'text-white' : 'text-slate-900'
            ]"
          >
            Create a Post
          </h3>
        </div>

        <!-- Form Fields -->
        <div class="space-y-5">
          <!-- Title -->
          <div>
            <label 
              :class="[
                'block mb-2 transition-colors duration-200',
                isDark ? 'text-slate-300' : 'text-slate-700'
              ]"
            >
              Post Title
            </label>
            <input 
              ref="titleInput"
              v-model="newPost.title" 
              type="text" 
              placeholder="Give your post a catchy title"
              :class="[
                'w-full px-4 py-2 rounded-lg transition-all duration-200',
                isDark 
                  ? 'bg-slate-700/50 border border-slate-600 text-white placeholder-slate-400 focus:ring-blue-500' 
                  : 'bg-slate-100 border border-slate-300 text-slate-900 placeholder-slate-400 focus:ring-blue-500'
              ]"
            />
          </div>

          <!-- Content -->
          <div>
            <label 
              :class="[
                'block mb-2 transition-colors duration-200',
                isDark ? 'text-slate-300' : 'text-slate-700'
              ]"
            >
              Content
            </label>
            <textarea 
              v-model="newPost.content" 
              rows="4" 
              placeholder="What's on your mind?"
              :class="[
                'w-full px-4 py-2 rounded-lg transition-all duration-200',
                isDark 
                  ? 'bg-slate-700/50 border border-slate-600 text-white placeholder-slate-400' 
                  : 'bg-slate-100 border border-slate-300 text-slate-900 placeholder-slate-400'
              ]"
            ></textarea>
          </div>

          <!-- Tags -->
          <div>
            <label 
              :class="[
                'block mb-2 transition-colors duration-200',
                isDark ? 'text-slate-300' : 'text-slate-700'
              ]"
            >
              Tags
            </label>
            
            <div class="flex flex-wrap gap-2 mb-3">
              <span 
                v-for="tag in newPost.tags" 
                :key="tag.name"
                class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium transition-all duration-200"
                :class="tag.color"
              >
                {{ tag.name }}
                <button 
                  @click="removeTag(tag)"
                  class="ml-2 focus:outline-none"
                  type="button"
                  aria-label="Remove tag"
                >
                  <X class="w-4 h-4 opacity-70" />
                </button>
              </span>
            </div>

            <div class="flex space-x-2">
              <input 
                v-model="tagInput" 
                @keyup.enter="addTag" 
                placeholder="Add tags (press Enter)"
                :class="[
                  'flex-1 px-4 py-2 rounded-lg transition-all duration-200',
                  isDark 
                    ? 'bg-slate-700/50 border border-slate-600 text-white placeholder-slate-400' 
                    : 'bg-slate-100 border border-slate-300 text-slate-900 placeholder-slate-400'
                ]"
              />
              <button 
                @click="addTag"
                type="button"
                :class="[
                  'px-4 py-2 rounded-lg transition-all duration-200',
                  isDark 
                    ? 'bg-emerald-500 hover:bg-emerald-600 text-white' 
                    : 'bg-emerald-500 hover:bg-emerald-600 text-white'
                ]"
              >
                Add
              </button>
            </div>
          </div>

          <!-- Media Type -->
          <div>
            <label 
              :class="[
                'block mb-2 transition-colors duration-200',
                isDark ? 'text-slate-300' : 'text-slate-700'
              ]"
            >
              Media Type
            </label>
            <select 
              v-model="mediaType"
              :class="[
                'w-full px-4 py-2 rounded-lg transition-all duration-200',
                isDark 
                  ? 'bg-slate-700/50 border border-slate-600 text-white' 
                  : 'bg-slate-100 border border-slate-300 text-slate-900'
              ]"
            >
              <option value="">No Media</option>
              <option value="video">Video</option>
            </select>
          </div>

          <!-- Video URL -->
          <div v-if="mediaType === 'video'" class="space-y-2">
            <label 
              :class="[
                'block mb-2 transition-colors duration-200',
                isDark ? 'text-slate-300' : 'text-slate-700'
              ]"
            >
              Video URL
            </label>
            <input 
              v-model="newPost.videoUrl.url" 
              type="text" 
              placeholder="https://example.com/video.mp4 "
              :class="[
                'w-full px-4 py-2 rounded-lg transition-all duration-200',
                isDark 
                  ? 'bg-slate-700/50 border border-slate-600 text-white placeholder-slate-400' 
                  : 'bg-slate-100 border border-slate-300 text-slate-900 placeholder-slate-400'
              ]"
            />
          </div>

          <!-- File Attachment -->
          <div class="mt-4">
            <label 
              :class="[
                'block mb-2 transition-colors duration-200',
                isDark ? 'text-slate-300' : 'text-slate-700'
              ]"
            >
              Attach a File (Image, PDF, etc.)
            </label>
            <div class="flex items-center space-x-2">
              <!-- Icon Button for File Picker -->
              <label 
                :class="[
                  'flex items-center px-4 py-2 rounded-lg cursor-pointer transition-all duration-200',
                  isDark 
                    ? 'bg-slate-700/50 hover:bg-slate-700 text-white' 
                    : 'bg-slate-100 hover:bg-slate-200 text-slate-900'
                ]"
              >
                <Paperclip 
                  :class="isDark ? 'text-slate-400' : 'text-slate-500'" 
                  class="w-5 h-5 mr-2" 
                />
                Choose File
                <!-- Hidden file input -->
                <input 
                  type="file" 
                  class="hidden" 
                  accept="image/*,.pdf,.docx,.txt" 
                  @change="handleFileUpload"
                />
              </label>

              <!-- File Name Preview -->
              <span 
                v-if="uploadedFilePreview" 
                :class="[
                  'text-sm truncate max-w-xs',
                  isDark ? 'text-slate-400' : 'text-slate-600'
                ]"
              >
                {{ uploadedFilePreview }}
              </span>
            </div>

            <!-- Image Preview if an image was uploaded -->
            <div v-if="uploadedImagePreview" class="mt-2">
              <img 
                :src="uploadedImagePreview" 
                alt="Image preview" 
                class="max-h-64 w-auto rounded-lg object-cover"
              />
            </div>
          </div>
        </div>

        <!-- Actions -->
        <div class="mt-6 flex justify-end space-x-3">
          <button 
            @click="closeModal" 
            type="button"
            :class="[
              'px-4 py-2 transition-colors rounded-lg',
              isDark 
                ? 'text-slate-300 hover:text-slate-100 hover:bg-slate-700/50' 
                : 'text-slate-600 hover:text-slate-900 hover:bg-slate-100'
            ]"
          >
            Cancel
          </button>
          <button 
            @click="submitPost" 
            type="button"
            :disabled="!newPost.title.trim() || !newPost.content.trim()"
            :class="[
              'px-6 py-2 rounded-lg transition-all duration-200',
              isDark 
                ? 'bg-emerald-500 hover:bg-emerald-600 text-white disabled:opacity-50' 
                : 'bg-emerald-500 hover:bg-emerald-600 text-white disabled:opacity-50'
            ]"
          >
            Publish
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, defineProps, defineEmits } from 'vue';
import { X, Paperclip } from 'lucide-vue-next';
import { useThemeStore } from '../stores/theme';

// Theme store
const themeStore = useThemeStore();
const isDark = computed(() => themeStore.isDark);

// Props
const props = defineProps<{
  isOpen: boolean;
}>()

// Emits
const emit = defineEmits<{
  (e: 'close'): void;
  (e: 'submit', post: any): void;
}>()

// Local state
const showCreateModal = ref(false);
const tagInput = ref('');
const mediaType = ref<'image' | 'video' | ''>('');
const newPost = ref({
  id: '',
  title: '',
  content: '',
  imageUrl: null as string | null,
  videoUrl: {
    url: '',
    thumbnail: ''
  },
  author: {
    id: 3,
    name: 'roua ben hassine',
    initials: 'RB',
    avatarUrl: new URL('@/assets/mikasa.png', import.meta.url).href
  },
  createdAt: '',
  comments: 0,
  shares: 0,
  isFavorite: false,
  reactions: [],
  tags: [] as Array<{name: string; color: string}>
})

const uploadedImagePreview = ref<string | null>(null);
const uploadedImagePreviewName = ref<string | null>(null);
const uploadedFilePreview = ref<string | null>(null);


// Tag color options
const tagColors = [
  "bg-purple-500/10 text-purple-500",
  "bg-blue-500/10 text-blue-500",
  "bg-emerald-500/10 text-emerald-500",
  "bg-amber-500/10 text-amber-500",
  "bg-green-500/10 text-green-500",
  "bg-pink-500/10 text-pink-500"
];

// Methods
const resetForm = () => {
  newPost.value = {
    id: '',
    title: '',
    content: '',
    imageUrl: null,
    videoUrl: { url: '', thumbnail: '' },
    author: {
      id: 3,
      name: 'roua ben hassine',
      initials: 'RB',
      avatarUrl: new URL('@/assets/mikasa.png', import.meta.url).href
    },
    createdAt: '',
    comments: 0,
    shares: 0,
    isFavorite: false,
    reactions: [],
    tags: []
  };
  mediaType.value = '';
  tagInput.value = '';
  uploadedImagePreview.value = null;
  uploadedImagePreviewName.value = null;
  uploadedFilePreview.value = null;
};

watch(
  () => props.isOpen,
  (newVal) => {
    showCreateModal.value = newVal;
    if (!newVal) {
      resetForm(); // Now safe to call
    }
  }
)

const addTag = () => {
  const tagName = tagInput.value.trim();
  if (tagName && !newPost.value.tags.some(t => t.name === tagName)) {
    const randomColor = tagColors[Math.floor(Math.random() * tagColors.length)];
    
    newPost.value.tags.push({
      name: tagName,
      color: randomColor
    });
    tagInput.value = '';
  }
};

const removeTag = (tagToRemove: {name: string; color: string}) => {
  newPost.value.tags = newPost.value.tags.filter(tag => tag !== tagToRemove);
};

const handleFileUpload = (event: Event) => {
  const target = event.target as HTMLInputElement;
  if (!target.files || !target.files[0]) return;

  const file = target.files[0];
  uploadedFilePreview.value = file.name;

  // If it's an image, show preview
  if (file.type.startsWith('image/')) {
    const reader = new FileReader();
    reader.onload = (e) => {
      uploadedImagePreview.value = e.target?.result as string;
      newPost.value.imageUrl = e.target?.result as string;
    };
    reader.readAsDataURL(file);
  } else {
    uploadedImagePreview.value = null;
    newPost.value.imageUrl = null;
  }

  console.log('Attached file:', file); // For future backend integration
};

const submitPost = () => {
  if (!newPost.value.title.trim() || !newPost.value.content.trim()) {
    alert('Please fill in both title and content');
    return;
  }

  // Set creation date and ID
  newPost.value.createdAt = new Date().toISOString();
  newPost.value.id = (Date.now()).toString();
  
  // Emit submit event
  emit('submit', { ...newPost.value });
  
  // Close modal
  closeModal();
};

const closeModal = () => {
  emit('close');
  resetForm();
};

// Auto-focus title input when modal opens
const titleInput = ref<HTMLInputElement | null>(null);
watch(showCreateModal, (isOpen) => {
  if (isOpen && titleInput.value) {
    setTimeout(() => {
      titleInput.value?.focus();
    }, 100);
  }
});
</script>