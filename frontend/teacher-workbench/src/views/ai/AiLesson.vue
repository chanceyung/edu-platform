<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { generateLesson } from '../../api/ai'

const topic = ref('')
const gradeLevel = ref(7)
const loading = ref(false)
const result = ref('')

async function doGenerate() {
  if (!topic.value) { ElMessage.warning('请输入课题'); return }
  loading.value = true; result.value = ''
  try { result.value = await generateLesson(topic.value, gradeLevel.value) }
  catch (e) { ElMessage.error((e as Error).message) } finally { loading.value = false }
}
</script>

<template>
  <el-card>
    <h2>AI 备课助手（智谱 GLM-5.2）</h2>
    <div class="bar">
      <el-input v-model="topic" placeholder="输入课题，如：勾股定理" style="width: 300px" />
      <el-input-number v-model="gradeLevel" :min="7" :max="9" />
      <el-button type="primary" :loading="loading" @click="doGenerate">生成教案</el-button>
    </div>
    <div v-loading="loading" class="result">
      <pre v-if="result">{{ result }}</pre>
      <el-empty v-else description="输入课题生成完整教案（目标/重点/过程/练习）" />
    </div>
  </el-card>
</template>

<style scoped>
.bar { display: flex; align-items: center; gap: 12px; margin: 12px 0; }
.result { margin-top: 16px; }
pre { white-space: pre-wrap; word-wrap: break-word; background: #f5f7fa; padding: 16px; border-radius: 8px; font-size: 14px; line-height: 1.8; }
</style>
