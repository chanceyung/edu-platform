# ai-platform — Agent 平台（产品核心）

> 本目录是 Agent-first 架构的核心规范与契约。**所有 AI/Agent 开发必须遵循此规范。**

## 一、架构总览

```
ai-platform/
├── README.md            # 本文件（规范与契约）
├── tools/               # MCP 工具定义（schema + 权限 + 校验）—— Agent 的"手脚"
├── prompts/             # Prompt 模板（版本化）—— Agent 的"大脑指令"
├── agents/              # Agent 定义（角色/工具集/记忆/评测基线）
├── eval/                # 评测集与回归脚本（批改准确率/组卷合理性等）
└── docs/                # 设计文档（编排/任务管理/信任内核）
```

> 实际运行代码在 `backend/ai-service`；本目录是**规范、契约、资产（工具/Prompt/评测集）**，与代码分离以便版本治理与评审。

## 二、四块基石（缺一不可，见 CLAUDE.md）

1. **工具集（Tools/Skills，MCP 协议）**——Agent 的手脚
2. **多 Agent 协同**——编排(Planner) + 专业(Worker) + 审校(Reviewer)
3. **长程任务管理**——持久化/检查点/进度可视化/状态机兜底
4. **信任内核**——动作分级/人在回路/可追溯/可回滚/可中断

## 三、MCP 工具协议（Tool 定义规范）

每个工具是一个独立声明，Agent 通过函数调用发现并使用。工具定义放在 `tools/{module}/{tool-name}.yaml`：

```yaml
name: qbank_generate_paper          # 全局唯一，snake_case
module: question-bank               # 归属模块
version: 1.0.0
description: 按知识点/难度/题量自动组卷
# 权限：哪些角色/Agent 可调用
permissions:
  roles: [teacher, admin]
  agents: [zujuan-agent]
# 入参 schema（JSON Schema）
input_schema:
  type: object
  required: [subject_id, grade_level, total_score]
  properties:
    subject_id: { type: integer }
    grade_level: { type: integer }
    knowledge_points: { type: array, items: { type: integer } }
    difficulty: { type: string, enum: [easy, medium, hard] }
    total_score: { type: integer, description: 千分制 }
# 出参 schema
output_schema:
  type: object
  properties:
    paper_id: { type: integer }
    question_count: { type: integer }
# 副作用等级（信任内核用）
side_effect: read_only              # read_only / write / destructive / external_notify
# 是否需要人在回路
human_in_the_loop: false
# 对应后端实现
implementation: com.eduplatform.qbank.api.PaperTool#generate
```

**工具开发铁律**：
- 所有系统功能要被 Agent 用，**必须先按此协议定义工具**，再实现。
- `side_effect=write/destructive/external_notify` 的工具，必须接入信任内核（分级/日志/可回滚）。
- 工具入参出参严格 schema，禁止裸传。
- 工具实现走 `backend/ai-service` 的统一调用层，业务模块提供 `api` 接口。

## 四、角色 Agent 编制（`agents/`）

| Agent | 职责 | 主工具 | 评测指标 |
|---|---|---|---|
| 教学助手 Agent | 备课/课件 | 文档工具+CogView+CogVideoX+题库 | 课件可用率 |
| 批改 Agent | 批改作业/试卷 | GLM-5V识别+成绩工具+评分 | 批改准确率/与教师一致率 |
| 组卷 Agent | 出卷 | 题库+知识点+组卷工具 | 考点覆盖率/难度合理性 |
| 学情分析 Agent | 分析/预警 | 成绩+分析+1M上下文 | 预警准确率 |
| 家校沟通 Agent | 反馈/通知 | 通知+文档工具 | 内容合规率 |
| 班主任 Agent | 综合编排 | 全工具 | 任务完成率 |

每个 Agent 定义文件含：角色 Prompt、可用工具白名单、记忆策略、信任等级、评测基线。

## 五、信任内核（强制，无例外）

每个 Agent 执行必须：
1. **动作分级**（仿 Claude Code auto mode）：read_only 自动 / write 记日志 / destructive+external 强制人在回路
2. **全程日志**：`ai_task_log`（任务级）+ `ai_grading_log`（动作级），含 input/output/model/token/耗时/actor
3. **可回滚**：write 类操作生成补偿动作，支持 undo
4. **可中断**：长任务随时可暂停/取消
5. **人在回路**：关键节点（发全校通知/改成绩/对外发送）必须人工确认

## 六、Prompt 治理（`prompts/`）

- Prompt 模板版本化（`prompts/{agent}/{version}.md`），变更走评审 + 评测回归
- 禁止在代码里硬编码大段 Prompt；统一从模板加载
- 含变量占位，运行时注入（注意注入安全，防 Prompt 注入）

## 七、智谱网关接入（`backend/ai-service`）

- 所有模型调用经统一网关，禁止业务模块直连智谱 API
- 模型路由：复杂长程→GLM-5.2(1M)、高频简单→GLM-4 Air(降本)、识图→GLM-5V、生图→CogView、视频→CogVideoX、向量→Embedding-3
- 网关职责：鉴权/限流/计费/缓存/降级/日志
- API Key 用环境变量，严禁入库

## 八、评测（`eval/`）

- 核心能力必有评测集：批改准确率、组卷合理性、评语质量、学情预警准确率
- 每次 Prompt/模型/工具变更跑回归，不达标阻塞发布
- 评测数据脱敏，不含真实学生隐私
