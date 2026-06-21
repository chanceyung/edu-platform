# school-org — 学校组织模块（⭐ 业务模块开发模板）

> **本模块是所有业务模块的开发模板。** 新建模块（question-bank/exam-engine/homework 等）请**完整复制本模块结构**，改模块名与职责。

## 职责

学校组织：学校 / 年级 / 班级 / 学期 / 学生学籍 / 教师档案 / 任课表 / 家长账号 + 亲子关系。是所有业务的基础（题库/考试/作业都挂在班级学科上）。

> 来源：🔨 自研（学之思只有年级一级，缺学校/班级/教师/家长，必须自建完整组织模型）。

## 标准目录结构（模板，新模块照抄）

```
src/main/java/com/eduplatform/schoolorg/
├── controller/    OrgStudentController.java   # 只做参数校验/转发，禁止业务逻辑
├── service/       OrgStudentService.java      # 业务接口
│   └── impl/      OrgStudentServiceImpl.java  # 业务实现
├── mapper/        OrgStudentMapper.java       # 持久化（MyBatis-Plus）
├── entity/        OrgStudent.java             # 数据库实体（继承 BaseEntity）
├── dto/           OrgStudentSaveDTO.java      # 入参（带校验，待补）
├── vo/            OrgStudentVO.java           # 出参（返回前端）
└── config/        （模块配置，按需）
src/main/resources/
└── db/migration/  V20260620__school_org_init.sql  # Flyway 建表脚本
src/test/java/     单元测试（每个 Service 必须有，待补示范）
```

## 开发该模块的纪律（见 CLAUDE.md）

- 实体必须继承 `BaseEntity`（审计字段/租户/软删由基类统一）。
- Controller 返回 `R<T>`，禁止返回 entity；出参用 VO，入参用 DTO + `@Valid`。
- 数据权限：教师只能查自己班/任课学科的学生（数据权限拦截器，待 ruoyi-base 接入）。
- 表名前缀 `org_`，迁移脚本不可逆操作须评审。
- 每个业务方法先写测试（TDD），核心模块覆盖率 ≥80%。

## 待开发（按优先级）

1. org_school / org_grade / org_class / org_term（组织骨架）
2. org_student / org_teacher（学籍/教师档案）
3. org_teacher_class_subject（任课表）
4. org_parent / org_student_parent（家长 + 亲子）
5. 学生升学/转班（历史班级 org_student_class）
