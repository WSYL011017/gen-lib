package com.genlib.security.auth;

import java.util.Objects;
import java.util.Set;

/**
 * 用户权限信息
 * 封装用户的角色和权限信息
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public class UserPermissions {

    private final String userId;
    private final Set<String> roles;
    private final Set<String> permissions;

    public UserPermissions(String userId, Set<String> roles, Set<String> permissions) {
        this.userId = userId;
        this.roles = Objects.requireNonNull(roles, "roles cannot be null");
        this.permissions = Objects.requireNonNull(permissions, "permissions cannot be null");
    }

    /**
     * 获取用户ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 获取用户角色
     */
    public Set<String> getRoles() {
        return roles;
    }

    /**
     * 获取用户权限
     */
    public Set<String> getPermissions() {
        return permissions;
    }

    /**
     * 检查是否有指定角色
     */
    public boolean hasRole(String role) {
        return roles.contains(role);
    }

    /**
     * 检查是否有指定权限
     */
    public boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }

    /**
     * 检查是否有任一角色
     */
    public boolean hasAnyRole(String... roles) {
        for (String role : roles) {
            if (hasRole(role)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查是否有任一权限
     */
    public boolean hasAnyPermission(String... permissions) {
        for (String permission : permissions) {
            if (hasPermission(permission)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPermissions that = (UserPermissions) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(roles, that.roles) &&
                Objects.equals(permissions, that.permissions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, roles, permissions);
    }

    @Override
    public String toString() {
        return "UserPermissions{" +
                "userId='" + userId + '\'' +
                ", roles=" + roles +
                ", permissions=" + permissions +
                '}';
    }
}