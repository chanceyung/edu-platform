<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { listExams, createExam, listPapers } from '../../api/exam'
import type { ExamVO, PaperVO } from '../../types/exam'

const exams = ref<ExamVO[]>([])
const papers = ref<PaperVO[]>([])
const loading = ref(false)
const dialogVisible = ref(false)

const form = reactive<{ name: string; paperId: number | undefined; classId: number | undefined; duration: number }>({
  name: '', paperId: undefined, classId: undefined, duration: 45
})

const statusMap: Record<number, string> = { 1: '未发布', 2: '进行中', 3: '已结束' }

async function load() {
  loading.value = true
  try { [exams.value, papers.value] = await Promise.all([listExams(), listPapers()]) }
  catch { ElMessage.error('加载失败') } finally { loading.value = false }
}

function openCreate() { form.name = ''; form.paperId = undefined; form.classId = undefined; form.duration = 45; dialogVisible.value = true }
async function submit() {
  if (!form.name || !form.paperId || !form.classId) { ElMessage.warning('名称/试卷/班级必填'); return }
  try {
    await createExam({ name: form.name, paperId: form.paperId, classId: form.classId, duration: form.duration })
    ElMessage.success('发布成功'); dialogVisible.value = false; await load()
  } catch (e) { ElMessage.error((e as Error).message) }
}

onMounted(load)
</script>

<template>
  <el-card>
    <div class="header"><h2>考试管理</h2><el-button type="primary" @click="openCreate">发布考试</el-button></div>
    <el-table :data="exams" v-loading="loading" border>
      <el-table-column prop="name" label="考试名称" />
      <el-table-column label="试卷">
        <template #default="{ row }">{{ papers.find(p => p.id === row.paperId)?.name || row.paperId }}</template>
      </el-table-column>
      <el-table-column prop="classId" label="班级ID" width="160" />
      <el-table-column prop="duration" label="时长(分)" width="100" />
      <el-table-column label="状态" width="100"><template #default="{ row }">{{ statusMap[row.status] }}</template></el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="发布考试" width="480px">
      <el-form label-width="80px">
        <el-form-item label="名称" required><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="试卷" required>
          <el-select v-model="form.paperId" placeholder="选试卷" style="width: 100%">
            <el-option v-for="p in papers" :key="p.id" :label="`${p.name}（${(p.totalScore / 1000).toFixed(0)}分/${p.questionCount}题）`" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="班级ID" required><el-input-number v-model="form.classId" :controls="false" style="width: 100%" /></el-form-item>
        <el-form-item label="时长(分)"><el-input-number v-model="form.duration" :min="1" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible = false">取消</el-button><el-button type="primary" @click="submit">发布</el-button></template>
    </el-dialog>
  </el-card>
</template>

<style scoped>
.header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 12px; }
</style>
