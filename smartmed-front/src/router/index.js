import { createRouter, createWebHistory } from 'vue-router'

// 引入页面组件
import Home from '../views/Home.vue'
import Chat from '../views/Chat.vue'
import Feedback from '../views/Feedback.vue'
import MedicineDetail from '../views/MedicineDetail.vue'
import MedicineList from '../views/MedicineList.vue'
import DiseaseDetail from '../views/DiseaseDetail.vue'
import DiseaseList from '../views/DiseaseList.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import UserProfile from '../views/UserProfile.vue'
import SearchResult from '../views/SearchResult.vue'

// 管理员端页面
const AdminDashboard = () => import('../views/admin/AdminDashboard.vue')
const AdminDiseases = () => import('../views/admin/diseases.vue')
const AdminMedicines = () => import('../views/admin/medicines.vue')
const AdminFeedbacks = () => import('../views/admin/feedbacks.vue')
const AdminUsers = () => import('../views/admin/users.vue')

// 路由表
const routes = [
  { path: '/', component: Home, name: 'Home' },
  { path: '/chat', component: Chat, name: 'Chat' },
  { path: '/feedback', component: Feedback, name: 'Feedback' },
  { path: '/medicines', component: MedicineList, name: 'MedicineList' },
  { path: '/medicines/:id', component: MedicineDetail, name: 'MedicineDetail' },
  { path: '/diseases', component: DiseaseList, name: 'DiseaseList' },
  { path: '/diseases/:id', component: DiseaseDetail, name: 'DiseaseDetail' },
  { path: '/login', component: Login, name: 'Login' },
  { path: '/register', component: Register, name: 'Register' },
  { path: '/profile', component: UserProfile, name: 'UserProfile' },
  { path: '/search', component: SearchResult, name: 'SearchResult' },
  // 管理员端
  { path: '/admin', redirect: '/admin/dashboard' },
  { path: '/admin/dashboard', component: AdminDashboard, name: 'AdminDashboard' },
  { path: '/admin/diseases', component: AdminDiseases, name: 'AdminDiseases' },
  { path: '/admin/medicines', component: AdminMedicines, name: 'AdminMedicines' },
  { path: '/admin/feedbacks', component: AdminFeedbacks, name: 'AdminFeedbacks' },
  { path: '/admin/users', component: AdminUsers, name: 'AdminUsers' },
  // 后台管理页面后续可加
]

// 创建路由实例
const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router