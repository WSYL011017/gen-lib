package com.genlib.data.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 查询条件包装器
 * 用于构建动态查询条件
 * 
 * @param <T> 实体类型
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public class QueryWrapper<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 查询条件列表
     */
    private final List<QueryCondition> conditions = new ArrayList<>();

    /**
     * 排序条件列表
     */
    private final List<OrderCondition> orders = new ArrayList<>();

    /**
     * 分组字段列表
     */
    private final List<String> groupByFields = new ArrayList<>();

    /**
     * Having条件列表
     */
    private final List<QueryCondition> havingConditions = new ArrayList<>();

    /**
     * 查询字段列表（null表示查询所有字段）
     */
    private List<String> selectFields;

    /**
     * 是否去重
     */
    private boolean distinct = false;

    /**
     * 限制返回记录数
     */
    private Integer limit;

    // ================== 基础条件方法 ==================

    /**
     * 等于条件
     *
     * @param field 字段名
     * @param value 值
     * @return this
     */
    public QueryWrapper<T> eq(String field, Object value) {
        if (value != null) {
            conditions.add(new QueryCondition(field, ConditionType.EQ, value));
        }
        return this;
    }

    /**
     * 不等于条件
     *
     * @param field 字段名
     * @param value 值
     * @return this
     */
    public QueryWrapper<T> ne(String field, Object value) {
        if (value != null) {
            conditions.add(new QueryCondition(field, ConditionType.NE, value));
        }
        return this;
    }

    /**
     * 大于条件
     *
     * @param field 字段名
     * @param value 值
     * @return this
     */
    public QueryWrapper<T> gt(String field, Object value) {
        if (value != null) {
            conditions.add(new QueryCondition(field, ConditionType.GT, value));
        }
        return this;
    }

    /**
     * 大于等于条件
     *
     * @param field 字段名
     * @param value 值
     * @return this
     */
    public QueryWrapper<T> ge(String field, Object value) {
        if (value != null) {
            conditions.add(new QueryCondition(field, ConditionType.GE, value));
        }
        return this;
    }

    /**
     * 小于条件
     *
     * @param field 字段名
     * @param value 值
     * @return this
     */
    public QueryWrapper<T> lt(String field, Object value) {
        if (value != null) {
            conditions.add(new QueryCondition(field, ConditionType.LT, value));
        }
        return this;
    }

    /**
     * 小于等于条件
     *
     * @param field 字段名
     * @param value 值
     * @return this
     */
    public QueryWrapper<T> le(String field, Object value) {
        if (value != null) {
            conditions.add(new QueryCondition(field, ConditionType.LE, value));
        }
        return this;
    }

    /**
     * LIKE条件
     *
     * @param field 字段名
     * @param value 值
     * @return this
     */
    public QueryWrapper<T> like(String field, String value) {
        if (value != null && !value.isEmpty()) {
            conditions.add(new QueryCondition(field, ConditionType.LIKE, "%" + value + "%"));
        }
        return this;
    }

    /**
     * 左LIKE条件
     *
     * @param field 字段名
     * @param value 值
     * @return this
     */
    public QueryWrapper<T> likeLeft(String field, String value) {
        if (value != null && !value.isEmpty()) {
            conditions.add(new QueryCondition(field, ConditionType.LIKE, "%" + value));
        }
        return this;
    }

    /**
     * 右LIKE条件
     *
     * @param field 字段名
     * @param value 值
     * @return this
     */
    public QueryWrapper<T> likeRight(String field, String value) {
        if (value != null && !value.isEmpty()) {
            conditions.add(new QueryCondition(field, ConditionType.LIKE, value + "%"));
        }
        return this;
    }

    /**
     * IN条件
     *
     * @param field 字段名
     * @param values 值集合
     * @return this
     */
    public QueryWrapper<T> in(String field, Collection<?> values) {
        if (values != null && !values.isEmpty()) {
            conditions.add(new QueryCondition(field, ConditionType.IN, values));
        }
        return this;
    }

    /**
     * NOT IN条件
     *
     * @param field 字段名
     * @param values 值集合
     * @return this
     */
    public QueryWrapper<T> notIn(String field, Collection<?> values) {
        if (values != null && !values.isEmpty()) {
            conditions.add(new QueryCondition(field, ConditionType.NOT_IN, values));
        }
        return this;
    }

    /**
     * IS NULL条件
     *
     * @param field 字段名
     * @return this
     */
    public QueryWrapper<T> isNull(String field) {
        conditions.add(new QueryCondition(field, ConditionType.IS_NULL, null));
        return this;
    }

    /**
     * IS NOT NULL条件
     *
     * @param field 字段名
     * @return this
     */
    public QueryWrapper<T> isNotNull(String field) {
        conditions.add(new QueryCondition(field, ConditionType.IS_NOT_NULL, null));
        return this;
    }

    /**
     * BETWEEN条件
     *
     * @param field 字段名
     * @param start 开始值
     * @param end 结束值
     * @return this
     */
    public QueryWrapper<T> between(String field, Object start, Object end) {
        if (start != null && end != null) {
            conditions.add(new QueryCondition(field, ConditionType.BETWEEN, new Object[]{start, end}));
        }
        return this;
    }

    // ================== 排序方法 ==================

    /**
     * 升序排序
     *
     * @param field 字段名
     * @return this
     */
    public QueryWrapper<T> orderByAsc(String field) {
        orders.add(new OrderCondition(field, OrderDirection.ASC));
        return this;
    }

    /**
     * 降序排序
     *
     * @param field 字段名
     * @return this
     */
    public QueryWrapper<T> orderByDesc(String field) {
        orders.add(new OrderCondition(field, OrderDirection.DESC));
        return this;
    }

    /**
     * 多字段排序
     *
     * @param field 字段名
     * @param direction 排序方向
     * @return this
     */
    public QueryWrapper<T> orderBy(String field, OrderDirection direction) {
        orders.add(new OrderCondition(field, direction));
        return this;
    }

    // ================== 其他方法 ==================

    /**
     * 分组
     *
     * @param field 字段名
     * @return this
     */
    public QueryWrapper<T> groupBy(String field) {
        groupByFields.add(field);
        return this;
    }

    /**
     * 指定查询字段
     *
     * @param fields 字段列表
     * @return this
     */
    public QueryWrapper<T> select(String... fields) {
        if (this.selectFields == null) {
            this.selectFields = new ArrayList<>();
        }
        for (String field : fields) {
            this.selectFields.add(field);
        }
        return this;
    }

    /**
     * 去重
     *
     * @return this
     */
    public QueryWrapper<T> distinct() {
        this.distinct = true;
        return this;
    }

    /**
     * 限制返回记录数
     *
     * @param limit 限制数量
     * @return this
     */
    public QueryWrapper<T> limit(int limit) {
        this.limit = limit;
        return this;
    }

    // ================== Getters ==================

    public List<QueryCondition> getConditions() {
        return conditions;
    }

    public List<OrderCondition> getOrders() {
        return orders;
    }

    public List<String> getGroupByFields() {
        return groupByFields;
    }

    public List<QueryCondition> getHavingConditions() {
        return havingConditions;
    }

    public List<String> getSelectFields() {
        return selectFields;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public Integer getLimit() {
        return limit;
    }

    // ================== 内部类 ==================

    /**
     * 查询条件
     */
    public static class QueryCondition implements Serializable {
        private final String field;
        private final ConditionType type;
        private final Object value;

        public QueryCondition(String field, ConditionType type, Object value) {
            this.field = field;
            this.type = type;
            this.value = value;
        }

        public String getField() {
            return field;
        }

        public ConditionType getType() {
            return type;
        }

        public Object getValue() {
            return value;
        }
    }

    /**
     * 排序条件
     */
    public static class OrderCondition implements Serializable {
        private final String field;
        private final OrderDirection direction;

        public OrderCondition(String field, OrderDirection direction) {
            this.field = field;
            this.direction = direction;
        }

        public String getField() {
            return field;
        }

        public OrderDirection getDirection() {
            return direction;
        }
    }

    /**
     * 条件类型枚举
     */
    public enum ConditionType {
        EQ, NE, GT, GE, LT, LE, LIKE, IN, NOT_IN, IS_NULL, IS_NOT_NULL, BETWEEN
    }

    /**
     * 排序方向枚举
     */
    public enum OrderDirection {
        ASC, DESC
    }
}