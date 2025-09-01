package com.genlib.data.mybatis.plus;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.genlib.core.model.PageResult;
import com.genlib.data.repository.BaseRepository;
import com.genlib.data.repository.QueryWrapper;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * MyBatis-Plus Repository实现
 * 
 * @param <T> 实体类型
 * @param <ID> 主键类型
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public class MybatisPlusRepository<T, ID extends Serializable> implements BaseRepository<T, ID> {

    protected final BaseMapper<T> baseMapper;

    public MybatisPlusRepository(BaseMapper<T> baseMapper) {
        this.baseMapper = baseMapper;
    }

    @Override
    public Optional<T> findById(ID id) {
        T entity = baseMapper.selectById(id);
        return Optional.ofNullable(entity);
    }

    @Override
    public List<T> findAll() {
        return baseMapper.selectList(null);
    }

    @Override
    public List<T> findAllById(Collection<ID> ids) {
        return baseMapper.selectBatchIds(ids);
    }

    @Override
    public long count() {
        Long count = baseMapper.selectCount(null);
        return count != null ? count : 0L;
    }

    @Override
    public boolean existsById(ID id) {
        return baseMapper.selectById(id) != null;
    }

    @Override
    public <S extends T> S save(S entity) {
        if (isEntityNew(entity)) {
            baseMapper.insert(entity);
        } else {
            baseMapper.updateById(entity);
        }
        return entity;
    }

    @Override
    public <S extends T> List<S> saveAll(Iterable<S> entities) {
        if (entities == null) {
            return List.of();
        }
        
        entities.forEach(this::save);
        return (List<S>) entities;
    }

    @Override
    public <S extends T> S saveOrUpdate(S entity) {
        return save(entity);
    }

    @Override
    public void deleteById(ID id) {
        baseMapper.deleteById(id);
    }

    @Override
    public void delete(T entity) {
        if (entity != null) {
            baseMapper.delete(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<T>().eq("id", getEntityId(entity)));
        }
    }

    @Override
    public void deleteAll(Iterable<? extends T> entities) {
        if (entities != null) {
            entities.forEach(this::delete);
        }
    }

    @Override
    public void deleteAllById(Collection<ID> ids) {
        if (ids != null && !ids.isEmpty()) {
            baseMapper.deleteBatchIds(ids);
        }
    }

    @Override
    public void deleteAll() {
        baseMapper.delete(null);
    }

    @Override
    public PageResult<T> findPage(int pageNum, int pageSize) {
        Page<T> page = new Page<>(pageNum, pageSize);
        IPage<T> result = baseMapper.selectPage(page, null);
        
        return PageResult.of(
            (int) result.getCurrent(),
            (int) result.getSize(),
            result.getTotal(),
            result.getRecords()
        );
    }

    @Override
    public PageResult<T> findPage(int pageNum, int pageSize, String sortBy, String sortDir) {
        Page<T> page = new Page<>(pageNum, pageSize);
        
        // 添加排序
        if (sortBy != null && !sortBy.isEmpty()) {
            if ("DESC".equalsIgnoreCase(sortDir)) {
                page.addOrder(com.baomidou.mybatisplus.core.metadata.OrderItem.desc(sortBy));
            } else {
                page.addOrder(com.baomidou.mybatisplus.core.metadata.OrderItem.asc(sortBy));
            }
        }
        
        IPage<T> result = baseMapper.selectPage(page, null);
        
        return PageResult.of(
            (int) result.getCurrent(),
            (int) result.getSize(),
            result.getTotal(),
            result.getRecords()
        );
    }

    @Override
    public int batchInsert(Collection<T> entities) {
        if (entities == null || entities.isEmpty()) {
            return 0;
        }
        
        int count = 0;
        for (T entity : entities) {
            count += baseMapper.insert(entity);
        }
        return count;
    }

    @Override
    public int batchUpdate(Collection<T> entities) {
        if (entities == null || entities.isEmpty()) {
            return 0;
        }
        
        int count = 0;
        for (T entity : entities) {
            count += baseMapper.updateById(entity);
        }
        return count;
    }

    @Override
    public int batchDelete(Collection<ID> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        
        return baseMapper.deleteBatchIds(ids);
    }

    @Override
    public List<T> findByCondition(QueryWrapper<T> queryWrapper) {
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<T> mpWrapper = convertQueryWrapper(queryWrapper);
        return baseMapper.selectList(mpWrapper);
    }

    @Override
    public Optional<T> findOneByCondition(QueryWrapper<T> queryWrapper) {
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<T> mpWrapper = convertQueryWrapper(queryWrapper);
        mpWrapper.last("LIMIT 1");
        T entity = baseMapper.selectOne(mpWrapper);
        return Optional.ofNullable(entity);
    }

    @Override
    public long countByCondition(QueryWrapper<T> queryWrapper) {
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<T> mpWrapper = convertQueryWrapper(queryWrapper);
        Long count = baseMapper.selectCount(mpWrapper);
        return count != null ? count : 0L;
    }

    @Override
    public PageResult<T> findPageByCondition(QueryWrapper<T> queryWrapper, int pageNum, int pageSize) {
        Page<T> page = new Page<>(pageNum, pageSize);
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<T> mpWrapper = convertQueryWrapper(queryWrapper);
        
        IPage<T> result = baseMapper.selectPage(page, mpWrapper);
        
        return PageResult.of(
            (int) result.getCurrent(),
            (int) result.getSize(),
            result.getTotal(),
            result.getRecords()
        );
    }

    /**
     * 转换查询条件包装器
     *
     * @param queryWrapper 通用查询包装器
     * @return MyBatis-Plus查询包装器
     */
    protected com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<T> convertQueryWrapper(QueryWrapper<T> queryWrapper) {
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<T> mpWrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        
        if (queryWrapper == null) {
            return mpWrapper;
        }
        
        // 转换查询条件
        for (QueryWrapper.QueryCondition condition : queryWrapper.getConditions()) {
            String field = condition.getField();
            Object value = condition.getValue();
            
            switch (condition.getType()) {
                case EQ:
                    mpWrapper.eq(field, value);
                    break;
                case NE:
                    mpWrapper.ne(field, value);
                    break;
                case GT:
                    mpWrapper.gt(field, value);
                    break;
                case GE:
                    mpWrapper.ge(field, value);
                    break;
                case LT:
                    mpWrapper.lt(field, value);
                    break;
                case LE:
                    mpWrapper.le(field, value);
                    break;
                case LIKE:
                    mpWrapper.like(field, value);
                    break;
                case IN:
                    if (value instanceof Collection) {
                        mpWrapper.in(field, (Collection<?>) value);
                    }
                    break;
                case NOT_IN:
                    if (value instanceof Collection) {
                        mpWrapper.notIn(field, (Collection<?>) value);
                    }
                    break;
                case IS_NULL:
                    mpWrapper.isNull(field);
                    break;
                case IS_NOT_NULL:
                    mpWrapper.isNotNull(field);
                    break;
                case BETWEEN:
                    if (value instanceof Object[]) {
                        Object[] values = (Object[]) value;
                        if (values.length >= 2) {
                            mpWrapper.between(field, values[0], values[1]);
                        }
                    }
                    break;
            }
        }
        
        // 转换排序条件
        for (QueryWrapper.OrderCondition order : queryWrapper.getOrders()) {
            if (order.getDirection() == QueryWrapper.OrderDirection.ASC) {
                mpWrapper.orderByAsc(order.getField());
            } else {
                mpWrapper.orderByDesc(order.getField());
            }
        }
        
        // 处理查询字段
        if (queryWrapper.getSelectFields() != null && !queryWrapper.getSelectFields().isEmpty()) {
            mpWrapper.select(queryWrapper.getSelectFields().toArray(new String[0]));
        }
        
        // 处理分组
        if (!queryWrapper.getGroupByFields().isEmpty()) {
            String[] groupByFields = queryWrapper.getGroupByFields().toArray(new String[0]);
            if (groupByFields.length > 0) {
                String firstField = groupByFields[0];
                String[] remainingFields = new String[groupByFields.length - 1];
                System.arraycopy(groupByFields, 1, remainingFields, 0, groupByFields.length - 1);
                mpWrapper.groupBy(firstField, remainingFields);
            }
        }
        
        // 处理限制
        if (queryWrapper.getLimit() != null) {
            mpWrapper.last("LIMIT " + queryWrapper.getLimit());
        }
        
        return mpWrapper;
    }

    /**
     * 判断实体是否为新实体
     *
     * @param entity 实体
     * @return 是否为新实体
     */
    protected boolean isEntityNew(T entity) {
        // 这里可以根据实际情况实现判断逻辑
        // 比如检查ID是否为空
        try {
            Object id = getEntityId(entity);
            return id == null;
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 获取实体ID
     *
     * @param entity 实体
     * @return 实体ID
     */
    protected Object getEntityId(T entity) {
        // 这里需要通过反射或其他方式获取实体ID
        // 简化实现，实际应用中需要根据具体情况处理
        try {
            return entity.getClass().getMethod("getId").invoke(entity);
        } catch (Exception e) {
            return null;
        }
    }
}