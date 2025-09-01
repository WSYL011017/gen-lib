package com.genlib.business.domain;

import com.genlib.core.exception.BusinessException;
import com.genlib.core.enums.ResultCodeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 领域服务基类
 * 提供领域服务的基础功能和规范
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public abstract class BaseDomainService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 执行业务操作（带异常处理）
     *
     * @param operation 业务操作
     * @param errorMessage 错误消息
     * @param <T> 返回类型
     * @return 操作结果
     */
    protected <T> T executeWithErrorHandler(BusinessOperation<T> operation, String errorMessage) {
        try {
            return operation.execute();
        } catch (BusinessException e) {
            logger.warn("业务操作失败: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("业务操作异常: {}", errorMessage, e);
            throw new BusinessException(ResultCodeEnum.BUSINESS_ERROR, errorMessage);
        }
    }

    /**
     * 执行业务操作（无返回值）
     *
     * @param operation 业务操作
     * @param errorMessage 错误消息
     */
    protected void executeWithErrorHandler(VoidBusinessOperation operation, String errorMessage) {
        try {
            operation.execute();
        } catch (BusinessException e) {
            logger.warn("业务操作失败: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("业务操作异常: {}", errorMessage, e);
            throw new BusinessException(ResultCodeEnum.BUSINESS_ERROR, errorMessage);
        }
    }

    /**
     * 验证业务规则
     *
     * @param condition 条件
     * @param errorMessage 错误消息
     */
    protected void validateBusinessRule(boolean condition, String errorMessage) {
        if (!condition) {
            throw new BusinessException(ResultCodeEnum.BUSINESS_ERROR, errorMessage);
        }
    }

    /**
     * 验证业务规则
     *
     * @param condition 条件
     * @param resultCode 错误码
     * @param errorMessage 错误消息
     */
    protected void validateBusinessRule(boolean condition, ResultCodeEnum resultCode, String errorMessage) {
        if (!condition) {
            throw new BusinessException(resultCode, errorMessage);
        }
    }

    /**
     * 验证对象不为空
     *
     * @param object 对象
     * @param fieldName 字段名
     */
    protected void validateNotNull(Object object, String fieldName) {
        if (object == null) {
            throw new BusinessException(ResultCodeEnum.PARAM_INVALID, fieldName + "不能为空");
        }
    }

    /**
     * 验证字符串不为空
     *
     * @param value 值
     * @param fieldName 字段名
     */
    protected void validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new BusinessException(ResultCodeEnum.PARAM_INVALID, fieldName + "不能为空");
        }
    }

    /**
     * 记录业务日志
     *
     * @param action 操作名称
     * @param description 描述
     */
    protected void logBusinessAction(String action, String description) {
        logger.info("业务操作: {} - {}", action, description);
    }

    /**
     * 记录业务日志（带参数）
     *
     * @param action 操作名称
     * @param description 描述
     * @param params 参数
     */
    protected void logBusinessAction(String action, String description, Object... params) {
        logger.info("业务操作: {} - {} - 参数: {}", action, description, params);
    }

    /**
     * 业务操作接口（有返回值）
     */
    @FunctionalInterface
    protected interface BusinessOperation<T> {
        T execute() throws Exception;
    }

    /**
     * 业务操作接口（无返回值）
     */
    @FunctionalInterface
    protected interface VoidBusinessOperation {
        void execute() throws Exception;
    }
}