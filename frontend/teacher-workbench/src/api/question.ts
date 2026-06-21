import { request } from './http'
import type { QuestionSaveDTO, QuestionVO, OptionSaveDTO, OptionVO } from '../types/question'

/** 题库 API */
export function listQuestions(subjectId?: number, gradeLevel?: number, type?: number): Promise<QuestionVO[]> {
  return request<QuestionVO[]>({ url: '/v1/qbank/questions', method: 'get', params: { subjectId, gradeLevel, type } })
}

export function createQuestion(data: QuestionSaveDTO): Promise<number> {
  return request<number>({ url: '/v1/qbank/questions', method: 'post', data })
}

export function createOption(data: OptionSaveDTO): Promise<number> {
  return request<number>({ url: '/v1/qbank/options', method: 'post', data })
}

export function listOptions(questionId: number): Promise<OptionVO[]> {
  return request<OptionVO[]>({ url: '/v1/qbank/options', method: 'get', params: { questionId } })
}
