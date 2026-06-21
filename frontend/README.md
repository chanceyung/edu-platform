# frontend — 前端

## 多端架构

| 端 | 技术 | 用户 | 状态 |
|---|---|---|---|
| `teacher-workbench` | Vue3 + Vite + TS + Element Plus | 教师（核心） | ⭐模板已建，照此开发 |
| `admin` | Vue3 + Vite + TS + Element Plus | 教务/管理员/校领导 | 待开发（复制 teacher-workbench 模板） |
| `student-pc` | Vue3 + Vite + TS | 学生（机房/考试） | 待开发（复制模板） |
| `mobile-student` | uniapp（小程序+H5+APP） | 学生（日常） | 待开发（uniapp 模板） |
| `mobile-parent` | uniapp（小程序为主） | 家长 | 待开发（uniapp 模板） |

## 公共约定

- **Web 端统一模板**：`teacher-workbench` 为标准模板（Vue3 + Vite + TS + Element Plus + Pinia + Vue Router + axios）。新建 web 端复制它。
- **移动端统一模板**：uniapp 工程，条件编译区分小程序/H5/APP。mobile-student 与 mobile-parent 逻辑隔离。
- **请求**：统一 `api/http.ts` 封装（baseURL 指向后端 `/api`、token 拦截、错误码统一处理）。
- **类型**：TypeScript strict，禁止 `any` 滥用；接口类型集中 `types/`。
- **UI 范式（AI-first）**：教师工作台核心是「**对话区 + 任务进度区 + 成果审核区**」，不是传统表单菜单——老师下达目标、看 Agent 进度、审核成果。

## 开发纪律（见 `../CLAUDE.md` 与 `../docs/开发规范.md`）

- 组件 `<script setup lang="ts">`，PascalCase 命名
- 状态 Pinia，请求走 `api/`，类型放 `types/`
- 样式 scoped + 设计令牌，禁止硬编码覆盖
- 提交前 `pnpm lint && pnpm type-check` 必过
