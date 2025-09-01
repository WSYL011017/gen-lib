package com.genlib.samples.entity;

import com.genlib.core.model.AuditEntity;

import java.math.BigDecimal;

/**
 * 用户实体示例
 * 演示如何使用Gen-Lib的基础实体类
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public class User extends AuditEntity {

    /**
     * 用户名
     */
    private String username;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 余额
     */
    private BigDecimal balance;

    /**
     * 状态：1-正常，0-禁用
     */
    private Integer status;

    // ================== 构造函数 ==================

    public User() {
        super();
    }

    public User(String username, String email) {
        super();
        this.username = username;
        this.email = email;
        this.status = 1; // 默认正常状态
        this.balance = BigDecimal.ZERO; // 默认余额为0
    }

    // ================== Getters and Setters ==================

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    // ================== 业务方法 ==================

    /**
     * 检查用户是否激活
     *
     * @return 是否激活
     */
    public boolean isActive() {
        return status != null && status == 1;
    }

    /**
     * 增加余额
     *
     * @param amount 金额
     */
    public void addBalance(BigDecimal amount) {
        if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0) {
            if (this.balance == null) {
                this.balance = BigDecimal.ZERO;
            }
            this.balance = this.balance.add(amount);
        }
    }

    /**
     * 扣减余额
     *
     * @param amount 金额
     * @return 是否成功
     */
    public boolean deductBalance(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        
        if (this.balance == null) {
            this.balance = BigDecimal.ZERO;
        }
        
        if (this.balance.compareTo(amount) >= 0) {
            this.balance = this.balance.subtract(amount);
            return true;
        }
        
        return false;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", mobile='" + mobile + '\'' +
                ", realName='" + realName + '\'' +
                ", age=" + age +
                ", balance=" + balance +
                ", status=" + status +
                ", createTime=" + getCreateTime() +
                ", updateTime=" + getUpdateTime() +
                '}';
    }
}