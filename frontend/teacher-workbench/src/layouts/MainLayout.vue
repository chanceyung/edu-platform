<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

// 侧边栏菜单（AI-first 演进，当前先传统管理布局）
const menus = [
  { index: '/org/schools', title: '学校管理' },
  { index: '/agent', title: '🤖 Agent工作台' },
  { index: '/qbank/knowledge-tree', title: '知识点树' },
  { index: '/qbank/questions', title: '题库管理' },
  { index: '/exam/papers', title: '组卷管理' },
  { index: '/exam/exams', title: '考试管理' },
  { index: '/exam/take', title: '学生答卷' },
  { index: '/homework', title: '作业+AI批改' },
  { index: '/analytics', title: '学情分析' },
  { index: '/ai/lesson', title: 'AI备课' },
  { index: '/ai/comment', title: 'AI评语' },
  { index: '/hs/notices', title: '家校通知' }
]

function go(index: string) {
  router.push(index)
}
</script>

<template>
  <el-container class="layout">
    <el-aside width="200px" class="aside">
      <div class="logo">智慧教学平台</div>
      <el-menu :default-active="route.path" @select="go">
        <el-menu-item v-for="m in menus" :key="m.index" :index="m.index">{{ m.title }}</el-menu-item>
      </el-menu>
    </el-aside>
    <el-main class="main">
      <router-view />
    </el-main>
  </el-container>
</template>

<style scoped>
.layout { height: 100vh; }
.aside { background: #001529; }
.logo { color: #fff; text-align: center; padding: 16px 0; font-size: 16px; font-weight: 600; }
.aside :deep(.el-menu) { border-right: none; background: #001529; }
.aside :deep(.el-menu-item) { color: #ccc; }
.aside :deep(.el-menu-item.is-active) { background: #1890ff; color: #fff; }
.main { background: #f0f2f5; padding: 16px; }
</style>
