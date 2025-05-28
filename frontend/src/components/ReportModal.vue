<template>
  <div 
    v-if="isOpen"
    class="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center p-4 z-50"
    @click.self="$emit('close')"
  >
    <!-- Modal Content -->
    <div 
      :class="[
        'relative max-w-xl w-full rounded-2xl shadow-xl p-6',
        isDark ? 'bg-slate-800/90 border border-slate-700' : 'bg-white border border-slate-200'
      ]"
      @click.stop
    >
      <!-- Close Button -->
      <button 
        @click="$emit('close')"
        :class="[
          'absolute top-3 right-3 transition-colors',
          isDark ? 'text-slate-400 hover:text-slate-300' : 'text-slate-500 hover:text-slate-600'
        ]"
        aria-label="Close modal"
      >
        <X class="w-5 h-5" />
      </button>

      <!-- Header -->
      <div class="flex items-center mb-6">
        <Flag 
          :class="[
            'w-5 h-5 mr-2',
            isDark ? 'text-slate-400' : 'text-slate-500'
          ]"
        />
        <h2 
          :class="[
            'text-xl font-semibold',
            isDark ? 'text-white' : 'text-slate-900'
          ]"
        >
          Report {{ entityType === 'post' ? 'Post' : 'Comment' }}
        </h2>
      </div>

      <!-- Form Content -->
      <div class="space-y-4">
        <h3 
          :class="[
            'text-lg font-medium',
            isDark ? 'text-white' : 'text-slate-900'
          ]"
        >
          What's happening?
        </h3>
        <p class="text-sm text-slate-400 mb-4">Check all that apply.</p>

        <!-- Report Options -->
        <div class="space-y-3">
          <!-- Inappropriate Content -->
          <label class="flex items-start space-x-3 p-3 rounded-lg cursor-pointer transition-all duration-200"
            :class="{
              'bg-slate-700/50 hover:bg-slate-700/70': isDark,
              'bg-slate-100 border border-slate-300 hover:bg-slate-100/90': !isDark
            }"
          >
            <input 
              type="checkbox"
              v-model="reportData.inappropriate"
              class="mt-1 h-4 w-4 rounded focus:ring-emerald-500 text-emerald-500"
              :class="{
                'border-black/50': isDark,
                'border-slate-200': !isDark,
                'bg-slate-700/50': reportData.inappropriate && isDark,
                'bg-white': reportData.inappropriate && !isDark,
                'checked:bg-transparent': true,
                'checked:text-white': isDark,
                'checked:text-black/50': !isDark,
              }"
            />
            <div>
              <p 
                :class="[
                  'font-medium',
                  isDark ? 'text-white' : 'text-slate-900'
                ]"
              >
                Inappropriate Content
              </p>
              <p class="text-sm text-slate-400">Contains offensive, explicit, or harmful material.</p>
            </div>
          </label>

          <!-- Spam or Misleading -->
          <label class="flex items-start space-x-3 p-3 rounded-lg cursor-pointer transition-all duration-200"
            :class="{
              'bg-slate-700/50 hover:bg-slate-700/70': isDark,
              'bg-slate-100 border border-slate-300 hover:bg-slate-100/90': !isDark
            }"
          >
            <input 
              type="checkbox"
              v-model="reportData.spam"
              class="mt-1 h-4 w-4 rounded focus:ring-emerald-500 text-emerald-500"
              :class="{
                'border-black/50': isDark,
                'border-slate-200': !isDark,
                'bg-slate-700/50': reportData.spam && isDark,
                'bg-white': reportData.spam && !isDark,
                'checked:bg-transparent': true,
                'checked:text-white': isDark,
                'checked:text-black/50': !isDark,
              }"
            />
            <div>
              <p 
                :class="[
                  'font-medium',
                  isDark ? 'text-white' : 'text-slate-900'
                ]"
              >
                Spam or Misleading
              </p>
              <p class="text-sm text-slate-400">Contains spam, scams, or misleading information.</p>
            </div>
          </label>

          <!-- Copyright Violation -->
          <label class="flex items-start space-x-3 p-3 rounded-lg cursor-pointer transition-all duration-200"
            :class="{
              'bg-slate-700/50 hover:bg-slate-700/70': isDark,
              'bg-slate-100 border border-slate-300 hover:bg-slate-100/90': !isDark
            }"
          >
            <input 
              type="checkbox"
              v-model="reportData.copyright"
              class="mt-1 h-4 w-4 rounded focus:ring-emerald-500 text-emerald-500"
              :class="{
                'border-black/50': isDark,
                'border-slate-200': !isDark,
                'bg-slate-700/50': reportData.copyright && isDark,
                'bg-white': reportData.copyright && !isDark,
                'checked:bg-transparent': true,
                'checked:text-white': isDark,
                'checked:text-black/50': !isDark,
              }"
            />
            <div>
              <p 
                :class="[
                  'font-medium',
                  isDark ? 'text-white' : 'text-slate-900'
                ]"
              >
                Copyright Violation
              </p>
              <p class="text-sm text-slate-400">Infringes on intellectual property rights.</p>
            </div>
          </label>

          <!-- Harassment/Bullying -->
          <label class="flex items-start space-x-3 p-3 rounded-lg cursor-pointer transition-all duration-200"
            :class="{
              'bg-slate-700/50 hover:bg-slate-700/70': isDark,
              'bg-slate-100 border border-slate-300 hover:bg-slate-100/90': !isDark
            }"
          >
            <input 
              type="checkbox"
              v-model="reportData.harassment"
              class="mt-1 h-4 w-4 rounded focus:ring-emerald-500 text-emerald-500"
              :class="{
                'border-black/50': isDark,
                'border-slate-200': !isDark,
                'bg-slate-700/50': reportData.harassment && isDark,
                'bg-white': reportData.harassment && !isDark,
                'checked:bg-transparent': true,
                'checked:text-white': isDark,
                'checked:text-black/50': !isDark,
              }"
            />
            <div>
              <p 
                :class="[
                  'font-medium',
                  isDark ? 'text-white' : 'text-slate-900'
                ]"
              >
                Harassment or Bullying
              </p>
              <p class="text-sm text-slate-400">Contains harassment, threats, or personal attacks.</p>
            </div>
          </label>
        </div>

        <!-- Additional Details -->
        <div class="mt-6">
          <label class="block text-sm font-medium text-slate-400 mb-2">Additional Details</label>
          <textarea
            v-model="reportData.details"
            rows="3"
            placeholder="Please provide any additional context..."
            :class="[
              'w-full px-4 py-3 rounded-lg focus:outline-none focus:ring-2',
              isDark 
                ? 'bg-slate-700/50 border border-slate-600 text-white placeholder-slate-400 focus:ring-emerald-500' 
                : 'bg-slate-100 border border-slate-300 text-slate-900 placeholder-slate-400 focus:ring-emerald-600'
            ]"
          ></textarea>
        </div>

        <!-- Submit Button -->
        <div 
          class="flex justify-end space-x-4 pt-6 border-t"
          :class="[
            isDark ? 'border-slate-700' : 'border-slate-200',
            'transition-colors duration-300'
          ]"
        >
          <button
            @click="$emit('close')"
            :class="[
              'px-4 py-2 rounded-lg transition-colors duration-200',
              isDark ? 'text-slate-300 hover:text-slate-100 hover:bg-slate-700/50' : 'text-slate-600 hover:text-slate-900 hover:bg-slate-100'
            ]"
          >
            Cancel
          </button>
          <button
            @click="handleSubmit"
            :disabled="!isValid"
            :class="[
              'px-6 py-2 rounded-lg flex items-center transition-all duration-200',
              isValid 
                ? 'bg-red-600 hover:bg-red-700 text-white' 
                : 'bg-gray-500 cursor-not-allowed opacity-50',
              'disabled:opacity-50 disabled:cursor-not-allowed'
            ]"
          >
            <Flag class="w-4 h-4 mr-2" />
            Submit Report
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref, computed, defineProps, defineEmits } from 'vue';
import { X, Flag } from 'lucide-vue-next';
import { useThemeStore } from '../stores/theme';

// Theme store
const themeStore = useThemeStore();
const isDark = computed(() => themeStore.isDark);

// Props
defineProps<{
  isOpen: boolean;
  entityType: 'post' | 'comment'; // To show "Post" or "Comment" in title
}>()

// Emits
const emit = defineEmits<{
  (e: 'close'): void;
  (e: 'submit', data: ReportData): void;
}>()

// Types
interface ReportData {
  inappropriate: boolean;
  spam: boolean;
  copyright: boolean;
  harassment: boolean;
  details: string;
}

// Local state
const reportData = ref<ReportData>({
  inappropriate: false,
  spam: false,
  copyright: false,
  harassment: false,
  details: ''
});

// Validation
const isValid = computed(() => {
  return (
    reportData.value.inappropriate || 
    reportData.value.spam || 
    reportData.value.copyright || 
    reportData.value.harassment
  );
});

// Submit handler
const handleSubmit = () => {
  if (!isValid.value) return;
  emit('submit', { ...reportData.value });
  closeModal();
};

// Reset form
const closeModal = () => {
  reportData.value = {
    inappropriate: false,
    spam: false,
    copyright: false,
    harassment: false,
    details: ''
  };
  emit('close');
};
</script>