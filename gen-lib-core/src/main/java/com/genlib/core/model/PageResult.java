package com.genlib.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 分页响应结果封装类
 * 
 * @param <T> 数据类型
 * @author Gen-Lib Team
 * @since 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前页码（从1开始）
     */
    private Integer pageNum;

    /**
     * 每页大小
     */
    private Integer pageSize;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 总页数
     */
    private Integer pages;

    /**
     * 当前页数据
     */
    private List<T> records;

    /**
     * 是否有下一页
     */
    private Boolean hasNext;

    /**
     * 是否有上一页
     */
    private Boolean hasPrevious;

    /**
     * 是否为第一页
     */
    private Boolean isFirst;

    /**
     * 是否为最后一页
     */
    private Boolean isLast;

    /**
     * 无参构造方法
     */
    public PageResult() {
    }

    /**
     * 构造方法
     *
     * @param pageNum 页码
     * @param pageSize 页大小
     * @param total 总记录数
     * @param records 当前页数据
     */
    public PageResult(Integer pageNum, Integer pageSize, Long total, List<T> records) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.total = total;
        this.records = records;
        this.calculatePages();
        this.calculateFlags();
    }

    /**
     * 创建分页结果
     *
     * @param pageNum 页码
     * @param pageSize 页大小
     * @param total 总记录数
     * @param records 当前页数据
     * @param <T> 数据类型
     * @return 分页结果
     */
    public static <T> PageResult<T> of(Integer pageNum, Integer pageSize, Long total, List<T> records) {
        return new PageResult<>(pageNum, pageSize, total, records);
    }

    /**
     * 创建分页结果
     *
     * @param pageNum 页码
     * @param pageSize 页大小
     * @param total 总记录数
     * @param records 当前页数据
     * @param <T> 数据类型
     * @return 分页结果
     */
    public static <T> PageResult<T> of(Integer pageNum, Integer pageSize, Long total, Collection<T> records) {
        List<T> recordList = records instanceof List ? (List<T>) records : 
                            records != null ? List.copyOf(records) : Collections.emptyList();
        return new PageResult<>(pageNum, pageSize, total, recordList);
    }

    /**
     * 创建空的分页结果
     *
     * @param pageNum 页码
     * @param pageSize 页大小
     * @param <T> 数据类型
     * @return 空分页结果
     */
    public static <T> PageResult<T> empty(Integer pageNum, Integer pageSize) {
        return new PageResult<>(pageNum, pageSize, 0L, Collections.emptyList());
    }

    /**
     * 创建空的分页结果
     *
     * @param <T> 数据类型
     * @return 空分页结果
     */
    public static <T> PageResult<T> empty() {
        return new PageResult<>(1, 10, 0L, Collections.emptyList());
    }

    /**
     * 计算总页数
     */
    private void calculatePages() {
        if (this.total == null || this.pageSize == null || this.pageSize <= 0) {
            this.pages = 0;
        } else {
            this.pages = (int) Math.ceil((double) this.total / this.pageSize);
        }
    }

    /**
     * 计算分页标志
     */
    private void calculateFlags() {
        if (this.pageNum == null || this.pages == null) {
            this.hasNext = false;
            this.hasPrevious = false;
            this.isFirst = true;
            this.isLast = true;
            return;
        }

        this.isFirst = this.pageNum <= 1;
        this.isLast = this.pageNum >= this.pages;
        this.hasPrevious = this.pageNum > 1;
        this.hasNext = this.pageNum < this.pages;
    }

    /**
     * 转换数据类型
     *
     * @param mapper 转换函数
     * @param <R> 目标数据类型
     * @return 转换后的分页结果
     */
    public <R> PageResult<R> map(java.util.function.Function<T, R> mapper) {
        if (this.records == null || this.records.isEmpty()) {
            return PageResult.empty(this.pageNum, this.pageSize);
        }
        
        List<R> mappedRecords = this.records.stream()
                .map(mapper)
                .toList();
        
        return PageResult.of(this.pageNum, this.pageSize, this.total, mappedRecords);
    }

    /**
     * 判断是否为空
     *
     * @return 是否为空
     */
    public boolean isEmpty() {
        return this.records == null || this.records.isEmpty();
    }

    /**
     * 判断是否不为空
     *
     * @return 是否不为空
     */
    public boolean isNotEmpty() {
        return !isEmpty();
    }

    /**
     * 获取当前页记录数
     *
     * @return 当前页记录数
     */
    public int getSize() {
        return this.records != null ? this.records.size() : 0;
    }

    // ================== Getter 和 Setter ==================

    public Integer getPageNum() {
        return pageNum;
    }

    public PageResult<T> setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
        this.calculateFlags();
        return this;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public PageResult<T> setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
        this.calculatePages();
        this.calculateFlags();
        return this;
    }

    public Long getTotal() {
        return total;
    }

    public PageResult<T> setTotal(Long total) {
        this.total = total;
        this.calculatePages();
        this.calculateFlags();
        return this;
    }

    public Integer getPages() {
        return pages;
    }

    public List<T> getRecords() {
        return records;
    }

    public PageResult<T> setRecords(List<T> records) {
        this.records = records;
        return this;
    }

    public Boolean getHasNext() {
        return hasNext;
    }

    public Boolean getHasPrevious() {
        return hasPrevious;
    }

    public Boolean getIsFirst() {
        return isFirst;
    }

    public Boolean getIsLast() {
        return isLast;
    }

    @Override
    public String toString() {
        return String.format("PageResult{pageNum=%d, pageSize=%d, total=%d, pages=%d, size=%d, hasNext=%s, hasPrevious=%s}", 
                           pageNum, pageSize, total, pages, getSize(), hasNext, hasPrevious);
    }
}