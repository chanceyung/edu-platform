package com.eduplatform.ruoyibase.controller;

import com.eduplatform.common.exception.BizException;
import com.eduplatform.common.response.ErrorCode;
import com.eduplatform.common.response.R;
import com.eduplatform.ruoyibase.entity.SysUser;
import com.eduplatform.ruoyibase.mapper.SysUserMapper;
import com.eduplatform.ruoyibase.security.JwtTokenProvider;
import com.eduplatform.ruoyibase.security.RedisTokenStore;
import com.eduplatform.ruoyibase.security.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 认证接口（登录/当前用户/登出）。
 */
@Tag(name = "认证")
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SysUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final RedisTokenStore tokenStore;

    @Operation(summary = "登录（5次失败锁定5分钟）")
    @PostMapping("/login")
    public R<Map<String, Object>> login(@RequestBody LoginRequest req) {
        // 检查锁定
        if (tokenStore.isLocked(req.getUsername())) {
            throw new BizException(ErrorCode.ACCOUNT_LOCKED);
        }
        SysUser user = userMapper.selectOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, req.getUsername()));
        if (user == null || !passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            long fails = tokenStore.recordLoginFailure(req.getUsername());
            throw new BizException(ErrorCode.LOGIN_FAILED, "剩余" + (5 - fails) + "次");
        }
        if (user.getStatus() != null && user.getStatus() == 2) {
            throw new BizException(ErrorCode.ACCOUNT_LOCKED);
        }
        tokenStore.clearLoginFailure(req.getUsername());
        List<String> roles = userMapper.selectRoleCodesByUserId(user.getId());
        String token = tokenProvider.generate(user.getId(), user.getUsername(), user.getTenantId(), roles);
        return R.ok(Map.of(
                "token", token,
                "userId", user.getId(),
                "username", user.getUsername(),
                "realName", user.getRealName() != null ? user.getRealName() : user.getUsername(),
                "roles", roles
        ));
    }

    @Operation(summary = "当前用户信息")
    @GetMapping("/me")
    public R<Map<String, Object>> me() {
        UserContext ctx = UserContext.get();
        if (ctx == null || ctx.getUserId() == null) {
            throw new BizException(ErrorCode.UNAUTHORIZED);
        }
        return R.ok(Map.of(
                "userId", ctx.getUserId(),
                "username", ctx.getUsername() != null ? ctx.getUsername() : "",
                "tenantId", ctx.getTenantId() != null ? ctx.getTenantId() : "",
                "roleCode", ctx.getRoleCode() != null ? ctx.getRoleCode() : ""
        ));
    }

    @Operation(summary = "登出（token加入黑名单立即失效）")
    @PostMapping("/logout")
    public R<Void> logout(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            tokenStore.blacklist(bearer.substring(7));
        }
        return R.ok();
    }

    @lombok.Data
    public static class LoginRequest {
        @NotBlank(message = "用户名不能为空")
        private String username;
        @NotBlank(message = "密码不能为空")
        private String password;
    }
}
