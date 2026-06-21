<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { listPapers, createPaper, composePaper } from '../../api/exam'
import { listQuestions } from '../../api/question'
import { QUESTION_TYPE, type QuestionVO } from '../../types/question'
import type { PaperVO } from '../../types/exam'

const papers = ref<PaperVO[]>([])
const questions = ref<QuestionVO[]>([])
const loading = ref(false)

const createDialog = ref(false)
const form = reactive<{ name: string; gradeLevel: number; paperType: number }>({ name: '', gradeLevel: 7, paperType: 1 })

const composeDialog = ref(false)
const currentPaper = ref<PaperVO | null>(null)
// questionId → 分值(元)，存在即表示勾选
const selected = reactive<Record<number, number>>({})

async function loadPapers() {
  loading.value = true
  try { papers.value = await listPapers() } catch { ElMessage.error('加载试卷失败') } finally { loading.value = false }
}

function openCreate() { form.name = ''; form.gradeLevel = 7; form.paperType = 1; createDialog.value = true }
async function submitCreate() {
  if (!form.name) { ElMessage.warning('名称不能为空'); return }
  try {
    await createPaper({ ...form })
    ElMessage.success('创建成功'); createDialog.value = false; await loadPapers()
  } catch (e) { ElMessage.error((e as Error).message) }
}

async function openCompose(p: PaperVO) {
  currentPaper.value = p
  questions.value = await listQuestions()
  Object.keys(selected).forEach(k => delete selected[Number(k)])
  composeDialog.value = true
}
function toggleQuestion(qid: number, checked: boolean) {
  if (checked) { selected[qid] = selected[qid] || 5 }
  else { delete selected[qid] }
}
async function submitCompose() {
  if (!currentPaper.value) return
  const items = Object.entries(selected).map(([qid, yuan]) => ({ questionId: Number(qid), score: yuan * 1000 }))
  if (!items.length) { ElMessage.warning('请勾选题目并设分值'); return }
  try {
    await composePaper({ paperId: currentPaper.value.id, items })
    ElMessage.success('组卷成功'); composeDialog.value = false; await loadPapers()
  } catch (e) { ElMessage.error((e as Error).message) }
}

onMounted(loadPapers)
</script>

<template>
  <el-card>
    <div class="header"><h2>组卷管理</h2><el-button type="primary" @click="openCreate">新建试卷</el-button></div>
    <el-table :data="papers" v-loading="loading" border>
      <el-table-column prop="name" label="试卷名称" />
      <el-table-column label="总分" width="100"><template #default="{ row }">{{ (row.totalScore / 1000).toFixed(1) }}分</template></el-table-column>
      <el-table-column prop="questionCount" label="题数" width="80" />
      <el-table-column label="操作" width="120">
        <template #default="{ row }"><el-button size="small" type="primary" @click="openCompose(row)">组卷</el-button></template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="createDialog" title="新建试卷" width="420px">
      <el-form label-width="80px">
        <el-form-item label="名称" required><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="年级"><el-input-number v-model="form.gradeLevel" :min="7" :max="9" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="createDialog = false">取消</el-button><el-button type="primary" @click="submitCreate">确定</el-button></template>
    </el-dialog>

    <el-dialog v-model="composeDialog" :title="`组卷：${currentPaper?.name || ''}`" width="760px">
      <el-table :data="questions" border max-height="420">
        <el-table-column label="勾选" width="70">
          <template #default="{ row }">
            <el-checkbox :model-value="selected[row.id] !== undefined" @change="(v: boolean) => toggleQuestion(row.id, v)" />
          </template>
        </el-table-column>
        <el-table-column label="题型" width="80"><template #default="{ row }">{{ QUESTION_TYPE[row.type] }}</template></el-table-column>
        <el-table-column prop="stem" label="题干" show-overflow-tooltip />
        <el-table-column label="分值(分)" width="120">
          <template #default="{ row }">
            <el-input-number v-if="selected[row.id] !== undefined" v-model="selected[row.id]" :min="1" :max="100" size="small" />
            <span v-else>-</span>
          </template>
        </el-table-column>
      </el-table>
      <template #footer><el-button @click="composeDialog = false">取消</el-button><el-button type="primary" @click="submitCompose">提交组卷</el-button></template>
    </el-dialog>
  </el-card>
</template>

<style scoped>
.header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 12px; }
</style>
