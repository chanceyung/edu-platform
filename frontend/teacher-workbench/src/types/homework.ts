export interface HomeworkVO {
  id: number
  title: string
  classId: number
  subjectId?: number
  type: number
  content?: string
  deadline?: string
  status: number
}

export interface HomeworkSaveDTO {
  title: string
  classId: number
  type: number
  content?: string
  deadline?: string
}

export interface HomeworkSubmissionVO {
  id: number
  homeworkId: number
  studentId: number
  content: string
  submitTime?: string
  score: number
  status: number
  comment?: string
}

export const HOMEWORK_TYPE: Record<number, string> = {
  1: '试题', 2: '拍照', 3: '文本'
}
