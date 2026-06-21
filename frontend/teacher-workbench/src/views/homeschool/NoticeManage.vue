<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { listNotices, createNotice } from '../../api/ai'
import type { HsNoticeVO } from '../../types/ai'

const notices = ref<HsNoticeVO[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const form = reactive<{ title: string; content: string; targetClassId?: number }>({ title: '', content: '' })
const classId = ref<number>()

async function load() {
  loading.value = true
  try { notices.value = await listNotices(classId.value) } catch { ElMessage.error('加载失败') } finally { loading.value = false }
}
function openCreate() { form.title = ''; form.content = ''; form.targetClassId = undefined; dialogVisible.value = true }
async function doCreate() {
  if (!form.title || !form.content) { ElMessage.warning('标题和内容必填'); return }
  try { await createNotice({ ...form, noticeType: 1 }); ElMessage.success('发布成功'); dialogVisible.value = false; await load() }
  catch (e) { ElMessage.error((e as Error).message) }
}
onMounted(load)
</script>

<template>
  <el-card>
    <div class="header"><h2>家校通知</h2>
      <div><el-input-number v-model="classId" :controls="false" placeholder="班级ID" style="width: 180px; margin-right: 8px" @change="load" /><el-button type="primary" @click="openCreate">发布通知</el-button></div>
    </div>
    <el-table :data="notices" v-loading="loading" border>
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="content" label="内容" show-overflow-tooltip />
      <el-table-column label="类型" width="100"><template #default="{ row }">{{ row.noticeType === 1 ? '通知' : row.noticeType === 2 ? '成绩推送' : '作业提醒' }}</template></el-table-column>
    </el-table>
    <el-dialog v-model="dialogVisible" title="发布通知" width="500px">
      <el-form label-width="60px">
        <el-form-item label="标题" required><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="内容" required><el-input v-model="form.content" type="textarea" :rows="4" /></el-form-item>
        <el-form-item label="班级"><el-input-number v-model="form.targetClassId" :controls="false" placeholder="留空=全校" style="width: 100%" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible = false">取消</el-button><el-button type="primary" @click="doCreate">发布</el-button></template>
    </el-dialog>
  </el-card>
</template>

<style scoped>
.header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 12px; }
</style>
