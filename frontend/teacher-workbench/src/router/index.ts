import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  { path: '/', redirect: '/org/schools' },
  {
    path: '/org/schools',
    name: 'SchoolManage',
    component: () => import('../views/school/SchoolManage.vue'),
    meta: { title: '学校管理' }
  },
  {
    path: '/qbank/questions',
    name: 'QuestionManage',
    component: () => import('../views/question/QuestionManage.vue'),
    meta: { title: '题库管理' }
  },
  {
    path: '/exam/papers',
    name: 'PaperManage',
    component: () => import('../views/exam/PaperManage.vue'),
    meta: { title: '组卷管理' }
  },
  {
    path: '/exam/exams',
    name: 'ExamManage',
    component: () => import('../views/exam/ExamManage.vue'),
    meta: { title: '考试管理' }
  },
  {
    path: '/ai/lesson',
    name: 'AiLesson',
    component: () => import('../views/ai/AiLesson.vue'),
    meta: { title: 'AI备课' }
  },
  {
    path: '/ai/comment',
    name: 'AiComment',
    component: () => import('../views/ai/AiComment.vue'),
    meta: { title: 'AI评语' }
  },
  {
    path: '/hs/notices',
    name: 'NoticeManage',
    component: () => import('../views/homeschool/NoticeManage.vue'),
    meta: { title: '家校通知' }
  },
  {
    path: '/analytics',
    name: 'Analytics',
    component: () => import('../views/analytics/AnalyticsView.vue'),
    meta: { title: '学情分析' }
  },
  {
    path: '/homework',
    name: 'HomeworkManage',
    component: () => import('../views/homework/HomeworkManage.vue'),
    meta: { title: '作业管理' }
  },
  {
    path: '/exam/take',
    name: 'ExamTake',
    component: () => import('../views/exam/ExamTake.vue'),
    meta: { title: '学生答卷' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
