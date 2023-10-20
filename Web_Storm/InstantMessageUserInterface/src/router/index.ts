import {createRouter, createWebHistory, RouteRecordRaw} from 'vue-router'
import login from '../views/login.vue'
// import home from '../views/home.vue'

const routes: Array<RouteRecordRaw> = [
    {
        path: '/',
        component: login
    }
    /*{
        path: '/',
        component: home
    }*/,
   {
       path: '/home',
       name: 'home',
       component: () => import(/* webpackChunkName: "home" */ '../views/home.vue')
   }
]

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes
})

export default router