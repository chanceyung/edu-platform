# backend — 智慧教学平台后端

Spring Boot 3 + JDK17 + MyBatis-Plus + MySQL8 + Redis，Maven 多模块。

## 模块地图

| 模块 | 职责 | 来源 | 状态 |
|---|---|---|---|
| `gateway` | API 网关 / 聚合启动入口（SpringBoot 启动类） | 自研 | 待开发 |
| `ruoyi-base` | 脚手架：用户/角色/菜单/部门/字典/日志/代码生成/权限 | ✅RuoYi(MIT) | 待接入 |
| `school-org` | 学校组织：校/年级/班/学期/师生/任课/家长 | 🔨自研 | 待开发 |
| `question-bank` | 题库：题型/知识点树/难度/千分制/富文本外置/批量导入 | 📖学之思设计 | 待开发 |
| `exam-engine` | 考试引擎：组卷/考试/防作弊/判分 | 📖sg-exam+学之思 | 待开发 |
| `homework` | 作业：布置/提交/批改/订正 | 🔨自研 | 待开发 |
| `ai-service` | AI 能力 + 智谱网关 + Agent 编排 + 信任内核 | 🤖自研（核心） | 待开发 |
| `analytics` | 成绩学情分析：班级/个人/薄弱点/预警 | 🔨自研 | 待开发 |
| `home-school` | 家校沟通：通知/成绩推送/家长查看 | 🔨自研 | 待开发 |

## 模块开发顺序（对应产品规划 P0–P5）

1. `ruoyi-base`（接入 RuoYi，搭权限/组织脚手架）
2. `school-org`（学校组织，所有业务的基础）
3. `question-bank` → `exam-engine`（考试核心）
4. `homework` + `ai-service`（作业 + AI 批改，甜区）
5. `analytics` → `home-school`（学情 + 家校）
6. `gateway`（聚合启动，各阶段均可逐步完善）

## 开发约定

- **新建模块**：复制 `school-org` 作为模板（目录结构/pom/分层），改模块名与职责。严禁凭空建结构。
- **依赖**：版本在根 `pom.xml` 的 `dependencyManagement` 统一管理；模块 `pom.xml` 不写版本号。
- **跨模块调用**：走 `api` 子包接口，不直接依赖他模块 `impl`。
- **数据库迁移**：每个模块的 `src/main/resources/db/migration/` 放 Flyway 脚本，按 `V{yyyyMMdd}__{module}_{desc}.sql` 命名。
- 详见 `../CLAUDE.md` 与 `../docs/开发规范.md`。

## 构建

```bash
mvn clean install          # 全量构建
mvn -pl school-org -am test # 单模块测试（含依赖）
mvn spotless:check          # 格式检查（提交前必过）
```

## 启动

```bash
# 配置好 MySQL/Redis/MinIO/智谱 API Key 后
java -jar gateway/target/gateway.jar
```
