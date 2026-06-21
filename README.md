# 智慧教学平台（edu-platform）

> 面向初中学校的 **AI 教学减负系统** —— 老师的 AI 数字员工。
> Agent-first 范式：老师下达目标 → Agent 自主规划+执行+交付 → 老师核验。

## 定位

- **核心用户**：教师（替老师干活，不是让老师填表）
- **买单者**：学校/教务（看学情与提分数据）
- **差异化**：AI 贯穿备课/作业/批改/考试/学情/家校全闭环；三件套统一 + 私有知识 + 信任内核

## 技术栈

Spring Boot 3 + JDK17 + MyBatis-Plus + MySQL8 + Redis ｜ Vue3 + Element Plus ｜ uniapp ｜ 智谱 GLM-5.2 全家桶 ｜ LangGraph + MCP ｜ Docker/K8s

## 项目结构

```
backend/      后端 Maven 多模块（gateway/ruoyi-base/school-org/question-bank/exam-engine/homework/ai-service/analytics/home-school）
frontend/     teacher-workbench(教师工作台) / admin / student-pc / mobile-student / mobile-parent
ai-platform/  Agent 平台（MCP 工具协议/编排/长程任务/信任内核）
deploy/       docker-compose / k8s / nginx
docs/         产品规划、开发规范、数据字典、API 文档
```

## 核心文档（必读）

| 文档 | 说明 |
|---|---|
| **`CLAUDE.md`** | ⭐ 开发规则（铁律/规范/禁令）——开工前必读 |
| `docs/产品规划.md` | 完整产品规划（多端/功能/数据模型/开发计划/AI/Agent） |
| `docs/开发规范.md` | 详细技术规范（分层/API/DB/测试/安全） |
| `../AI开源项目套壳商用-License审计.md` | 依赖选型的 license 合规依据 |

## 快速开始（待工程骨架就位后补全）

```bash
# 后端
cd backend && mvn clean install && java -jar gateway/target/gateway.jar
# 前端（教师工作台）
cd frontend/teacher-workbench && pnpm install && pnpm dev
# AI 网关配置见 ai-platform/README.md（需配置智谱 API Key）
```

## 复用来源（合规边界）

- **RuoYi**（MIT）：脚手架直接用
- **sg-exam**（Apache，`../sg-exam-eval/`）：考试引擎代码参考
- **学之思 xzs**（AGPL，`../xzs-mysql-master/`）：**只参考功能设计，严禁抄码**

## 开发纪律

**扎实开发，严禁胡乱开发。** 详见 `CLAUDE.md` 铁律与禁令清单。核心：License 纯净、AI 必可追溯、测试先行、模块边界、不擅引入技术。
