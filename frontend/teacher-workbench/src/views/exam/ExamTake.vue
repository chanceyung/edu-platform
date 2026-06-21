<script setup lang="ts">
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { listExams, getPaperDetail, submitRecord, getRecord } from '../../api/exam'
import { QUESTION_TYPE } from '../../types/question'
import type { ExamVO, PaperDetailVO, ExamRecordVO } from '../../types/exam'

const exams = ref<ExamVO[]>([])
const examId = ref<number>()
const studentId = ref<number>()
const paper = ref<PaperDetailVO | null>(null)
// questionId → 选中的 optionId 列表
const answers = reactive<Record<number, number[]>>({})
const result = ref<ExamRecordVO | null>(null)
const submitting = ref(false)

async function loadExams() { exams.value = await listExams() }
loadExams()

async function startExam() {
  if (!examId.value) return
  const exam = exams.value.find(e => e.id === examId.value)
  if (!exam) return
  paper.value = await getPaperDetail(exam.paperId)
  result.value = null
  Object.keys(answers).forEach(k => delete answers[Number(k)])
  // 初始化答案数组
  paper.value.questions.forEach(q => { answers[q.questionId] = [] })
}

async function submit() {
  if (!examId.value || !studentId.value) { ElMessage.warning('请选考试并填学生ID'); return }
  const items = Object.entries(answers).map(([qid, ids]) => ({ questionId: Number(qid), answer: JSON.stringify(ids) }))
  submitting.value = true
  try {
    await submitRecord({ examId: examId.value, studentId: studentId.value, answers: items })
    ElMessage.success('提交成功，已自动判分')
    result.value = await getRecord(examId.value, studentId.value)
  } catch (e) { ElMessage.error((e as Error).message) } finally { submitting.value = false }
}
</script>

<template>
  <el-card>
    <h2>学生答卷（P1 闭环验证）</h2>

    <!-- 选择考试 + 学生 -->
    <div class="bar">
      <el-select v-model="examId" placeholder="选考试" style="width: 280px" @change="startExam">
        <el-option v-for="e in exams" :key="e.id" :label="e.name" :value="e.id" />
      </el-select>
      <el-input-number v-model="studentId" :controls="false" placeholder="学生ID" style="width: 200px; margin: 0 12px" />
      <el-button type="primary" :loading="submitting" :disabled="!paper" @click="submit">提交答卷</el-button>
    </div>

    <!-- 成绩 -->
    <el-alert v-if="result" :title="`判分完成：得分 ${(result.systemScore / 1000).toFixed(1)} / ${(result.paperScore / 1000).toFixed(1)}（${result.status === 2 ? '已完成' : '含主观题待判'}）`"
      :type="result.systemScore >= result.paperScore * 0.6 ? 'success' : 'warning'" show-icon style="margin: 12px 0" />

    <!-- 题目 -->
    <div v-if="paper">
      <div v-for="(q, i) in paper.questions" :key="q.questionId" class="question">
        <div class="stem">{{ i + 1 }}. [{{ QUESTION_TYPE[q.type] }}] {{ q.stem }}（{{ (q.score / 1000).toFixed(1) }}分）</div>
        <el-checkbox-group v-model="answers[q.questionId]">
          <div v-for="opt in q.options" :key="opt.id" class="option">
            <el-checkbox :value="opt.id" :label="opt.content" />
          </div>
        </el-checkbox-group>
      </div>
    </div>
    <el-empty v-else description="请选择考试开始作答" />
  </el-card>
</template>

<style scoped>
.bar { display: flex; align-items: center; margin: 12px 0; }
.question { margin-bottom: 20px; padding: 12px; background: #fafafa; border-radius: 4px; }
.stem { font-weight: 600; margin-bottom: 8px; }
.option { margin: 4px 0; }
</style>
