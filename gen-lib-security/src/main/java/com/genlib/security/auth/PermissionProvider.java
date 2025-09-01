package com.genlib.security.auth;

import java.util.Set;

/**
 * 权限提供者接口
 * 定义权限数据获取的标准接口
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public interface PermissionProvider {

    /**
     * 获取用户权限信息
     *
     * @param userId 用户ID
     * @return 用户权限信息
     */
    UserPermissions getUserPermissions(String userId);

    /**
     * 获取角色权限
     *
     * @param role 角色代码
     * @return 权限集合
     */
    Set<String> getRolePermissions(String role);

    /**
     * 刷新用户权限缓存
     *
     * @param userId 用户ID
     */
    default void refreshUserPermissions(String userId) {
        // 默认实现为空，子类可以重写
    }

    /**
     * 刷新角色权限缓存
     *
     * @param role 角色代码
     */
    default void refreshRolePermissions(String role) {
        // 默认实现为空，子类可以重写
    }

    /**
     * 清除所有权限缓存
     */
    default void clearAllCache() {
        // 默认实现为空，子类可以重写
    }
}