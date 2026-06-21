/** 学校相关类型（与后端 VO/DTO 对齐） */

export interface SchoolVO {
  id: number
  name: string
  code: string
  address?: string
  schoolType: string
  status: number
}

export interface SchoolSaveDTO {
  name: string
  code: string
  address?: string
  schoolType?: string
}
