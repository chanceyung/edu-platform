# teacher-workbench — PC 教师工作台（前端模板）

> **本工程是 Web 端开发模板。** admin / student-pc 复制本工程结构开发。

## 技术栈

Vue 3 + Vite + TypeScript + Element Plus + Pinia + Vue Router + axios

## 目录结构（规范）

```
src/
├── api/          请求层（http.ts 封装 + 各模块 api）—— 禁止业务直连 axios
├── views/        页面（AI-first：对话区/任务进度/成果审核）
├── components/   公共组件
├── stores/       Pinia 状态
├── router/       路由（权限路由动态加载）
├── types/        TypeScript 类型定义
├── utils/        工具
├── App.vue
└── main.ts
```

## 启动

```bash
pnpm install
pnpm dev          # 启动开发服务器（代理后端 localhost:8080）
pnpm build        # 构建（含 type-check）
pnpm lint         # 代码检查（提交前必过）
```

## AI-first UI 范式（区别于传统管理后台）

教师工作台核心不是"表单菜单"，而是三区联动：
1. **对话区**：老师用自然语言下达目标（"批改三班数学作业并出学情报告"）
2. **任务进度区**：实时看 Agent 规划/执行进度（批改中 20/45...）
3. **成果审核区**：Agent 交付的成果（成绩/报告/通知）待老师一键核验确认

详见 `../../docs/产品规划.md` 第八章 Agent 架构。

## 开发纪律

- 组件 `<script setup lang="ts">`，PascalCase
- 请求走 `api/`，类型放 `types/`，状态放 `stores/`
- 提交前 `pnpm lint && pnpm type-check` 必过
- 见 `../../CLAUDE.md` 与 `../../docs/开发规范.md`
