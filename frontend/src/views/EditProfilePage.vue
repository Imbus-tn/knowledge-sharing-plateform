<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
    <div class="bg-slate-800/50 rounded-2xl p-6 border border-slate-700 space-y-8">
      <!-- Avatar Section -->
      <div class="flex items-center space-x-6">
        <div class="relative">
          <img 
            v-if="avatarPreview" 
            :src="avatarPreview" 
            class="w-32 h-32 rounded-2xl object-cover border-4 border-slate-800 shadow-xl"
          >
          <img 
            v-else-if="avatarUrl" 
            :src="avatarUrl" 
            class="w-32 h-32 rounded-2xl object-cover border-4 border-slate-800 shadow-xl"
          >
          <div 
            v-else 
            class="w-32 h-32 rounded-2xl bg-slate-700 border-4 border-slate-800 flex items-center justify-center shadow-xl"
          >
            <span class="text-4xl font-bold">{{ userInitials }}</span>
          </div>
          <input 
            type="file" 
            accept="image/*" 
            @change="handleAvatarUpload" 
            class="absolute inset-0 opacity-0 cursor-pointer"
          >
          <div class="absolute -bottom-2 -right-2 w-8 h-8 bg-emerald-500 rounded-lg flex items-center justify-center border-4 border-slate-800">
            <Camera class="w-4 h-4 text-white" />
          </div>
        </div>
        <button 
          @click="triggerAvatarUpload"
          class="px-4 py-2 bg-emerald-500 text-white rounded-xl hover:bg-emerald-600 transition-colors"
        >
          Upload Avatar
        </button>
      </div>
      <!-- Modern Form -->
      <form @submit.prevent="handleSubmit" class="space-y-8">
        <!-- Full Name -->
        <div class="relative">
          <label for="fullName" class="block mb-2 text-slate-400">Full Name :</label>
          <div class="relative">
            <User class="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-slate-400 pointer-events-none" />
            <input
              id="fullName"
              v-model="formData.name"
              type="text"
              class="w-full pl-10 pr-4 py-3 bg-slate-800/50 border border-slate-700 rounded-lg text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-emerald-500"
              placeholder="Enter your full name (e.g., John Doe)"
            />
          </div>
          <div v-if="fullNameErrors.length" class="mt-2 text-slate-400 text-sm">
            {{ fullNameErrors.join(', ') }}
          </div>
        </div>
        <!-- Bio -->
        <div class="relative">
          <label for="bio" class="block mb-2 text-slate-400">Bio :</label>
          <div class="relative">
            <BookOpen class="absolute left-3 top-3 w-5 h-5 text-slate-400 pointer-events-none" />
            <textarea
              id="bio"
              v-model="formData.bio"
              rows="4"
              class="w-full pl-10 pr-4 py-3 bg-slate-700/50 border border-slate-600 rounded-xl text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-emerald-500"
              placeholder="Tell us about yourself"
            ></textarea>
          </div>
        </div>
        <!-- Social Links -->
        <div class="space-y-6">
          <div class="relative">
            <label for="github" class="block mb-2 text-slate-400">GitHub Username :</label>
            <div class="relative">
              <Github class="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-slate-400 pointer-events-none" />
              <input
                id="github"
                v-model="formData.github"
                type="text"
                class="w-full pl-10 pr-4 py-3 bg-slate-700/50 border border-slate-600 rounded-xl text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-emerald-500"
                placeholder="GitHub username"
              />
            </div>
          </div>
          <div class="relative">
            <label for="linkedin" class="block mb-2 text-slate-400">LinkedIn Profile URL :</label>
            <div class="relative">
              <Linkedin class="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-slate-400 pointer-events-none" />
              <input
                id="linkedin"
                v-model="formData.linkedin"
                type="text"
                class="w-full pl-10 pr-4 py-3 bg-slate-700/50 border border-slate-600 rounded-xl text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-emerald-500"
                placeholder="LinkedIn profile URL"
              />
            </div>
          </div>
        </div>
        <!-- Location and Phone Number Section -->
        <div class="space-y-6">
          <div class="relative">
            <label for="location" class="block mb-2 text-slate-400">Location :</label>
            <input
              id="location"
              v-model="locationSearchQuery"
              type="text"
              placeholder="Search for your location"
              class="w-full pl-4 pr-10 py-3 bg-slate-700/50 border border-slate-600 rounded-xl text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-emerald-500"
              @input="filterLocations"
            />
            <!-- Dropdown List -->
            <div
              v-if="filteredLocations.length > 0"
              class="absolute z-10 w-full mt-2 bg-slate-800 border border-slate-700 rounded-xl shadow-lg max-h-48 overflow-y-auto"
            >
              <div
                v-for="country in filteredLocations"
                :key="country.code"
                class="px-4 py-2 cursor-pointer hover:bg-slate-700/50 text-white" 
                @click="selectLocation(country)"
              >
                {{ country.emoji }} {{ country.name }}
              </div>
            </div>
          </div>

          <!-- Phone Number Section -->
          <div class="flex items-center space-x-2">
            <!-- Label for Phone Number -->
            <label for="phoneCode" class="block mb-2 text-slate-400">Phone Number :</label>

            <!-- Phone Code Select and Input -->
            <div class="flex items-center space-x-2">
              <!-- Phone Code Select -->
              <select
                id="phoneCode"
                v-model="selectedPhoneCountry"
                class="w-36 px-3 py-3 bg-slate-700/50 border border-slate-600 rounded-xl text-white focus:outline-none focus:ring-2 focus:ring-emerald-500"
              >
                <option
                  v-for="country in countries"
                  :key="country.code"
                  :value="country"
                >
                  {{ country.emoji }} {{ country.phone }}
                </option>
              </select>

              <!-- Phone Input -->
              <input
                v-model="formData.phoneNumber"
                type="tel"
                :placeholder="phonePlaceholder"
                class="flex-1 px-4 py-3 bg-slate-700/50 border border-slate-600 rounded-xl text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-emerald-500"
              />
            </div>
            <div v-if="phoneErrors.length" class="mt-2 text-slate-400 text-sm">
              {{ phoneErrors.join(', ') }}
            </div>
          </div>
        </div>
        <!-- Submit Button -->
        <button
          type="submit"
          :disabled="isSubmitting"
          class="w-full flex items-center justify-center px-6 py-3 border border-transparent rounded-xl shadow-sm text-sm font-medium text-white bg-emerald-500 hover:bg-emerald-600 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-emerald-500 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
        >
          <Save class="w-5 h-5 mr-2" />
          Save Changes
        </button>
      </form>
    </div>
  </div>
