export interface HsNoticeVO {
  id: number
  title: string
  content: string
  targetClassId?: number
  noticeType: number
  status: number
}

export interface HsNoticeSaveDTO {
  title: string
  content: string
  targetClassId?: number
  noticeType?: number
}
