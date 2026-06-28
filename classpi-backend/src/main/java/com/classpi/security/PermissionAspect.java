package com.classpi.security;

import com.classpi.entity.User;
import com.classpi.service.UserService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Aspect
@Component
public class PermissionAspect {

    @Autowired
    private UserService userService;  // ✅ 注入 UserService

    @Before("@annotation(requireRole)")
    public void checkRole(JoinPoint joinPoint, RequireRole requireRole) {
        System.out.println("========== PermissionAspect.checkRole ==========");

        // 从请求头获取 userId
        String userId = getUserIdFromHeader();
        System.out.println("从请求头获取 userId: " + userId);

        if (userId == null) {
            System.out.println("❌ userId 为 null，未登录");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录或登录已过期");
        }

        // 获取用户身份
        String userIdentity = getUserIdentity(joinPoint, userId);
        System.out.println("获取到的 userIdentity: " + userIdentity);

        if (userIdentity == null) {
            System.out.println("❌ userIdentity 为 null");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录或登录已过期");
        }

        System.out.println("需要的角色: " + Arrays.toString(requireRole.value()));
        System.out.println("用户身份: " + userIdentity);

        boolean hasPermission = Arrays.stream(requireRole.value())
                .anyMatch(role -> role.equalsIgnoreCase(userIdentity));

        System.out.println("是否有权限: " + hasPermission);

        if (!hasPermission) {
            System.out.println("❌ 权限不足");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "权限不足，无法访问该资源");
        }

        System.out.println("✅ 权限验证通过");
    }

    @Before("@annotation(requireOwner)")
    public void checkOwner(JoinPoint joinPoint, RequireOwner requireOwner) {
        String userId = getUserIdFromHeader();
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录或登录已过期");
        }

        String userIdentity = getUserIdentity(joinPoint, userId);
        if ("teacher".equalsIgnoreCase(userIdentity)) {
            return;
        }

        String resourceOwnerId = getResourceOwnerId(joinPoint, requireOwner.paramName());
        if (resourceOwnerId != null && !resourceOwnerId.equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权操作他人的资源");
        }
    }

    private String getUserIdFromHeader() {
        try {
            RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                System.out.println("attributes 为 null");
                return null;
            }
            HttpServletRequest request = ((ServletRequestAttributes) attributes).getRequest();
            String userId = request.getHeader("userId");
            System.out.println("请求头 userId: " + userId);
            return userId;
        } catch (Exception e) {
            System.out.println("获取 userId 异常: " + e.getMessage());
            return null;
        }
    }

    private String getUserIdentity(JoinPoint joinPoint, String userId) {
        System.out.println("========== getUserIdentity ==========");

        // 1. 先从请求头获取 identity
        try {
            RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = ((ServletRequestAttributes) attributes).getRequest();
                String identity = request.getHeader("identity");
                System.out.println("请求头 identity: " + identity);
                if (identity != null && !identity.isEmpty()) {
                    return identity;
                }
            }
        } catch (Exception e) {
            System.out.println("获取请求头 identity 异常: " + e.getMessage());
        }

        // 2. 从方法参数获取 identity
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Object[] args = joinPoint.getArgs();
        String[] paramNames = signature.getParameterNames();

        System.out.println("方法参数名: " + Arrays.toString(paramNames));
        System.out.println("方法参数值: " + Arrays.toString(args));

        for (int i = 0; i < paramNames.length; i++) {
            if ("identity".equalsIgnoreCase(paramNames[i])) {
                if (args[i] != null) {
                    System.out.println("从参数获取 identity: " + args[i].toString());
                    return args[i].toString();
                }
            }
        }

        // 3. 通过 userId 查数据库获取 identity
        System.out.println("请求头和参数都没有 identity，通过 userId 查数据库");
        try {
            Long id = Long.valueOf(userId);
            User user = userService.getById(id);
            if (user != null) {
                String identity = user.getIdentity();
                System.out.println("从数据库获取 identity: " + identity);
                return identity;
            }
        } catch (Exception e) {
            System.out.println("查数据库失败: " + e.getMessage());
        }

        System.out.println("❌ 没有找到 identity");
        return null;
    }

    private String getResourceOwnerId(JoinPoint joinPoint, String paramName) {
        Object[] args = joinPoint.getArgs();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();

        for (int i = 0; i < paramNames.length; i++) {
            if (paramName.equalsIgnoreCase(paramNames[i])) {
                if (args[i] != null) {
                    return args[i].toString();
                }
            }
        }
        return null;
    }
}