</template>
  
<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../stores/auth';
import { Camera, Save, User, BookOpen, Github, Linkedin } from 'lucide-vue-next';
import { countries as countriesData } from 'countries-list'; 
import { isValidPhoneNumber, parsePhoneNumber, formatNumber, type CountryCode } from 'libphonenumber-js';

const router = useRouter();
const authStore = useAuthStore();
const user = computed(() => authStore.user);

interface Country {
  name: string;
  code: string; 
  phone: string; 
  emoji: string;
}

// Utility function to generate emoji flags
const getFlagEmoji = (countryCode: string): string => {
  const codePoints = countryCode
    .toUpperCase()
    .split('')
    .map((char) => 127397 + char.charCodeAt(0));
  return String.fromCodePoint(...codePoints);
};

// Country data with flags and example formats
const countries = computed(() => {
  return Object.entries(countriesData).map(([iso2, country]: [string, any]) => ({
    name: country.name || 'Unknown',
    code: iso2 || 'XX', 
    phone: `+${Array.isArray(country.phone) && country.phone.length > 0 
      ? country.phone[0] 
      : '+216'}`,
    emoji: getFlagEmoji(iso2 || 'XX'), 
  })) as Country[];
});

// Selected values for location and phone country
const selectedLocation = ref<Country | null>(null);
const selectedPhoneCountry = ref<Country | null>(null);


// Form Data
const formData = reactive({
  name: user.value?.name || '',
  bio: user.value?.bio || '',
  github: user.value?.github || '',
  linkedin: user.value?.linkedin || '',
  phoneNumber: '',
  avatar: null as File | null,
});

// Parse existing phone number
onMounted(() => {
  if (user.value?.location) {
    selectedLocation.value = countries.value.find(
      (c) => c.name === user.value?.location
    ) || null;
  } else {
    selectedLocation.value = countries.value.find(
      (c) => c.code === 'TN'
    ) || null;
  }

  if (user.value?.phoneNumber) {
    try {
      const parsed = parsePhoneNumber(user.value.phoneNumber);
      selectedPhoneCountry.value =
        countries.value.find((c) => c.phone === `+${parsed.countryCallingCode}`) || null;
      formData.phoneNumber = parsed.nationalNumber; // Extract the national number
    } catch (error) {
      // Fallback to Tunisia if parsing fails
      selectedPhoneCountry.value = countries.value.find(
        (c) => c.phone === '+216'
      ) || null;
      formData.phoneNumber = user.value.phoneNumber;
    }
  } else {
    selectedPhoneCountry.value = countries.value.find(
      (c) => c.phone === '+216'
    ) || null;
  }
});

