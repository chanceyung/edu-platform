package com.eduplatform.ruoyibase.security;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * JWT 黑名单（logout 后旧 token 立即失效）+ 登录失败锁定。
 * 用 StringRedisTemplate 轻量实现，无需额外序列化配置。
 */
@Component
@RequiredArgsConstructor
public class RedisTokenStore {

    private final StringRedisTemplate redis;

    private static final String KEY_BLACKLIST = "jwt:blacklist:";
    private static final String KEY_LOGIN_FAIL = "login:fail:";
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final Duration LOCK_DURATION = Duration.ofMinutes(5);
    private static final Duration TOKEN_TTL = Duration.ofHours(25);

    /** logout 时加入黑名单 */
    public void blacklist(String token) {
        redis.opsForValue().set(KEY_BLACKLIST + token, "1", TOKEN_TTL);
    }

    /** 检查是否在黑名单 */
    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(redis.hasKey(KEY_BLACKLIST + token));
    }

    /** 记录登录失败次数，返回当前失败次数 */
    public long recordLoginFailure(String username) {
        String key = KEY_LOGIN_FAIL + username;
        Long count = redis.opsForValue().increment(key);
        if (count != null && count == 1) {
            redis.expire(key, LOCK_DURATION);
        }
        return count != null ? count : 0;
    }

    /** 登录成功清除失败计数 */
    public void clearLoginFailure(String username) {
        redis.delete(KEY_LOGIN_FAIL + username);
    }

    /** 是否被锁定（超过最大失败次数） */
    public boolean isLocked(String username) {
        String key = KEY_LOGIN_FAIL + username;
        String val = redis.opsForValue().get(key);
        if (val == null) return false;
        try {
            return Long.parseLong(val) >= MAX_LOGIN_ATTEMPTS;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
