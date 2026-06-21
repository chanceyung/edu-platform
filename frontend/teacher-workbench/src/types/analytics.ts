export interface ClassScoreVO {
  examId: number
  examName: string
  studentCount: number
  avgScore: number
  maxScore: number
  minScore: number
  passRate: number
  segments: { label: string; count: number }[]
  students: { studentId: number; score: number; status: string }[]
}

export interface StudentAnalysisVO {
  studentId: number
  examAvgRate: number
  homeworkAvgRate: number
  atRisk: boolean
  examScores: { examId: number; score: number; paperScore: number; rate: number; status: string }[]
}

export interface ErrorBookVO {
  studentId: number
  totalErrors: number
  errors: { questionId: number; examId: number; answer: string; doRight: number }[]
}

export interface DashboardVO {
  totalExams: number
  totalQuestions: number
  totalPapers: number
  totalHomeworks: number
  recentExams: { examId: number; name: string; studentCount: number; avgScore: number }[]
}
