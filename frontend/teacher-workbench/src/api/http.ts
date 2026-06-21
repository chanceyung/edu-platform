import axios, { type AxiosInstance, type AxiosRequestConfig, type AxiosResponse } from 'axios'

/** 后端统一响应结构 */
interface ApiResult<T = unknown> {
  code: number
  message: string
  data: T
}

// 统一请求封装。所有 API 调用必须经此实例，禁止业务代码直连 axios。
const http: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 30000
})

// 请求拦截：注入 token
http.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 响应拦截：统一拆包。把后端 {code,message,data} 中的 data 放回 response.data
http.interceptors.response.use(
  (response) => {
    const body = response.data as ApiResult
    if (body.code === 0) {
      response.data = body.data as never
      return response
    }
    return Promise.reject(new Error(body.message || '请求失败'))
  },
  (error) => {
    // 网络/HTTP 错误处理（401 跳登录等，待补）
    return Promise.reject(error)
  }
)

/**
 * 类型安全的请求方法。返回后端 data 部分（已拆包）。
 * 用法：request<StudentVO[]>({ url: '/v1/org/students', method: 'get' })
 */
export function request<T>(config: AxiosRequestConfig): Promise<T> {
  return http.request<unknown, AxiosResponse<T>>(config).then((res) => res.data)
}

export default http
