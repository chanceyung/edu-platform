import { request } from './http'
import type { ClassScoreVO, StudentAnalysisVO, ErrorBookVO, DashboardVO } from '../types/analytics'

export function analyzeExam(examId: number): Promise<ClassScoreVO> {
  return request<ClassScoreVO>({ url: `/v1/analytics/exam/${examId}`, method: 'get' })
}

export function analyzeStudent(studentId: number): Promise<StudentAnalysisVO> {
  return request<StudentAnalysisVO>({ url: `/v1/analytics/student/${studentId}`, method: 'get' })
}

export function getErrorBook(studentId: number): Promise<ErrorBookVO> {
  return request<ErrorBookVO>({ url: `/v1/analytics/errorbook/${studentId}`, method: 'get' })
}

export function getDashboard(): Promise<DashboardVO> {
  return request<DashboardVO>({ url: '/v1/analytics/dashboard', method: 'get' })
}
