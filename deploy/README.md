# deploy — 部署

## 本地开发 / 单校私有化（docker-compose）

```bash
# 1. 准备环境变量（复制示例并填写，尤其 ZHIPU_API_KEY）
cp .env.example .env   # 待补 .env.example
# 2. 启动基础设施 + 后端
docker compose up -d
# 3. 查看日志
docker compose logs -f backend
```

服务端口：
- MySQL 3306 ｜ Redis 6379 ｜ MinIO 9000(API)/9001(控制台) ｜ 后端 8080

## 多校 SaaS（K8s）

`k8s/` 目录待补：含 namespace / configmap / 各服务 deployment+service / ingress / hpa。

## 部署纪律（见 CLAUDE.md）

- **凭据全部用环境变量**，`.env` 不入库（已在 .gitignore）。
- 生产配置 `application-prod.yml` 不入库，用配置中心或 K8s Secret 注入。
- 数据库变更走 Flyway 迁移，启动自动执行，禁止手改库。
- 监控：Prometheus 采集 + 关键指标告警（考试并发/AI 调用失败率/慢查询）。
- 灰度：新版本先小流量灰度，观察后全量。

## 待补

- [ ] `.env.example`
- [ ] `backend/Dockerfile`
- [ ] 前端各端 Dockerfile + nginx 配置
- [ ] `k8s/` 编排
- [ ] Prometheus / Grafana 监控配置
