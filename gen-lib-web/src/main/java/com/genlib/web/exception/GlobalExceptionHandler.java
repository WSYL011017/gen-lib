package com.genlib.web.exception;

import com.genlib.core.enums.ResultCodeEnum;
import com.genlib.core.exception.BaseException;
import com.genlib.core.exception.BusinessException;
import com.genlib.core.exception.ParamException;
import com.genlib.core.exception.SystemException;
import com.genlib.core.model.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 统一处理各种类型的异常，返回标准化的响应格式
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<Void>> handleBusinessException(BusinessException e, HttpServletRequest request) {
        logger.warn("业务异常: {}, URI: {}", e.getMessage(), request.getRequestURI());
        Result<Void> result = Result.error(e.getCode(), e.getMessage());
        return ResponseEntity.ok(result);
    }

    /**
     * 处理参数异常
     */
    @ExceptionHandler(ParamException.class)
    public ResponseEntity<Result<Void>> handleParamException(ParamException e, HttpServletRequest request) {
        logger.warn("参数异常: {}, URI: {}", e.getMessage(), request.getRequestURI());
        Result<Void> result = Result.error(e.getCode(), e.getMessage());
        return ResponseEntity.badRequest().body(result);
    }

    /**
     * 处理系统异常
     */
    @ExceptionHandler(SystemException.class)
    public ResponseEntity<Result<Void>> handleSystemException(SystemException e, HttpServletRequest request) {
        logger.error("系统异常: {}, URI: {}", e.getMessage(), request.getRequestURI(), e);
        Result<Void> result = Result.error(e.getCode(), "系统内部错误，请联系管理员");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    /**
     * 处理基础异常
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<Result<Void>> handleBaseException(BaseException e, HttpServletRequest request) {
        logger.warn("基础异常: {}, URI: {}", e.getMessage(), request.getRequestURI());
        Result<Void> result = Result.error(e.getCode(), e.getMessage());
        return ResponseEntity.ok(result);
    }

    /**
     * 处理参数验证异常 - @Valid注解验证失败
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<String>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e, HttpServletRequest request) {
        
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        
        logger.warn("参数验证失败: {}, URI: {}", errorMessage, request.getRequestURI());
        Result<String> result = Result.error(ResultCodeEnum.PARAM_INVALID, errorMessage);
        return ResponseEntity.badRequest().body(result);
    }

    /**
     * 处理参数绑定异常
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<Result<String>> handleBindException(BindException e, HttpServletRequest request) {
        String errorMessage = e.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        
        logger.warn("参数绑定失败: {}, URI: {}", errorMessage, request.getRequestURI());
        Result<String> result = Result.error(ResultCodeEnum.PARAM_INVALID, errorMessage);
        return ResponseEntity.badRequest().body(result);
    }

    /**
     * 处理约束验证异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Result<String>> handleConstraintViolationException(
            ConstraintViolationException e, HttpServletRequest request) {
        
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        String errorMessage = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));
        
        logger.warn("约束验证失败: {}, URI: {}", errorMessage, request.getRequestURI());
        Result<String> result = Result.error(ResultCodeEnum.PARAM_INVALID, errorMessage);
        return ResponseEntity.badRequest().body(result);
    }

    /**
     * 处理缺少请求参数异常
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Result<String>> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException e, HttpServletRequest request) {
        
        String errorMessage = "缺少必需的请求参数: " + e.getParameterName();
        logger.warn("缺少请求参数: {}, URI: {}", errorMessage, request.getRequestURI());
        Result<String> result = Result.error(ResultCodeEnum.PARAM_MISSING, errorMessage);
        return ResponseEntity.badRequest().body(result);
    }

    /**
     * 处理参数类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Result<String>> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        
        String errorMessage = String.format("参数类型不匹配: %s，期望类型: %s", 
                e.getName(), e.getRequiredType().getSimpleName());
        logger.warn("参数类型不匹配: {}, URI: {}", errorMessage, request.getRequestURI());
        Result<String> result = Result.error(ResultCodeEnum.PARAM_INVALID, errorMessage);
        return ResponseEntity.badRequest().body(result);
    }

    /**
     * 处理请求方法不支持异常
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Result<String>> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        
        String errorMessage = "不支持的请求方法: " + e.getMethod();
        logger.warn("请求方法不支持: {}, URI: {}", errorMessage, request.getRequestURI());
        Result<String> result = Result.error(ResultCodeEnum.METHOD_NOT_ALLOWED, errorMessage);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(result);
    }

    /**
     * 处理404异常
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Result<String>> handleNoHandlerFoundException(
            NoHandlerFoundException e, HttpServletRequest request) {
        
        String errorMessage = "请求的资源不存在: " + e.getRequestURL();
        logger.warn("资源不存在: {}, URI: {}", errorMessage, request.getRequestURI());
        Result<String> result = Result.error(ResultCodeEnum.NOT_FOUND, errorMessage);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
    }

    /**
     * 处理非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Result<String>> handleIllegalArgumentException(
            IllegalArgumentException e, HttpServletRequest request) {
        
        logger.warn("非法参数: {}, URI: {}", e.getMessage(), request.getRequestURI());
        Result<String> result = Result.error(ResultCodeEnum.PARAM_INVALID, e.getMessage());
        return ResponseEntity.badRequest().body(result);
    }

    /**
     * 处理空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Result<String>> handleNullPointerException(
            NullPointerException e, HttpServletRequest request) {
        
        logger.error("空指针异常, URI: {}", request.getRequestURI(), e);
        Result<String> result = Result.error(ResultCodeEnum.SYSTEM_ERROR, "系统内部错误");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Result<String>> handleRuntimeException(
            RuntimeException e, HttpServletRequest request) {
        
        logger.error("运行时异常: {}, URI: {}", e.getMessage(), request.getRequestURI(), e);
        Result<String> result = Result.error(ResultCodeEnum.SYSTEM_ERROR, "系统内部错误");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    /**
     * 处理其他异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<String>> handleException(Exception e, HttpServletRequest request) {
        logger.error("未知异常: {}, URI: {}", e.getMessage(), request.getRequestURI(), e);
        Result<String> result = Result.error(ResultCodeEnum.SYSTEM_ERROR, "系统内部错误");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }
}