import { request } from './http'

export interface ToolInfo {
  name: string
  module: string
  description: string
  sideEffect: string
  humanInTheLoop: boolean
  requiredRoles: string[]
}

export interface AgentTask {
  id: number
  goal: string
  status: number
  progressPct: number
  currentStep: string
  resultJson?: string
  createTime: string
}

export function listTools(): Promise<ToolInfo[]> {
  return request<ToolInfo[]>({ url: '/v1/ai/tools', method: 'get' })
}

export function listAgentTasks(): Promise<AgentTask[]> {
  return request<AgentTask[]>({ url: '/v1/ai/agent-tasks', method: 'get' })
}

export function getAgentTask(id: number): Promise<AgentTask> {
  return request<AgentTask>({ url: `/v1/ai/agent-tasks/${id}`, method: 'get' })
}
