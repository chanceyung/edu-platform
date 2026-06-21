package com.eduplatform.aiservice.rag;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 知识库种子数据初始化器。
 * 启动时自动灌入：sg-exam架构知识 + xzs设计精华 + 初中知识点 + 最佳实践。
 * 来源：sg-exam-eval（Apache-2.0）+ xzs-mysql-master（AGPL-3.0，仅参考设计，代码自研）。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KbSeeder {

    private final KbService kbService;

    @PostConstruct
    public void seed() {
        // 检查是否已有数据（避免重复灌入）
        try {
            var results = kbService.search("知识库初始化检测", 1);
            if (!results.isEmpty()) {
                log.info("RAG知识库已有数据，跳过种子灌入");
                return;
            }
        } catch (Exception e) {
            log.warn("检测知识库失败，继续灌入: {}", e.getMessage());
        }

        log.info("=== 开始灌入 RAG 知识库种子数据 ===");
        int count = 0;

        // ===== 1. sg-exam 架构知识（Apache-2.0，参考设计）=====
        count += ingest("sg-exam多租户表设计", "TEMPLATE", "Architecture", 0,
            "sg-exam-eval采用多租户SaaS架构，所有业务表强制包含九字段审计范式：id主键 + creator创建人 + create_time + operator修改人 + update_time(ON UPDATE CURRENT_TIMESTAMP) + is_deleted(tinyint逻辑删除0正常1删除) + tenant_code(varchar(16)租户隔离)。所有租户数据通过tenant_code字段软隔离，无需物理分库。核心业务域划分为：课程域(course→chapter→section→knowledge_point)、题库域(subject_category分类树+六种题型表+option选项子表)、考试域(examination+examination_subject+examination_record+answer)。");

        count += ingest("sg-exam六种题型设计", "TEMPLATE", "Architecture", 0,
            "sg-exam定义六种题型枚举：CHOICES(0单选)、SHORT_ANSWER(1简答)、JUDGEMENT(2判断)、MULTIPLE_CHOICES(3多选)、FILL_BLANK(4填空)、MATERIAL(5材料)。枚举绑定service和handler两个Class，运行时通过matchByValue直接拿到判分策略类，实现题型到策略的零if-else分发。所有题型表共享字段骨架：subject_name(题干富文本varchar 5000)、answer(参考答案)、score(分值int)、analysis(解析)、level(难度1-5)、category_id(分类)。特色：每张题型表内置音视频支持(speech_id/speech_url/speech_play_limit/subject_video_id)，同一张表可承载文本题、语音题、视频题。选择题用choices_type区分单选/多选，简答题有judge_type(0自动/1人工判分)。");

        count += ingest("sg-exam判分策略模式", "TEMPLATE", "Architecture", 0,
            "sg-exam判分子系统采用策略+模板方法+工厂三层设计。IAnswerHandler接口→AbstractAnswerHandler模板基类→6个具体Handler。模板方法handle()定义统一流程：批量加载题目→转Map提速→遍历答案→检查isAutoJudge→调用具体judge()→累加AtomicDouble totalScore→返回Result(score,correctNum,inCorrectNum,hasHumanJudgeSubject)。各题型判分策略：单选用equalsIgnoreCase精确比较；多选用CollectionUtils.retainAll比较集合(顺序无关)；填空以\\n分隔多空逐空比较支持部分得分；判断和简答用eq()整体比较。当hasHumanJudgeSubject为true时考试记录停在SUBMITTED待批改状态。");

        count += ingest("sg-exam考试三段式生命周期", "TEMPLATE", "Architecture", 0,
            "sg-exam考试流程三段式：(1)组卷阶段—exam_examination主表承载考试元数据(name/type/start_time/end_time/total_score)，控制字段answer_type(0展示全部/1逐题)、show_subject_type(0顺序/1随机出题)、max_exam_cnt(最大考试次数)、exam_duration_minute(时长)、access_type(0全部用户/1指定/2部门)。题目通过exam_examination_subject关联表挂载。(2)答题阶段—学生进入生成exam_examination_record(user_id+examination_id+start_time+submit_status)，逐题写入exam_answer(answer/score/mark_status)。(3)提交阶段—submit()触发判分Handler工厂，更新record的score/correct_number/submit_status。含简答题则停留在SUBMITTED等MarkAnswerService人工批改。");

        // ===== 2. xzs 设计精华（AGPL-3.0，只参考设计，代码自研）=====
        count += ingest("xzs富文本外置中心表设计", "TEMPLATE", "Architecture", 0,
            "学之思xzs最核心的设计精华是富文本外置中心表t_text_content(id, content text)。所有大型内容(题目题干、试卷frame JSON、答题内容)都不直接存在业务表，而是写入text_content取得id，业务表只存*_text_content_id外键。优点：业务表保持窄表利于索引；富文本大字段不污染热点行；统一管理版本和审计。这与sg-exam把HTML直接内嵌subject_name的设计形成对比——xzs外置更利于全文检索和缓存，sg-exam内嵌更利于单次查询。");

        count += ingest("xzs千分制分值设计", "TEMPLATE", "Architecture", 0,
            "学之思xzs在所有score字段统一采用千分制——实际分值乘以10后以int存储。例如10分题目存100，1.5分题目存15。工具方法ExamUtil.scoreToVM：若score%10==0返回score/10字符串(100→10)，否则返回String.format(%.1f, score/10.0)(15→1.5)。优点：用int替代decimal/float避免浮点精度问题，索引和聚合(sum/avg)效率更高；千分制(×10)刚好支持一位小数(0.5分制)；DB层完全整数运算，展示层再转换。适用于金额、评分、权重等业务小数场景。");

        count += ingest("xzs答卷双层模型", "TEMPLATE", "Architecture", 0,
            "学之思xzs采用经典答卷双层模型。头部表t_exam_paper_answer记录整卷级数据：system_score(系统自动判分)、user_score(最终得分含人工批改)、paper_score(试卷总分)、question_correct(答对题数)、do_time(用时秒)、status(1待批改/2完成)。明细表t_exam_paper_question_customer_answer记录每题：customer_score(本题得分)、question_score(本题满分)、answer(用户答案)、do_right(是否答对)。判分流程：systemScore=sum(customerScore)、questionCorrect=count(customerScore==questionScore)、needJudge=anyMatch(含填空或简答)，纯客观题直接Complete(2)，含主观题WaitJudge(1)等待人工judge。");

        count += ingest("xzs消息双表群发设计", "TEMPLATE", "Architecture", 0,
            "学之思xzs站内信采用消息主体+收件人明细双表设计。主表t_message存title/content/send_user_id/send_user_name/receive_user_count/read_count。明细表t_message_user为每个收件人生成一行：message_id/receive_user_id/readed(bit是否已读)/read_time。优点：群发高效(1行主表+N行明细，content不重复存储)；已读状态独立追踪(明细表readed+read_time避免主表更新冲突)；主表冗余receive_user_count/read_count避免COUNT聚合(展示已读5/10)。适用于任何一对多分发+独立状态追踪场景。");

        // ===== 3. 初中数学知识点体系（基于课标2022版）=====
        count += ingest("初中数学-实数体系", "STANDARD", "Math", 7,
            "初一数学实数体系：有理数(正数/负数/零)→无理数(无限不循环小数)→实数。核心知识点：有理数的加减乘除乘方运算、绝对值、相反数、倒数、科学记数法、近似数与有效数字。重点：有理数混合运算顺序(先乘方后乘除再加减，括号优先)、数轴表示与比较大小。难点：负数参与运算的符号判定、绝对值方程与不等式。");

        count += ingest("初中数学-代数式与方程", "STANDARD", "Math", 7,
            "初一初二代数式与方程体系：整式(单项式/多项式)→整式加减乘除→因式分解(提公因式/公式法/十字相乘)→分式→二次根式。一元一次方程(ax+b=0)→二元一次方程组→一元二次方程(ax²+bx+c=0,求根公式/韦达定理/判别式)→分式方程→无理方程。核心方法：配方法、换元法、消元法。实际应用：行程问题/工程问题/利润问题/浓度问题的方程建模。");

        count += ingest("初中数学-函数体系", "STANDARD", "Math", 8,
            "初二初三函数体系：平面直角坐标系→一次函数(y=kx+b,正比例函数y=kx)→反比例函数(y=k/x)→二次函数(y=ax²+bx+c)。核心知识点：函数的三种表示(解析式/列表/图像)、定义域值域、增减性、与坐标轴交点。一次函数图像是直线,k和b的几何意义；反比例函数图像是双曲线,关于原点对称；二次函数图像是抛物线,顶点/对称轴/开口方向由a/b/c决定。函数与方程不等式的关系：二次函数与x轴交点即对应一元二次方程的根。");

        count += ingest("初中数学-几何体系", "STANDARD", "Math", 8,
            "初二初三几何体系：线段/角→相交线与平行线→三角形(内角和/全等/相似/特殊三角形)→勾股定理(a²+b²=c²)→四边形(平行四边形/矩形/菱形/正方形/梯形)→圆(圆心角/圆周角/切线/弧长/扇形面积)→图形变换(平移/旋转/轴对称/位似)。证明方法：全等三角形(SSS/SAS/ASA/AAS/HL)、相似三角形(对应边成比例)。重点：勾股定理及逆定理的应用、圆的切线判定与性质、图形面积计算。");

        count += ingest("初中数学-统计与概率", "STANDARD", "Math", 9,
            "初三统计与概率：数据收集与整理→数据代表(平均数/中位数/众数)→数据波动(极差/方差/标准差)→频数分布直方图→概率(古典概型/列举法/树状图/列表法)。核心：用样本估计总体、概率的计算(等可能事件)、互斥事件与独立事件。重点应用：调查问卷设计、统计图表分析、游戏公平性判断。");

        // ===== 4. 教学方法论 =====
        count += ingest("PBL项目式学习教学法", "TEMPLATE", "Methodology", 0,
            "项目式学习(PBL)在初中教学中的应用：以真实问题为驱动，学生通过探究完成项目来学习知识。教案结构：项目主题(真实情境)→学习目标(知识/技能/素养三维)→课前任务(微课/导学案)→课堂活动(分组探究/展示/互评)→评价(过程性+成果性)→课后拓展。结合翻转课堂：课前学生自学基础概念(视频+导学案)，课堂时间用于深度探究和协作。适合AI辅助：AI可生成项目情境、自动批改导学案、辅助分组匹配、实时学情反馈。");

        count += ingest("AI辅助批改的最佳实践", "TEMPLATE", "Methodology", 0,
            "AI辅助批改作业的最佳实践（基于ai_task_log信任内核）：(1)客观题(选择/判断)用规则自动判分，无需AI；(2)主观题(简答/作文)调GLM评分，prompt包含：作业要求+学生答案+评分标准+满分值，要求返回JSON{score,comment}；(3)评分后写入ai_task_log(task_type=GRADING, biz_ref_id=submission_id)，教师可在审核区查看AI评分并批准/驳回；(4)低置信度(score<5/10)自动标记需人工复核；(5)批量批改时单学生失败不阻断全班，汇总报告返回total/success/failed。关键：AI评分必须可溯源(ai_task_log)、可回滚(驳回触发回滚)、教师有最终决定权。");

        log.info("=== RAG 知识库种子灌入完成：{} 篇文档 ===", count);
    }

    private int ingest(String title, String sourceType, String subject, int grade, String content) {
        try {
            kbService.ingest(title, sourceType, subject, grade > 0 ? grade : null, content);
            return 1;
        } catch (Exception e) {
            log.warn("灌入失败 {}: {}", title, e.getMessage());
            return 0;
        }
    }
}
