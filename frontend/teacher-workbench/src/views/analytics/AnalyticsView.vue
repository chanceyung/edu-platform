<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { analyzeExam, analyzeStudent, getErrorBook, getDashboard } from '../../api/analytics'
import type { ClassScoreVO, StudentAnalysisVO, ErrorBookVO, DashboardVO } from '../../types/analytics'

const errorStudentId = ref<number>()
const errorData = ref<ErrorBookVO | null>(null)
const dashData = ref<DashboardVO | null>(null)

async function doErrorBook() {
  if (!errorStudentId.value) return
  try { errorData.value = await getErrorBook(errorStudentId.value) } catch (e) { ElMessage.error((e as Error).message) }
}
async function doDashboard() {
  try { dashData.value = await getDashboard() } catch (e) { ElMessage.error((e as Error).message) }
}
doDashboard()

const examId = ref<number>()
const studentId = ref<number>()
const classData = ref<ClassScoreVO | null>(null)
const studentData = ref<StudentAnalysisVO | null>(null)

async function doExamAnalysis() {
  if (!examId.value) return
  try { classData.value = await analyzeExam(examId.value) }
  catch (e) { ElMessage.error((e as Error).message) }
}

async function doStudentAnalysis() {
  if (!studentId.value) return
  try { studentData.value = await analyzeStudent(studentId.value) }
  catch (e) { ElMessage.error((e as Error).message) }
}
</script>

<template>
  <el-card>
    <h2>成绩学情分析</h2>

    <!-- 班级成绩分析 -->
    <el-divider>班级成绩分析（按考试）</el-divider>
    <div class="bar">
      <el-input-number v-model="examId" :controls="false" placeholder="考试ID" style="width: 240px" />
      <el-button type="primary" @click="doExamAnalysis">分析</el-button>
    </div>
    <template v-if="classData">
      <el-row :gutter="16" style="margin: 12px 0">
        <el-col :span="4"><el-statistic title="参考人数" :value="classData.studentCount" /></el-col>
        <el-col :span="4"><el-statistic title="平均分" :value="(classData.avgScore / 1000).toFixed(1)" /></el-col>
        <el-col :span="4"><el-statistic title="最高分" :value="(classData.maxScore / 1000).toFixed(1)" /></el-col>
        <el-col :span="4"><el-statistic title="最低分" :value="(classData.minScore / 1000).toFixed(1)" /></el-col>
        <el-col :span="4"><el-statistic title="及格率" :value="classData.passRate + '%'" /></el-col>
      </el-row>
      <h4>分数段分布</h4>
      <div v-for="seg in classData.segments" :key="seg.label" class="seg-row">
        <span class="seg-label">{{ seg.label }}分</span>
        <el-progress :percentage="classData.studentCount > 0 ? seg.count * 100 / classData.studentCount : 0" :format="() => seg.count + '人'" />
      </div>
      <el-table :data="classData.students" border style="margin-top: 12px">
        <el-table-column prop="studentId" label="学生ID" />
        <el-table-column label="得分"><template #default="{ row }">{{ (row.score / 1000).toFixed(1) }}分</template></el-table-column>
        <el-table-column prop="status" label="状态" width="120" />
      </el-table>
    </template>

    <!-- 个人学情分析 -->
    <el-divider>个人学情分析（掉队预警）</el-divider>
    <div class="bar">
      <el-input-number v-model="studentId" :controls="false" placeholder="学生ID" style="width: 240px" />
      <el-button type="primary" @click="doStudentAnalysis">分析</el-button>
    </div>
    <template v-if="studentData">
      <el-alert v-if="studentData.atRisk" title="⚠️ 掉队预警：综合得分率低于60%，建议关注" type="error" show-icon style="margin: 12px 0" />
      <el-alert v-else title="✅ 正常：综合表现良好" type="success" show-icon style="margin: 12px 0" />
      <el-row :gutter="16">
        <el-col :span="8"><el-statistic title="考试平均得分率" :value="studentData.examAvgRate + '%'" /></el-col>
        <el-col :span="8"><el-statistic title="作业平均得分率" :value="studentData.homeworkAvgRate + '%'" /></el-col>
      </el-row>
      <el-table :data="studentData.examScores" border style="margin-top: 12px">
        <el-table-column prop="examId" label="考试ID" />
        <el-table-column label="得分"><template #default="{ row }">{{ (row.score / 1000).toFixed(1) }} / {{ (row.paperScore / 1000).toFixed(1) }}</template></el-table-column>
        <el-table-column label="得分率"><template #default="{ row }">{{ row.rate }}%</template></el-table-column>
        <el-table-column prop="status" label="状态" width="120" />
      </el-table>
    </template>

    <!-- 错题本 -->
    <el-divider>错题本</el-divider>
    <div class="bar">
      <el-input-number v-model="errorStudentId" :controls="false" placeholder="学生ID" style="width: 240px" />
      <el-button type="primary" @click="doErrorBook">查错题</el-button>
    </div>
    <template v-if="errorData">
      <el-tag v-if="errorData.totalErrors === 0" type="success">无错题（全部答对）</el-tag>
      <el-table v-else :data="errorData.errors" border style="margin-top: 12px">
        <el-table-column prop="questionId" label="题目ID" />
        <el-table-column prop="examId" label="考试ID" />
        <el-table-column prop="answer" label="学生答案" show-overflow-tooltip />
      </el-table>
    </template>

    <!-- 校领导看板 -->
    <el-divider>校领导看板（自动加载）</el-divider>
    <template v-if="dashData">
      <el-row :gutter="16">
        <el-col :span="6"><el-statistic title="考试总数" :value="dashData.totalExams" /></el-col>
        <el-col :span="6"><el-statistic title="试卷总数" :value="dashData.totalPapers" /></el-col>
        <el-col :span="6"><el-statistic title="题目总数" :value="dashData.totalQuestions" /></el-col>
        <el-col :span="6"><el-statistic title="作业总数" :value="dashData.totalHomeworks" /></el-col>
      </el-row>
      <el-table v-if="dashData.recentExams.length" :data="dashData.recentExams" border style="margin-top: 12px">
        <el-table-column prop="name" label="考试" />
        <el-table-column prop="studentCount" label="参考人数" width="100" />
        <el-table-column label="平均分" width="100"><template #default="{ row }">{{ (row.avgScore / 1000).toFixed(1) }}分</template></el-table-column>
      </el-table>
    </template>
  </el-card>
</template>

<style scoped>
.bar { display: flex; align-items: center; gap: 12px; margin: 8px 0; }
.seg-row { display: flex; align-items: center; margin: 4px 0; }
.seg-label { width: 80px; font-size: 13px; }
</style>
