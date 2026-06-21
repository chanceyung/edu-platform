<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { listHomework, createHomework, submitHomework, getSubmission, aiGrade } from '../../api/homework'
import { HOMEWORK_TYPE, type HomeworkVO, type HomeworkSubmissionVO } from '../../types/homework'

const homeworks = ref<HomeworkVO[]>([])
const loading = ref(false)
const classId = ref(2068381003435585538)

// 布置作业对话框
const createDialog = ref(false)
const form = reactive<{ title: string; type: number; content: string }>({ title: '', type: 3, content: '' })

// 提交作业对话框
const submitDialog = ref(false)
const currentHw = ref<HomeworkVO | null>(null)
const submitForm = reactive<{ studentId: number; content: string }>({ studentId: 2068385245277794305, content: '' })

// AI 批改结果
const grading = ref(false)
const result = ref<HomeworkSubmissionVO | null>(null)
const resultVisible = ref(false)

async function load() {
  loading.value = true
  try { homeworks.value = await listHomework(classId.value) } catch { ElMessage.error('加载失败') } finally { loading.value = false }
}

function openCreate() { form.title = ''; form.type = 3; form.content = ''; createDialog.value = true }
async function doCreate() {
  if (!form.title) { ElMessage.warning('标题不能为空'); return }
  try {
    await createHomework({ title: form.title, classId: classId.value, type: form.type, content: form.content })
    ElMessage.success('布置成功'); createDialog.value = false; await load()
  } catch (e) { ElMessage.error((e as Error).message) }
}

function openSubmit(hw: HomeworkVO) {
  currentHw.value = hw
  submitForm.content = ''
  submitDialog.value = true
}
async function doSubmit() {
  if (!currentHw.value || !submitForm.content) { ElMessage.warning('请填写答案'); return }
  try {
    await submitHomework({ homeworkId: currentHw.value.id, studentId: submitForm.studentId, content: submitForm.content })
    ElMessage.success('提交成功'); submitDialog.value = false
  } catch (e) { ElMessage.error((e as Error).message) }
}

async function doAiGrade(hw: HomeworkVO) {
  grading.value = true
  result.value = null
  resultVisible.value = true
  try {
    await aiGrade(hw.id, submitForm.studentId)
    result.value = await getSubmission(hw.id, submitForm.studentId)
    ElMessage.success('AI 批改完成')
  } catch (e) { ElMessage.error((e as Error).message) } finally { grading.value = false }
}

onMounted(load)
</script>

<template>
  <el-card>
    <div class="header">
      <h2>作业管理</h2>
      <div>
        <el-input-number v-model="classId" :controls="false" placeholder="班级ID" style="width: 200px; margin-right: 8px" @change="load" />
        <el-button type="primary" @click="openCreate">布置作业</el-button>
      </div>
    </div>

    <el-table :data="homeworks" v-loading="loading" border>
      <el-table-column prop="title" label="标题" />
      <el-table-column label="类型" width="80"><template #default="{ row }">{{ HOMEWORK_TYPE[row.type] }}</template></el-table-column>
      <el-table-column prop="content" label="内容" show-overflow-tooltip />
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button size="small" @click="openSubmit(row)">学生提交</el-button>
          <el-button size="small" type="success" @click="doAiGrade(row)">AI批改</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 布置作业对话框 -->
    <el-dialog v-model="createDialog" title="布置作业" width="500px">
      <el-form label-width="60px">
        <el-form-item label="标题" required><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="类型">
          <el-select v-model="form.type" style="width: 120px">
            <el-option v-for="(label, val) in HOMEWORK_TYPE" :key="val" :label="label" :value="Number(val)" />
          </el-select>
        </el-form-item>
        <el-form-item label="内容"><el-input v-model="form.content" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="createDialog = false">取消</el-button><el-button type="primary" @click="doCreate">确定</el-button></template>
    </el-dialog>

    <!-- 学生提交对话框 -->
    <el-dialog v-model="submitDialog" :title="`提交作业：${currentHw?.title || ''}`" width="500px">
      <el-form label-width="80px">
        <el-form-item label="学生ID"><el-input-number v-model="submitForm.studentId" :controls="false" style="width: 100%" /></el-form-item>
        <el-form-item label="答案" required><el-input v-model="submitForm.content" type="textarea" :rows="4" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="submitDialog = false">取消</el-button><el-button type="primary" @click="doSubmit">提交</el-button></template>
    </el-dialog>

    <!-- AI 批改结果 -->
    <el-dialog v-model="resultVisible" title="AI 批改结果（智谱 GLM-5.2）" width="500px">
      <div v-loading="grading">
        <template v-if="result">
          <el-result :icon="result.score >= 6000 ? 'success' : 'warning'"
            :title="`得分：${(result.score / 1000).toFixed(1)} / 10 分`" :sub-title="result.comment || '无评语'">
          </el-result>
        </template>
        <el-empty v-else-if="!grading" description="无提交记录，请先学生提交" />
      </div>
    </el-dialog>
  </el-card>
</template>

<style scoped>
.header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 12px; }
</style>