// Phone placeholder based on country
const phonePlaceholder = computed(() => {
  const country = selectedPhoneCountry.value;
  return country ? `e.g., ${country.phone} 123 456 789` : 'Enter phone number';
});

// Error states
const fullNameErrors = ref<string[]>([]);
const phoneErrors = ref<string[]>([]);

// Full Name validation
const validateFullName = (fullName: string): string[] => {
  const errors: string[] = [];
  const trimmedName = fullName.trim();
  const parts = trimmedName.split(/\s+/).filter(p => p.length > 0);

  // Length check
  if (trimmedName.length < 3 || trimmedName.length > 50) {
    errors.push('Must be 3-50 characters');
  }

  // Character validation
  if (!/^[a-zA-Z '-]+$/.test(trimmedName)) {
    errors.push('Only letters, spaces, hyphens, and apostrophes allowed');
  }

  // Name parts check
  if (parts.length < 2) {
    errors.push('Full name must include first and last name separated by a space');
  }

  // Minimum length per part
  if (parts.some(part => part.length < 2)) {
    errors.push('Each name part must be at least 2 characters');
  }

  return errors;
};


// Phone validation using libphonenumber-js
const validatePhone = (number: string) => {
  const country = selectedPhoneCountry.value;
  if (!country) {
    return ['Please select a country'];
  }

  const fullNumber = `${country.phone}${number}`;
  if (!isValidPhoneNumber(fullNumber, country.code as CountryCode)) {
    return [`Invalid ${country.name} phone number`];
  }
  return [];
};

// Avatar handling
const avatarPreview = ref('');
const avatarUrl = computed(() => {
  const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080'; // Default fallback
  return user.value?.avatarUrl
    ? `${apiUrl}${user.value.avatarUrl}`
    : '';
});

const handleAvatarUpload = (e: Event) => {
  const file = (e.target as HTMLInputElement).files?.[0];
  if (file) {
    formData.avatar = file;
    avatarPreview.value = URL.createObjectURL(file);
  }
};

const triggerAvatarUpload = () => {
  const fileInput = document.querySelector('input[type="file"]') as HTMLInputElement;
  fileInput.click();
};

// User initials
const userInitials = computed(() => {
  return user.value?.name
    ? user.value.name.split(' ').map(n => n[0]).join('').toUpperCase()
    : '';
});

// Location search functionality
const locationSearchQuery = ref('');
const filteredLocations = ref<Country[]>([]);

const filterLocations = () => {
  if (!locationSearchQuery.value) {
    filteredLocations.value = [];
    return;
  }
  filteredLocations.value = countries.value.filter((country) =>
    country.name.toLowerCase().includes(locationSearchQuery.value.toLowerCase())
  );
};

const selectLocation = (country: Country) => {
  selectedLocation.value = country;
  locationSearchQuery.value = `${country.emoji} ${country.name}`;
  filteredLocations.value = [];
};

// Form submission
const isSubmitting = ref(false);
const handleSubmit = async () => {
  try {
    fullNameErrors.value = [];
    phoneErrors.value = [];
    isSubmitting.value = true;

    // Validate username
    fullNameErrors.value = validateFullName(formData.name);
    if (fullNameErrors.value.length > 0) {
      return;
    }

    // Validate phone
    phoneErrors.value = validatePhone(formData.phoneNumber);
    if (phoneErrors.value.length > 0) {
      return;
    }

    // Prepare data
    const fullPhoneNumber = `${selectedPhoneCountry.value?.phone}${formData.phoneNumber}`;
    const formattedPhoneNumber = formatNumber(
      fullPhoneNumber,
      selectedPhoneCountry.value?.code as CountryCode,
      'INTERNATIONAL'
    );


    const profileData = {
      name: formData.name,
      bio: formData.bio,
      location: selectedLocation.value?.name || '',
      phoneNumber: formattedPhoneNumber,
      github: formData.github,
      linkedin: formData.linkedin,
    };


    // Step 1: Update profile
    await authStore.updateProfile(profileData);

    // Step 2: Upload avatar if present
    if (formData.avatar) {
      await authStore.uploadAvatar(formData.avatar);
    }

    router.push({ name: 'profile' });
  } catch (error: any) {
    if (error.response?.data) {
      if (error.response.data.name) { 
        fullNameErrors.value = error.response.data.name;
      }
      if (error.response.data.phoneNumber) {
        phoneErrors.value = error.response.data.phoneNumber; 
      }
    }
  } finally {
    isSubmitting.value = false;
  }
};
</script>