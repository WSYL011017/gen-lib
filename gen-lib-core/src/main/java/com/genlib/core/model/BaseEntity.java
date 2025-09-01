package com.genlib.core.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 基础实体类
 * 包含通用的实体字段，如主键、创建时间、更新时间等
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 乐观锁版本号
     */
    private Integer version;

    /**
     * 逻辑删除标记（0：未删除，1：已删除）
     */
    private Integer deleted;

    /**
     * 无参构造方法
     */
    public BaseEntity() {
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
        this.version = 1;
        this.deleted = 0;
    }

    /**
     * 构造方法
     *
     * @param id 主键ID
     */
    public BaseEntity(Long id) {
        this();
        this.id = id;
    }

    /**
     * 创建时调用，设置创建时间和更新时间
     */
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createTime = now;
        this.updateTime = now;
        if (this.version == null) {
            this.version = 1;
        }
        if (this.deleted == null) {
            this.deleted = 0;
        }
    }

    /**
     * 更新时调用，设置更新时间
     */
    public void onUpdate() {
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 逻辑删除
     */
    public void delete() {
        this.deleted = 1;
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 恢复删除
     */
    public void restore() {
        this.deleted = 0;
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 判断是否已删除
     *
     * @return 是否已删除
     */
    public boolean isDeleted() {
        return this.deleted != null && this.deleted == 1;
    }

    /**
     * 判断是否为新实体（ID为空）
     *
     * @return 是否为新实体
     */
    public boolean isNew() {
        return this.id == null;
    }

    /**
     * 判断是否为持久化实体（ID不为空）
     *
     * @return 是否为持久化实体
     */
    public boolean isPersistent() {
        return this.id != null;
    }

    // ================== Getter 和 Setter ==================

    public Long getId() {
        return id;
    }

    public BaseEntity setId(Long id) {
        this.id = id;
        return this;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public BaseEntity setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
        return this;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public BaseEntity setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public Integer getVersion() {
        return version;
    }

    public BaseEntity setVersion(Integer version) {
        this.version = version;
        return this;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public BaseEntity setDeleted(Integer deleted) {
        this.deleted = deleted;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity that = (BaseEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("%s{id=%d, createTime=%s, updateTime=%s, version=%d, deleted=%d}",
                           getClass().getSimpleName(), id, createTime, updateTime, version, deleted);
    }
}