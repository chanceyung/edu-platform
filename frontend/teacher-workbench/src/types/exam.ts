/** 考试引擎类型 */
export interface PaperVO {
  id: number
  name: string
  subjectId?: number
  gradeLevel?: number
  totalScore: number
  questionCount: number
  paperType: number
  status: number
}

export interface PaperSaveDTO {
  name: string
  subjectId?: number
  gradeLevel?: number
  paperType?: number
}

export interface ComposeItem {
  questionId: number
  score: number
}

export interface ComposeDTO {
  paperId: number
  items: ComposeItem[]
}

export interface PaperDetailVO {
  id: number
  name: string
  totalScore: number
  questionCount: number
  questions: PaperDetailQuestion[]
}

export interface PaperDetailQuestion {
  questionId: number
  stem: string
  type: number
  score: number
  sort: number
  options: PaperDetailOption[]
}

export interface PaperDetailOption {
  id: number
  content: string
  isCorrect: number
}

export interface ExamVO {
  id: number
  name: string
  paperId: number
  classId: number
  startTime?: string
  endTime?: string
  duration?: number
  status: number
}

export interface ExamSaveDTO {
  name: string
  paperId: number
  classId: number
  startTime?: string
  endTime?: string
  duration?: number
}

export interface ExamRecordVO {
  id: number
  examId: number
  studentId: number
  submitTime?: string
  systemScore: number
  paperScore: number
  status: number        // 1待判分 2完成
}

export interface SubmitAnswerItem {
  questionId: number
  answer: string        // 选项ID JSON 如 "[123]"
}

export interface SubmitDTO {
  examId: number
  studentId: number
  answers: SubmitAnswerItem[]
}
