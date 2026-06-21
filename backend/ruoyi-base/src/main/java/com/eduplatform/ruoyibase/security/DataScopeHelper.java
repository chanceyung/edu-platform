package com.eduplatform.ruoyibase.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eduplatform.schoolorg.entity.OrgClass;
import com.eduplatform.schoolorg.mapper.OrgTeacherMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据权限助手。
 * TEACHER 只能看到自己任课班级的数据，ADMIN/校领导看全部。
 * 通过 Service 层手动调用 filterByTeacherRole 实现（非拦截器，更可控）。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataScopeHelper {

    private final OrgTeacherMapper teacherMapper;

    /** 缓存：userId → classIds（5分钟过期，减少查库） */
    private final Map<Long, CacheEntry> classIdCache = new ConcurrentHashMap<>();

    /**
     * 如果当前用户是教师，返回其可访问的班级ID列表。
     * ADMIN/校领导返回 null（=不限制）。
     */
    public List<Long> getCurrentTeacherClassIds() {
        UserContext ctx = UserContext.get();
        if (ctx == null || ctx.getUserId() == null) return List.of(-1L); // 无用户=空集

        String role = ctx.getRoleCode();
        if ("ADMIN".equals(role)) return null; // 管理员不限

        // 教师：查其任课班级
        if ("TEACHER".equals(role)) {
            return getCachedClassIds(ctx.getUserId());
        }

        // 学生：返回自己所在班级
        // TODO: 学生权限待补充
        return List.of(-1L);
    }

    /**
     * 给 LambdaQueryWrapper 加 class_id 过滤（如果有教师权限限制）。
     * 用法：helper.applyClassScope(wrapper, OrgHomework::getClassId);
     */
    public <T> void applyClassScope(LambdaQueryWrapper<T> wrapper,
                                     com.baomidou.mybatisplus.core.toolkit.support.SFunction<T, ?> classIdGetter) {
        List<Long> classIds = getCurrentTeacherClassIds();
        if (classIds != null) {
            if (classIds.isEmpty()) {
                wrapper.eq(classIdGetter, -1L); // 无权限=空结果
            } else if (classIds.size() == 1) {
                wrapper.eq(classIdGetter, classIds.get(0));
            } else {
                wrapper.in(classIdGetter, classIds);
            }
        }
        // null = 不限制（ADMIN）
    }

    private List<Long> getCachedClassIds(Long userId) {
        CacheEntry entry = classIdCache.get(userId);
        if (entry != null && System.currentTimeMillis() - entry.timestamp < 300_000) { // 5min缓存
            return entry.classIds;
        }
        // 查DB
        List<OrgClass> classes = teacherMapper.selectClassesByTeacherId(userId);
        List<Long> ids = classes.stream().map(OrgClass::getId).toList();
        classIdCache.put(userId, new CacheEntry(ids, System.currentTimeMillis()));
        log.debug("教师{}可访问班级: {}", userId, ids);
        return ids;
    }

    /** 清除缓存（教师任课变更时调用） */
    public void evict(Long userId) {
        classIdCache.remove(userId);
    }

    private record CacheEntry(List<Long> classIds, long timestamp) {}
}
