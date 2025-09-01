package com.genlib.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 审计实体类
 * 在基础实体的基础上增加创建人、更新人字段
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class AuditEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 创建人ID
     */
    private Long createBy;

    /**
     * 创建人姓名
     */
    private String createByName;

    /**
     * 更新人ID
     */
    private Long updateBy;

    /**
     * 更新人姓名
     */
    private String updateByName;

    /**
     * 租户ID（多租户场景）
     */
    private String tenantId;

    /**
     * 无参构造方法
     */
    public AuditEntity() {
        super();
    }

    /**
     * 构造方法
     *
     * @param id 主键ID
     */
    public AuditEntity(Long id) {
        super(id);
    }

    /**
     * 设置创建人信息
     *
     * @param createBy 创建人ID
     * @param createByName 创建人姓名
     */
    public void setCreateInfo(Long createBy, String createByName) {
        this.createBy = createBy;
        this.createByName = createByName;
        this.updateBy = createBy;
        this.updateByName = createByName;
    }

    /**
     * 设置更新人信息
     *
     * @param updateBy 更新人ID
     * @param updateByName 更新人姓名
     */
    public void setUpdateInfo(Long updateBy, String updateByName) {
        this.updateBy = updateBy;
        this.updateByName = updateByName;
    }

    /**
     * 创建时调用，设置审计信息
     *
     * @param userId 用户ID
     * @param userName 用户姓名
     */
    public void onCreate(Long userId, String userName) {
        super.onCreate();
        this.createBy = userId;
        this.createByName = userName;
        this.updateBy = userId;
        this.updateByName = userName;
    }

    /**
     * 创建时调用，设置审计信息（包含租户）
     *
     * @param userId 用户ID
     * @param userName 用户姓名
     * @param tenantId 租户ID
     */
    public void onCreate(Long userId, String userName, String tenantId) {
        onCreate(userId, userName);
        this.tenantId = tenantId;
    }

    /**
     * 更新时调用，设置审计信息
     *
     * @param userId 用户ID
     * @param userName 用户姓名
     */
    public void onUpdate(Long userId, String userName) {
        super.onUpdate();
        this.updateBy = userId;
        this.updateByName = userName;
    }

    /**
     * 逻辑删除
     *
     * @param userId 用户ID
     * @param userName 用户姓名
     */
    public void delete(Long userId, String userName) {
        super.delete();
        this.updateBy = userId;
        this.updateByName = userName;
    }

    /**
     * 恢复删除
     *
     * @param userId 用户ID
     * @param userName 用户姓名
     */
    public void restore(Long userId, String userName) {
        super.restore();
        this.updateBy = userId;
        this.updateByName = userName;
    }

    // ================== Getter 和 Setter ==================

    public Long getCreateBy() {
        return createBy;
    }

    public AuditEntity setCreateBy(Long createBy) {
        this.createBy = createBy;
        return this;
    }

    public String getCreateByName() {
        return createByName;
    }

    public AuditEntity setCreateByName(String createByName) {
        this.createByName = createByName;
        return this;
    }

    public Long getUpdateBy() {
        return updateBy;
    }

    public AuditEntity setUpdateBy(Long updateBy) {
        this.updateBy = updateBy;
        return this;
    }

    public String getUpdateByName() {
        return updateByName;
    }

    public AuditEntity setUpdateByName(String updateByName) {
        this.updateByName = updateByName;
        return this;
    }

    public String getTenantId() {
        return tenantId;
    }

    public AuditEntity setTenantId(String tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    @Override
    public String toString() {
        return String.format("%s{id=%d, createBy=%d, createByName='%s', updateBy=%d, updateByName='%s', tenantId='%s'}",
                           getClass().getSimpleName(), getId(), createBy, createByName, updateBy, updateByName, tenantId);
    }
}