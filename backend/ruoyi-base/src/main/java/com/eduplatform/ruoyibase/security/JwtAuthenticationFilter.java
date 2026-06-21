package com.eduplatform.ruoyibase.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT 认证过滤器。从 Header 提取 token → 解析 → 设 SecurityContext + UserContext。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final RedisTokenStore tokenStore;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String token = resolveToken(request);
        if (token != null && tokenProvider.validate(token) && !tokenStore.isBlacklisted(token)) {
            try {
                Claims claims = tokenProvider.parse(token);
                String username = claims.get("username", String.class);
                String tenantId = claims.get("tenantId", String.class);
                Long userId = Long.parseLong(claims.getSubject());
                @SuppressWarnings("unchecked")
                List<String> roles = claims.get("roles", List.class);

                // 设 UserContext（供 MetaObjectHandler 用）
                UserContext ctx = new UserContext();
                ctx.setUserId(userId);
                ctx.setUsername(username);
                ctx.setTenantId(tenantId);
                if (roles != null && !roles.isEmpty()) ctx.setRoleCode(roles.get(0));
                UserContext.set(ctx);

                // 设 SecurityContext
                List<SimpleGrantedAuthority> authorities = roles != null
                        ? roles.stream().map(r -> new SimpleGrantedAuthority("ROLE_" + r)).collect(Collectors.toList())
                        : List.of();
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception e) {
                log.warn("JWT 解析失败: {}", e.getMessage());
            }
        }
        try {
            chain.doFilter(request, response);
        } finally {
            UserContext.clear();
        }
    }

    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}
