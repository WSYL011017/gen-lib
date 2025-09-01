package com.genlib.data.repository;

import com.genlib.core.model.PageResult;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * 通用Repository接口
 * 定义了基础的CRUD操作
 * 
 * @param <T> 实体类型
 * @param <ID> 主键类型
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public interface BaseRepository<T, ID extends Serializable> {

    // ================== 查询操作 ==================

    /**
     * 根据ID查询实体
     *
     * @param id 主键
     * @return 实体Optional
     */
    Optional<T> findById(ID id);

    /**
     * 查询所有实体
     *
     * @return 实体列表
     */
    List<T> findAll();

    /**
     * 根据ID列表查询实体
     *
     * @param ids 主键列表
     * @return 实体列表
     */
    List<T> findAllById(Collection<ID> ids);

    /**
     * 统计总数
     *
     * @return 总数
     */
    long count();

    /**
     * 判断实体是否存在
     *
     * @param id 主键
     * @return 是否存在
     */
    boolean existsById(ID id);

    // ================== 保存操作 ==================

    /**
     * 保存实体
     *
     * @param entity 实体
     * @return 保存后的实体
     */
    <S extends T> S save(S entity);

    /**
     * 批量保存实体
     *
     * @param entities 实体列表
     * @return 保存后的实体列表
     */
    <S extends T> List<S> saveAll(Iterable<S> entities);

    /**
     * 保存或更新实体
     *
     * @param entity 实体
     * @return 保存后的实体
     */
    <S extends T> S saveOrUpdate(S entity);

    // ================== 删除操作 ==================

    /**
     * 根据ID删除实体
     *
     * @param id 主键
     */
    void deleteById(ID id);

    /**
     * 删除实体
     *
     * @param entity 实体
     */
    void delete(T entity);

    /**
     * 批量删除实体
     *
     * @param entities 实体列表
     */
    void deleteAll(Iterable<? extends T> entities);

    /**
     * 根据ID列表删除实体
     *
     * @param ids 主键列表
     */
    void deleteAllById(Collection<ID> ids);

    /**
     * 删除所有实体
     */
    void deleteAll();

    // ================== 分页查询 ==================

    /**
     * 分页查询
     *
     * @param pageNum 页码（从1开始）
     * @param pageSize 页大小
     * @return 分页结果
     */
    PageResult<T> findPage(int pageNum, int pageSize);

    /**
     * 分页查询（带排序）
     *
     * @param pageNum 页码（从1开始）
     * @param pageSize 页大小
     * @param sortBy 排序字段
     * @param sortDir 排序方向（ASC/DESC）
     * @return 分页结果
     */
    PageResult<T> findPage(int pageNum, int pageSize, String sortBy, String sortDir);

    // ================== 批量操作 ==================

    /**
     * 批量插入
     *
     * @param entities 实体列表
     * @return 插入数量
     */
    int batchInsert(Collection<T> entities);

    /**
     * 批量更新
     *
     * @param entities 实体列表
     * @return 更新数量
     */
    int batchUpdate(Collection<T> entities);

    /**
     * 批量删除
     *
     * @param ids 主键列表
     * @return 删除数量
     */
    int batchDelete(Collection<ID> ids);

    // ================== 条件查询 ==================

    /**
     * 根据条件查询
     *
     * @param queryWrapper 查询条件包装器
     * @return 实体列表
     */
    List<T> findByCondition(QueryWrapper<T> queryWrapper);

    /**
     * 根据条件查询单个实体
     *
     * @param queryWrapper 查询条件包装器
     * @return 实体Optional
     */
    Optional<T> findOneByCondition(QueryWrapper<T> queryWrapper);

    /**
     * 根据条件统计数量
     *
     * @param queryWrapper 查询条件包装器
     * @return 数量
     */
    long countByCondition(QueryWrapper<T> queryWrapper);

    /**
     * 根据条件分页查询
     *
     * @param queryWrapper 查询条件包装器
     * @param pageNum 页码（从1开始）
     * @param pageSize 页大小
     * @return 分页结果
     */
    PageResult<T> findPageByCondition(QueryWrapper<T> queryWrapper, int pageNum, int pageSize);
}