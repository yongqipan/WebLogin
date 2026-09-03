import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import HomeView from '../views/HomeView.vue'
import BugListView from '../views/BugListView.vue'
import BugFormView from '../views/BugFormView.vue'
import BugDetailView from '../views/BugDetailView.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: (to) => ({ path: '/bugs', query: to.query }) },
    { path: '/login', name: 'login', component: LoginView },
    { path: '/home', name: 'home', component: HomeView, meta: { requiresAuth: true } },
    {
      path: '/bugs',
      name: 'bugs',
      component: BugListView,
      meta: { requiresAuth: true },
    },
    {
      path: '/bugs/new',
      name: 'bug-new',
      component: BugFormView,
      meta: { requiresAuth: true },
    },
    {
      path: '/bugs/:id',
      name: 'bug-detail',
      component: BugDetailView,
      meta: { requiresAuth: true },
    },
    {
      path: '/bugs/:id/edit',
      name: 'bug-edit',
      component: BugFormView,
      meta: { requiresAuth: true },
    },
    { path: '/:pathMatch(.*)*', redirect: '/bugs' },
  ],
})

router.beforeEach((to) => {
  if (to.path === '/casdoor') {
    window.location.href = '/api/auth/casdoor/login'
    return false
  }

  if (to.query.casdoor === 'success') {
    const username = (to.query.username as string) || 'casdoor-user'
    localStorage.setItem('login_user', username)
    return { path: to.path, query: {} }
  }
  if (to.query.casdoor === 'error') {
    alert('Casdoor 登录失败，请重试')
    return { path: to.path, query: {} }
  }

  const loggedIn = !!localStorage.getItem('login_user')
  if (to.meta.requiresAuth && !loggedIn) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }
  if (to.path === '/login' && loggedIn) {
    return { path: '/bugs' }
  }
  return true
})

export default router
