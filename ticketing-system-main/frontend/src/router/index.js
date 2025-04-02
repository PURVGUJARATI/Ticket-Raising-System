import { createRouter, createWebHistory } from 'vue-router'
import SignInView from '../views/SignInView.vue'
import { useSessionStore } from '../stores/session.js'
// import { createRouter, createWebHistory } from "vue-router";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/signin',
      name: 'signin',
      component: SignInView
    },
    {
      path: '/',
      redirect: () => '/projects'
    },
    {
      path: '/',
      name: 'main',
      component: () => import('../views/MainView.vue'),
      children: [
        {
          path: 'projects',
          name: 'projects',
          redirect: () => '/projects/overview',
          children: [
            {
              path: 'overview',
              name: 'overview',
              component: () => import('../views/ProjectsView.vue')
            },
            {
              path: ':id',
              name: 'projectDetails',
              component: () => import('../views/ProjectDetailView.vue')
            }
          ]
        },
        {
          path: 'tickets',
          name: 'tickets',
          component: () => import('../views/TicketsView.vue')
        },
        {
          path: 'analysis',
          name: 'analysis',
          component: () => import('../views/Analysis.vue')  
        }
      ]
    },
    {
      path: '/:pathMatch(.*)*',
      component: () => import('../views/NotFoundView.vue')
    }
  ]
});

router.beforeEach((to, from, next) => {
  const sessionStore = useSessionStore();

  if (to.name === 'signin' && sessionStore.isLoggedIn()) {
    next({ name: 'projects' });
  } else if (to.name !== 'signin' && !sessionStore.isLoggedIn()) {
    next({ name: 'signin' });
  } else {
    next();
  }
});

export default router;
