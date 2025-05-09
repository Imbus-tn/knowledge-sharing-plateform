import { createRouter, createWebHistory } from 'vue-router';
import LandingPage from '../views/LandingPage.vue';
import LoginPage from '../views/auth/LoginPage.vue';
import ForgotPasswordPage from '../views/auth/ForgotPasswordPage.vue';
import VerifyOtpPage from '../views/auth/VerifyOtpPage.vue';
import ResetPasswordPage from '../views/auth/ResetPasswordPage.vue';
import RegisterPage from '../views/auth/RegisterPage.vue';
import FeedPage from '../views/FeedPage.vue';
import ProfilePage from '../views/ProfilePage.vue';
import EditProfilePage from '../views/EditProfilePage.vue';
import ManageUsersPage from '../views/ManageUsersPage.vue';
import UpdateUserRolePage from '../views/UpdateUserRolePage.vue';
import DashboardPage from '../views/DashboardPage.vue';
import DiscussionsPage from '../views/DiscussionsPage.vue';
import FavoritesPage from '../views/FavoritesPage.vue';
import InviteUserPage from '../views/InviteUserPage.vue';
import { useAuthStore } from '../stores/auth';

const router = createRouter({
  history: createWebHistory(),
  routes: [
      {
        path: '/',
        name: 'landing',
        component: LandingPage,
        meta: { requiresGuest: true }
      },
      {
        path: '/login',
        name: 'login',
        component: LoginPage,
        meta: { requiresGuest: true }
      },
      {
        path: '/forgot-password',
        name: 'forgot-password',
        component: ForgotPasswordPage
      },
      {
        path: '/verify-otp',
        name: 'verify-otp',
        component: VerifyOtpPage
      },
      {
        path: '/reset-password',
        name: 'reset-password',
        component: ResetPasswordPage
      },
      {
        path: '/register',
        name: 'register',
        component: RegisterPage,
      },
      {
        path: '/feed',
        name: 'feed',
        component: FeedPage,
        meta: { requiresAuth: true }
      },
      {
        path: '/profile',
        name: 'profile',
        component: ProfilePage,
        meta: { requiresAuth: true }
      },
      {
        path: '/edit-profile',
        name: 'edit-profile',
        component: EditProfilePage,
        meta: { requiresAuth: true }
      },
      {
        path: '/manage-users',
        name: 'manage-users',
        component: ManageUsersPage,
        meta: { requiresAuth: true }
      },
      {
        path: '/users/:userId/update-role',
        name: 'update-user-role',
        component: UpdateUserRolePage,
        props: true,
        meta: { requiresAuth: true }
      },
      {
        path: '/dashboard',
        name: 'dashboard',
        component: DashboardPage,
        meta: { requiresAuth: true }
      },
      {
        path: '/discussions',
        name: 'discussions',
        component: DiscussionsPage
      },
      {
        path: '/favorites',
        name: 'favorites',
        component: FavoritesPage
      },
      {
        path: '/invite-user',
        name: 'invite-user',
        component: InviteUserPage,
        meta: { requiresAuth: true }
      }
  ]
});

router.beforeEach(async (to) => {
  const authStore = useAuthStore();
  const isAuthenticated = await authStore.checkAuth();

  if (to.meta.requiresAuth && !isAuthenticated) {
    return '/login';
  }

  if (to.meta.requiresGuest && isAuthenticated) {
    return '/feed';
  }
})

export default router;