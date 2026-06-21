/** 题库类型 */
export interface QuestionVO {
  id: number
  type: number          // 1单选 2多选 3判断 4填空 5简答
  subjectId?: number
  gradeLevel?: number
  score: number         // 千分制
  difficult?: number
  knowledgePointId?: number
  stem: string
  answer?: string
  analysis?: string
  status: number
}

export interface QuestionSaveDTO {
  type: number
  subjectId?: number
  gradeLevel?: number
  score: number
  difficult?: number
  stem: string
  answer?: string
  analysis?: string
}

export interface OptionVO {
  id: number
  questionId: number
  content: string
  isCorrect: number     // 0错 1对
  sort: number
}

export interface OptionSaveDTO {
  questionId: number
  content: string
  isCorrect?: number
  sort?: number
}

/** 题型映射 */
export const QUESTION_TYPE: Record<number, string> = {
  1: '单选', 2: '多选', 3: '判断', 4: '填空', 5: '简答'
}
