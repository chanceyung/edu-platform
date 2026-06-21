<script setup lang="ts">
import { onMounted, onUnmounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { listTools, listAgentTasks, getAgentTask, type ToolInfo, type AgentTask } from '../../api/agent'
import { getToken } from '../../utils/auth'

const tools = ref<ToolInfo[]>([])
const tasks = ref<AgentTask[]>([])
const goalInput = ref('')
const currentTaskId = ref<number>()
const polling = ref<number | null>(null)

const statusMap: Record<number, { label: string; type: string }> = {
  0: { label: '待开始', type: 'info' },
  1: { label: '运行中', type: 'warning' },
  2: { label: '已完成', type: 'success' },
  3: { label: '已取消', type: 'info' },
  4: { label: '失败', type: 'danger' }
}

const sideEffectColor: Record<string, string> = {
  READ_ONLY: 'success',
  WRITE: 'warning',
  DESTRUCTIVE: 'danger',
  EXTERNAL_NOTIFY: 'danger'
}

async function loadTools() {
  try { tools.value = await listTools() } catch (e) { ElMessage.error('加载工具失败') }
}

async function loadTasks() {
  try { tasks.value = await listAgentTasks() } catch (e) { /* ignore */ }
}

/** 提交 Agent 目标（SSE 流式监听进度） */
async function submitGoal() {
  if (!goalInput.value.trim()) { ElMessage.warning('请输入目标'); return }

  try {
    const token = getToken()
    const resp = await fetch('/api/v1/ai/agent-tasks/run', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
      body: JSON.stringify({ goal: goalInput.value })
    })

    if (!resp.ok) { ElMessage.error('Agent启动失败'); return }

    // 读取 SSE 流
    const reader = resp.body?.getReader()
    const decoder = new TextDecoder()
    let buffer = ''

    while (reader && true) {
      const { done, value } = await reader.read()
      if (done) break
      buffer += decoder.decode(value, { stream: true })
      const lines = buffer.split('\n')
      buffer = lines.pop() || ''
      for (const line of lines) {
        if (line.startsWith('data:')) {
          try {
            const data = JSON.parse(line.slice(5).trim())
            handleSseEvent(data)
          } catch (e) { /* ignore parse errors */ }
        }
      }
    }
    loadTasks()
  } catch (e) {
    ElMessage.error((e as Error).message)
  }
}

function handleSseEvent(data: Record<string, unknown>) {
  if (data.taskId) currentTaskId.value = Number(data.taskId)
  if (data.progress !== undefined) {
    ElMessage.info(`进度: ${data.progress}% - ${data.step}`)
  }
  if (data.answer) {
    ElMessage.success('Agent 完成！')
  }
  loadTasks()
}

onMounted(() => {
  loadTools()
  loadTasks()
  // 轮询任务状态（补充 SSE 不稳定）
  polling.value = window.setInterval(loadTasks, 5000)
})

onUnmounted(() => {
  if (polling.value) clearInterval(polling.value)
})
</script>

<template>
  <div class="workbench">
    <!-- 左：对话区 -->
    <el-card class="zone zone-dialogue">
      <template #header><span>💬 对话区</span></template>
      <el-input v-model="goalInput" type="textarea" :rows="4" placeholder="告诉AI你想做什么，如：帮我备一节初二《勾股定理》的课" />
      <el-button type="primary" style="margin-top: 12px; width: 100%" @click="submitGoal">
        🚀 派给 Agent
      </el-button>

      <el-divider content-position="left">可用工具</el-divider>
      <div class="tools-list">
        <el-tag v-for="t in tools" :key="t.name" :type="sideEffectColor[t.sideEffect] || 'info'" size="small" style="margin: 2px">
          {{ t.name }}
        </el-tag>
      </div>
    </el-card>

    <!-- 中：进度区 -->
    <el-card class="zone zone-progress">
      <template #header><span>📊 任务进度</span></template>
      <div v-if="tasks.length === 0" class="empty">暂无任务</div>
      <div v-for="t in tasks" :key="t.id" class="task-card">
        <div class="task-header">
          <el-tag :type="statusMap[t.status]?.type || 'info'" size="small">{{ statusMap[t.status]?.label || '未知' }}</el-tag>
          <span class="task-goal">{{ t.goal?.slice(0, 40) }}{{ t.goal?.length > 40 ? '...' : '' }}</span>
        </div>
        <el-progress :percentage="t.progressPct" :status="t.status === 2 ? 'success' : t.status === 4 ? 'exception' : ''" />
        <div class="task-step">{{ t.currentStep }}</div>
      </div>
    </el-card>

    <!-- 右：成果审核区 -->
    <el-card class="zone zone-review">
      <template #header><span>✅ 成果审核</span></template>
      <div v-if="!currentTaskId" class="empty">选择一个已完成任务查看成果</div>
      <template v-else>
        <div v-for="t in tasks.filter(x => x.id === currentTaskId)" :key="t.id">
          <div v-if="t.resultJson">
            <div class="result-text">{{ JSON.parse(t.resultJson).answer?.slice(0, 500) }}...</div>
            <div style="margin-top: 12px">
              <el-button type="success" size="small">批准</el-button>
              <el-button type="danger" size="small">驳回（回滚）</el-button>
            </div>
          </div>
          <div v-else class="empty">任务尚未完成</div>
        </div>
      </template>
    </el-card>
  </div>
</template>

<style scoped>
.workbench {
  display: flex;
  gap: 12px;
  height: calc(100vh - 100px);
}
.zone {
  flex: 1;
  overflow-y: auto;
}
.zone-dialogue { flex: 0 0 300px; }
.zone-review { flex: 0 0 320px; }
.tools-list { display: flex; flex-wrap: wrap; }
.empty { color: #999; text-align: center; padding: 40px 0; }
.task-card {
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 12px;
  margin-bottom: 12px;
  cursor: pointer;
}
.task-card:hover { border-color: #409eff; }
.task-header { display: flex; align-items: center; gap: 8px; margin-bottom: 8px; }
.task-goal { font-size: 13px; color: #333; }
.task-step { font-size: 12px; color: #999; margin-top: 4px; }
.result-text {
  white-space: pre-wrap;
  word-wrap: break-word;
  font-size: 13px;
  line-height: 1.6;
  background: #f5f7fa;
  padding: 12px;
  border-radius: 8px;
  max-height: 400px;
  overflow-y: auto;
}
</style>
