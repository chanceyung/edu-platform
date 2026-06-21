import { request } from './http'
import type { SchoolSaveDTO, SchoolVO } from '../types/school'

/** 学校相关 API。所有调用经 http 封装，类型化返回。 */

export function listSchools(): Promise<SchoolVO[]> {
  return request<SchoolVO[]>({ url: '/v1/org/schools', method: 'get' })
}

export function createSchool(data: SchoolSaveDTO): Promise<number> {
  return request<number>({ url: '/v1/org/schools', method: 'post', data })
}
