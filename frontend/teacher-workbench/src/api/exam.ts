import { request } from './http'
import type { PaperVO, PaperSaveDTO, ComposeDTO, PaperDetailVO, ExamVO, ExamSaveDTO, ExamRecordVO, SubmitDTO } from '../types/exam'

/** 试卷/组卷 API */
export function listPapers(): Promise<PaperVO[]> {
  return request<PaperVO[]>({ url: '/v1/exam/papers', method: 'get' })
}
export function createPaper(data: PaperSaveDTO): Promise<number> {
  return request<number>({ url: '/v1/exam/papers', method: 'post', data })
}
export function composePaper(data: ComposeDTO): Promise<void> {
  return request<void>({ url: '/v1/exam/papers/compose', method: 'post', data })
}
export function getPaperDetail(id: number): Promise<PaperDetailVO> {
  return request<PaperDetailVO>({ url: `/v1/exam/papers/${id}/detail`, method: 'get' })
}

/** 考试 API */
export function listExams(classId?: number): Promise<ExamVO[]> {
  return request<ExamVO[]>({ url: '/v1/exam/exams', method: 'get', params: { classId } })
}
export function createExam(data: ExamSaveDTO): Promise<number> {
  return request<number>({ url: '/v1/exam/exams', method: 'post', data })
}

/** 答卷 API */
export function submitRecord(data: SubmitDTO): Promise<number> {
  return request<number>({ url: '/v1/exam/records/submit', method: 'post', data })
}
export function getRecord(examId: number, studentId: number): Promise<ExamRecordVO> {
  return request<ExamRecordVO>({ url: '/v1/exam/records', method: 'get', params: { examId, studentId } })
}
