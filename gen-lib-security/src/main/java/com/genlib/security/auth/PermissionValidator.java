package com.genlib.security.auth;

import com.genlib.core.exception.BusinessException;
import com.genlib.core.enums.ResultCodeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 权限验证器
 * 提供权限验证和管理功能
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public class PermissionValidator {

    private static final Logger logger = LoggerFactory.getLogger(PermissionValidator.class);

    private final PermissionProvider permissionProvider;
    private final Set<String> superAdminRoles;

    public PermissionValidator(PermissionProvider permissionProvider) {
        this.permissionProvider = permissionProvider;
        this.superAdminRoles = ConcurrentHashMap.newKeySet();
        // 默认超级管理员角色
        this.superAdminRoles.add("SUPER_ADMIN");
        this.superAdminRoles.add("ROOT");
    }

    /**
     * 验证用户是否有指定权限
     *
     * @param userId 用户ID
     * @param permission 权限代码
     * @return 是否有权限
     */
    public boolean hasPermission(String userId, String permission) {
        try {
            // 获取用户权限
            UserPermissions userPermissions = permissionProvider.getUserPermissions(userId);
            if (userPermissions == null) {
                logger.warn("用户[{}]权限信息不存在", userId);
                return false;
            }

            // 检查是否为超级管理员
            if (isSuperAdmin(userPermissions)) {
                logger.debug("用户[{}]是超级管理员，拥有所有权限", userId);
                return true;
            }

            // 检查直接权限
            if (userPermissions.getPermissions().contains(permission)) {
                logger.debug("用户[{}]拥有直接权限[{}]", userId, permission);
                return true;
            }

            // 检查角色权限
            for (String role : userPermissions.getRoles()) {
                Set<String> rolePermissions = permissionProvider.getRolePermissions(role);
                if (rolePermissions != null && rolePermissions.contains(permission)) {
                    logger.debug("用户[{}]通过角色[{}]拥有权限[{}]", userId, role, permission);
                    return true;
                }
            }

            logger.debug("用户[{}]没有权限[{}]", userId, permission);
            return false;

        } catch (Exception e) {
            logger.error("权限验证失败: userId={}, permission={}", userId, permission, e);
            return false;
        }
    }

    /**
     * 验证用户是否有任一权限
     *
     * @param userId 用户ID
     * @param permissions 权限代码集合
     * @return 是否有任一权限
     */
    public boolean hasAnyPermission(String userId, String... permissions) {
        for (String permission : permissions) {
            if (hasPermission(userId, permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 验证用户是否有所有权限
     *
     * @param userId 用户ID
     * @param permissions 权限代码集合
     * @return 是否有所有权限
     */
    public boolean hasAllPermissions(String userId, String... permissions) {
        for (String permission : permissions) {
            if (!hasPermission(userId, permission)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 验证用户是否有指定角色
     *
     * @param userId 用户ID
     * @param role 角色代码
     * @return 是否有角色
     */
    public boolean hasRole(String userId, String role) {
        try {
            UserPermissions userPermissions = permissionProvider.getUserPermissions(userId);
            if (userPermissions == null) {
                logger.warn("用户[{}]权限信息不存在", userId);
                return false;
            }

            boolean hasRole = userPermissions.getRoles().contains(role);
            logger.debug("用户[{}] {} 角色[{}]", userId, hasRole ? "拥有" : "没有", role);
            return hasRole;

        } catch (Exception e) {
            logger.error("角色验证失败: userId={}, role={}", userId, role, e);
            return false;
        }
    }

    /**
     * 验证用户是否有任一角色
     *
     * @param userId 用户ID
     * @param roles 角色代码集合
     * @return 是否有任一角色
     */
    public boolean hasAnyRole(String userId, String... roles) {
        for (String role : roles) {
            if (hasRole(userId, role)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 验证用户是否有所有角色
     *
     * @param userId 用户ID
     * @param roles 角色代码集合
     * @return 是否有所有角色
     */
    public boolean hasAllRoles(String userId, String... roles) {
        for (String role : roles) {
            if (!hasRole(userId, role)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查权限（没有权限则抛出异常）
     *
     * @param userId 用户ID
     * @param permission 权限代码
     */
    public void checkPermission(String userId, String permission) {
        if (!hasPermission(userId, permission)) {
            throw new BusinessException(ResultCodeEnum.ACCESS_DENIED, 
                "用户没有权限: " + permission);
        }
    }

    /**
     * 检查权限（没有权限则抛出异常，自定义错误消息）
     *
     * @param userId 用户ID
     * @param permission 权限代码
     * @param errorMessage 错误消息
     */
    public void checkPermission(String userId, String permission, String errorMessage) {
        if (!hasPermission(userId, permission)) {
            throw new BusinessException(ResultCodeEnum.ACCESS_DENIED, errorMessage);
        }
    }

    /**
     * 检查角色（没有角色则抛出异常）
     *
     * @param userId 用户ID
     * @param role 角色代码
     */
    public void checkRole(String userId, String role) {
        if (!hasRole(userId, role)) {
            throw new BusinessException(ResultCodeEnum.ACCESS_DENIED, 
                "用户没有角色: " + role);
        }
    }

    /**
     * 检查角色（没有角色则抛出异常，自定义错误消息）
     *
     * @param userId 用户ID
     * @param role 角色代码
     * @param errorMessage 错误消息
     */
    public void checkRole(String userId, String role, String errorMessage) {
        if (!hasRole(userId, role)) {
            throw new BusinessException(ResultCodeEnum.ACCESS_DENIED, errorMessage);
        }
    }

    /**
     * 检查是否为超级管理员
     *
     * @param userPermissions 用户权限
     * @return 是否为超级管理员
     */
    private boolean isSuperAdmin(UserPermissions userPermissions) {
        for (String role : userPermissions.getRoles()) {
            if (superAdminRoles.contains(role)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 添加超级管理员角色
     *
     * @param role 角色代码
     */
    public void addSuperAdminRole(String role) {
        superAdminRoles.add(role);
        logger.info("添加超级管理员角色: {}", role);
    }

    /**
     * 移除超级管理员角色
     *
     * @param role 角色代码
     */
    public void removeSuperAdminRole(String role) {
        superAdminRoles.remove(role);
        logger.info("移除超级管理员角色: {}", role);
    }

    /**
     * 获取所有超级管理员角色
     *
     * @return 超级管理员角色集合
     */
    public Set<String> getSuperAdminRoles() {
        return Set.copyOf(superAdminRoles);
    }
}