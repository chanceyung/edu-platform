<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { generateComment } from '../../api/ai'

const studentId = ref<number>()
const loading = ref(false)
const result = ref('')

async function doGenerate() {
  if (!studentId.value) { ElMessage.warning('请输入学生ID'); return }
  loading.value = true; result.value = ''
  try { result.value = await generateComment(studentId.value) }
  catch (e) { ElMessage.error((e as Error).message) } finally { loading.value = false }
}
</script>

<template>
  <el-card>
    <h2>AI 评语生成（智谱 GLM-5.2，基于学情数据）</h2>
    <div class="bar">
      <el-input-number v-model="studentId" :controls="false" placeholder="学生ID" style="width: 240px" />
      <el-button type="primary" :loading="loading" @click="doGenerate">生成评语</el-button>
    </div>
    <div v-loading="loading">
      <el-alert v-if="result" :title="result" type="success" show-icon :closable="false" style="margin-top: 16px; white-space: pre-wrap" />
      <el-empty v-else-if="!loading" description="输入学生ID，AI 基于考试+作业数据生成个性化评语" />
    </div>
  </el-card>
</template>

<style scoped>
.bar { display: flex; align-items: center; gap: 12px; margin: 12px 0; }
</style>
