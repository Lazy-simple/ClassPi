package com.classpi.security;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;

@Aspect
@Component
public class PermissionAspect {

    @Before("@annotation(requireRole)")
    public void checkRole(JoinPoint joinPoint, RequireRole requireRole) {
        String userRole = getUserRole(joinPoint);
        String userId = getUserId(joinPoint);

        if (userRole == null || userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录或登录已过期");
        }

        boolean hasPermission = Arrays.stream(requireRole.value())
                .anyMatch(role -> role.equalsIgnoreCase(userRole));

        if (!hasPermission) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "权限不足，无法访问该资源");
        }
    }

    @Before("@annotation(requireOwner)")
    public void checkOwner(JoinPoint joinPoint, RequireOwner requireOwner) {
        String userRole = getUserRole(joinPoint);
        String userId = getUserId(joinPoint);

        if (userRole == null || userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录或登录已过期");
        }

        if ("teacher".equalsIgnoreCase(userRole)) {
            return;
        }

        String resourceOwnerId = getResourceOwnerId(joinPoint, requireOwner.paramName());
        if (resourceOwnerId != null && !resourceOwnerId.equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权操作他人的资源");
        }
    }

    private String getUserRole(JoinPoint joinPoint) {
        String role = getHeader("identity");
        if (role != null) {
            return role;
        }
        role = getHeader("role");
        if (role != null) {
            return role;
        }

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Object[] args = joinPoint.getArgs();
        String[] paramNames = signature.getParameterNames();

        for (int i = 0; i < paramNames.length; i++) {
            if ("identity".equalsIgnoreCase(paramNames[i]) || "role".equalsIgnoreCase(paramNames[i])) {
                if (args[i] != null) {
                    return args[i].toString();
                }
            }
        }
        return null;
    }

    private String getUserId(JoinPoint joinPoint) {
        String userId = getHeader("userId");
        if (userId != null) {
            return userId;
        }

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Object[] args = joinPoint.getArgs();
        String[] paramNames = signature.getParameterNames();

        for (int i = 0; i < paramNames.length; i++) {
            if ("userId".equalsIgnoreCase(paramNames[i]) || "authorId".equalsIgnoreCase(paramNames[i])
                    || "uploaderId".equalsIgnoreCase(paramNames[i]) || "studentId".equalsIgnoreCase(paramNames[i])
                    || "teacherId".equalsIgnoreCase(paramNames[i])) {
                if (args[i] != null) {
                    return args[i].toString();
                }
            }
        }
        return null;
    }

    private String getHeader(String name) {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                return request.getHeader(name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
