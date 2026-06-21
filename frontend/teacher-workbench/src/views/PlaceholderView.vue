<script setup lang="ts">
import { useRoute } from 'vue-router'
const route = useRoute()
</script>

<template>
  <el-card>
    <h2>{{ route.meta.title }}</h2>
    <el-empty description="页面开发中（按规划顺序推进）" />
  </el-card>
</template>
