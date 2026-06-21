<script setup lang="ts">
import { onMounted, ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { listKnowledgePoints, createKnowledgePoint, type KnowledgePoint } from '../../api/knowledge'

const allPoints = ref<KnowledgePoint[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const form = ref<{ parentId: number; name: string }>({ parentId: 0, name: '' })

/** 构建树结构 */
const treeData = computed(() => {
  const map = new Map<number, KnowledgePoint>()
  const roots: KnowledgePoint[] = []
  allPoints.value.forEach(p => {
    p.children = []
    map.set(p.id, p)
  })
  allPoints.value.forEach(p => {
    if (p.parentId === 0 || !map.has(p.parentId)) {
      roots.push(p)
    } else {
      map.get(p.parentId)!.children!.push(p)
    }
  })
  return roots
})

const treeProps = { label: 'name', children: 'children' }

async function load() {
  loading.value = true
  try {
    allPoints.value = await listKnowledgePoints()
  } catch (e) {
    ElMessage.error('加载知识点失败')
  } finally {
    loading.value = false
  }
}

function openCreate(parent?: KnowledgePoint) {
  form.value = {
    parentId: parent ? parent.id : 0,
    name: ''
  }
  dialogVisible.value = true
}

async function doCreate() {
  if (!form.value.name) { ElMessage.warning('名称不能为空'); return }
  try {
    await createKnowledgePoint({ parentId: form.value.parentId, name: form.value.name, sort: 0, status: 1 })
    ElMessage.success('创建成功')
    dialogVisible.value = false
    await load()
  } catch (e) {
    ElMessage.error((e as Error).message)
  }
}

onMounted(load)
</script>

<template>
  <el-card>
    <div class="header">
      <h2>知识点树</h2>
      <el-button type="primary" @click="openCreate()">新增根知识点</el-button>
    </div>

    <el-tree
      :data="treeData"
      :props="treeProps"
      v-loading="loading"
      default-expand-all
      node-key="id"
      :expand-on-click-node="false"
    >
      <template #default="{ data }">
        <span class="tree-node">
          <span>{{ data.name }}</span>
          <el-button text size="small" @click.stop="openCreate(data)">+ 子节点</el-button>
        </span>
      </template>
    </el-tree>

    <el-dialog v-model="dialogVisible" :title="form.parentId === 0 ? '新增根知识点' : '新增子知识点'" width="400px">
      <el-form label-width="80px">
        <el-form-item label="名称" required><el-input v-model="form.name" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="doCreate">确定</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<style scoped>
.header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 12px; }
.tree-node { display: flex; align-items: center; justify-content: space-between; width: 100%; padding-right: 12px; }
</style>
