<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listSchools, createSchool } from '../../api/school'
import type { SchoolSaveDTO, SchoolVO } from '../../types/school'

// 学校列表
const schools = ref<SchoolVO[]>([])
const loading = ref(false)

// 新增对话框
const dialogVisible = ref(false)
const form = reactive<SchoolSaveDTO>({ name: '', code: '', address: '', schoolType: 'MIDDLE' })

async function load() {
  loading.value = true
  try {
    schools.value = await listSchools()
  } catch (e) {
    ElMessage.error('加载学校列表失败')
  } finally {
    loading.value = false
  }
}

function openCreate() {
  form.name = ''
  form.code = ''
  form.address = ''
  form.schoolType = 'MIDDLE'
  dialogVisible.value = true
}

async function submit() {
  if (!form.name || !form.code) {
    ElMessage.warning('校名与编码不能为空')
    return
  }
  try {
    await createSchool({ ...form })
    ElMessage.success('创建成功')
    dialogVisible.value = false
    await load()
  } catch (e) {
    ElMessage.error((e as Error).message || '创建失败')
  }
}

onMounted(load)
</script>

<template>
  <div class="school-manage">
    <div class="header">
      <h2>学校管理</h2>
      <el-button type="primary" @click="openCreate">新增学校</el-button>
    </div>

    <el-table :data="schools" v-loading="loading" border>
      <el-table-column prop="name" label="学校名称" />
      <el-table-column prop="code" label="编码" width="160" />
      <el-table-column prop="address" label="地址" />
      <el-table-column prop="schoolType" label="类型" width="100" />
      <el-table-column prop="status" label="状态" width="80" />
    </el-table>

    <el-dialog v-model="dialogVisible" title="新增学校" width="480px">
      <el-form label-width="80px">
        <el-form-item label="校名" required>
          <el-input v-model="form.name" placeholder="如：第一中学" />
        </el-form-item>
        <el-form-item label="编码" required>
          <el-input v-model="form.code" placeholder="租户内唯一，如 NO1" />
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="form.address" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.school-manage {
  padding: 16px;
}
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}
</style>
