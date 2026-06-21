import { request } from './http'
import type { HsNoticeVO, HsNoticeSaveDTO } from '../types/ai'

// AI 备课
export function generateLesson(topic: string, gradeLevel: number): Promise<string> {
  return request<string>({ url: '/v1/ai/lesson/generate', method: 'post', params: { topic, gradeLevel } })
}

// AI 评语
export function generateComment(studentId: number): Promise<string> {
  return request<string>({ url: '/v1/ai/comment/generate', method: 'post', params: { studentId } })
}

// 家校通知
export function listNotices(classId?: number): Promise<HsNoticeVO[]> {
  return request<HsNoticeVO[]>({ url: '/v1/hs/notices', method: 'get', params: { classId } })
}

export function createNotice(data: HsNoticeSaveDTO): Promise<number> {
  return request<number>({ url: '/v1/hs/notices', method: 'post', data })
}
