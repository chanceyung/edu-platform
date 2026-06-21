import { request } from './http'

export interface KnowledgePoint {
  id: number
  parentId: number
  name: string
  sort: number
  status: number
  children?: KnowledgePoint[]
}

export function listKnowledgePoints(): Promise<KnowledgePoint[]> {
  return request<KnowledgePoint[]>({ url: '/v1/qbank/knowledge-points', method: 'get' })
}

export function createKnowledgePoint(data: Partial<KnowledgePoint>): Promise<number> {
  return request<number>({ url: '/v1/qbank/knowledge-points', method: 'post', data })
}
