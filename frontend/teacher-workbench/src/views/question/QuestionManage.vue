<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { listQuestions, createQuestion, createOption } from '../../api/question'
import { QUESTION_TYPE, type QuestionVO } from '../../types/question'

const questions = ref<QuestionVO[]>([])
const loading = ref(false)
const dialogVisible = ref(false)

// 新增题目表单
const form = reactive<{ type: number; gradeLevel: number; scoreYuan: number; stem: string; answer: string; analysis: string }>({
  type: 1, scoreYuan: 5, stem: '', answer: '', analysis: '', gradeLevel: 7
})
// 选项（选择题用）：{ content, isCorrect }
const options = reactive<{ content: string; isCorrect: number }[]>([])

async function load() {
  loading.value = true
  try {
    questions.value = await listQuestions()
  } catch (e) {
    ElMessage.error('加载题目失败')
  } finally {
    loading.value = false
  }
}

function openCreate() {
  form.type = 1; form.scoreYuan = 5; form.stem = ''; form.answer = ''; form.analysis = ''; form.gradeLevel = 7
  options.length = 0
  options.push({ content: '', isCorrect: 1 }, { content: '', isCorrect: 0 })
  dialogVisible.value = true
}

function addOption() {
  options.push({ content: '', isCorrect: 0 })
}
function removeOption(i: number) {
  options.splice(i, 1)
}

// 是否选择题（需选项）
function isChoice(type: number) {
  return type === 1 || type === 2 || type === 3
}

async function submit() {
  if (!form.stem) { ElMessage.warning('题干不能为空'); return }
  if (isChoice(form.type) && options.some(o => !o.content)) { ElMessage.warning('选项内容不能为空'); return }
  try {
    const qid = await createQuestion({
      type: form.type,
      gradeLevel: form.gradeLevel,
      score: form.scoreYuan * 1000,  // 千分制
      stem: form.stem,
      answer: form.answer,
      analysis: form.analysis
    })
    // 选择题：创建选项
    if (isChoice(form.type)) {
      for (let i = 0; i < options.length; i++) {
        await createOption({ questionId: qid, content: options[i].content, isCorrect: options[i].isCorrect, sort: i + 1 })
      }
    }
    ElMessage.success('题目创建成功')
    dialogVisible.value = false
    await load()
  } catch (e) {
    ElMessage.error((e as Error).message || '创建失败')
  }
}

onMounted(load)
</script>

<template>
  <el-card>
    <div class="header">
      <h2>题库管理</h2>
      <el-button type="primary" @click="openCreate">新增题目</el-button>
    </div>

    <el-table :data="questions" v-loading="loading" border>
      <el-table-column label="题型" width="80">
        <template #default="{ row }">{{ QUESTION_TYPE[row.type] || row.type }}</template>
      </el-table-column>
      <el-table-column prop="stem" label="题干" show-overflow-tooltip />
      <el-table-column label="分值" width="80">
        <template #default="{ row }">{{ (row.score / 1000).toFixed(1) }}分</template>
      </el-table-column>
      <el-table-column prop="gradeLevel" label="年级" width="80" />
    </el-table>

    <el-dialog v-model="dialogVisible" title="新增题目" width="640px">
      <el-form label-width="80px">
        <el-form-item label="题型">
          <el-select v-model="form.type" style="width: 160px">
            <el-option v-for="(label, val) in QUESTION_TYPE" :key="val" :label="label" :value="Number(val)" />
          </el-select>
        </el-form-item>
        <el-form-item label="年级">
          <el-input-number v-model="form.gradeLevel" :min="7" :max="9" />
        </el-form-item>
        <el-form-item label="分值(分)">
          <el-input-number v-model="form.scoreYuan" :min="1" :max="100" />
        </el-form-item>
        <el-form-item label="题干" required>
          <el-input v-model="form.stem" type="textarea" :rows="2" />
        </el-form-item>

        <template v-if="isChoice(form.type)">
          <el-divider>选项（勾选正确答案）</el-divider>
          <div v-for="(opt, i) in options" :key="i" class="opt-row">
            <el-radio v-model="opt.isCorrect" :value="1">正确</el-radio>
            <el-radio v-model="opt.isCorrect" :value="0">错误</el-radio>
            <el-input v-model="opt.content" :placeholder="`选项${i + 1}`" style="flex: 1; margin: 0 8px" />
            <el-button text type="danger" @click="removeOption(i)">删除</el-button>
          </div>
          <el-button text type="primary" @click="addOption">+ 添加选项</el-button>
        </template>

        <template v-else>
          <el-form-item label="参考答案">
            <el-input v-model="form.answer" type="textarea" :rows="2" />
          </el-form-item>
        </template>

        <el-form-item label="解析">
          <el-input v-model="form.analysis" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">确定</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<style scoped>
.header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 12px; }
.opt-row { display: flex; align-items: center; margin-bottom: 8px; }
</style>
