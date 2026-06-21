import { request } from './http'
import type { HomeworkVO, HomeworkSaveDTO, HomeworkSubmissionVO } from '../types/homework'

export function listHomework(classId: number): Promise<HomeworkVO[]> {
  return request<HomeworkVO[]>({ url: '/v1/homework', method: 'get', params: { classId } })
}

export function createHomework(data: HomeworkSaveDTO): Promise<number> {
  return request<number>({ url: '/v1/homework', method: 'post', data })
}

export function submitHomework(data: { homeworkId: number; studentId: number; content: string }): Promise<number> {
  return request<number>({ url: '/v1/homework/submissions', method: 'post', data })
}

export function getSubmission(homeworkId: number, studentId: number): Promise<HomeworkSubmissionVO> {
  return request<HomeworkSubmissionVO>({ url: '/v1/homework/submissions', method: 'get', params: { homeworkId, studentId } })
}

export function aiGrade(homeworkId: number, studentId: number): Promise<void> {
  return request<void>({ url: '/v1/ai/grading/grade', method: 'post', params: { homeworkId, studentId } })
}
