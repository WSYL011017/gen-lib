package com.genlib.web.validation;

import com.genlib.core.exception.ParamException;
import com.genlib.core.enums.ResultCodeEnum;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Collection;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 参数验证工具类
 * 提供常用的参数验证方法
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
public class ValidationUtils {

    private static final Validator validator;
    
    // 邮箱正则表达式
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    
    // 手机号正则表达式（中国大陆）
    private static final Pattern MOBILE_PATTERN = Pattern.compile(
        "^1[3-9]\\d{9}$"
    );
    
    // 身份证号正则表达式
    private static final Pattern ID_CARD_PATTERN = Pattern.compile(
        "(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)"
    );

    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    /**
     * 验证对象
     */
    public static <T> void validate(T object) {
        if (object == null) {
            throw new ParamException(ResultCodeEnum.PARAM_INVALID, "验证对象不能为空");
        }
        
        Set<ConstraintViolation<T>> violations = validator.validate(object);
        if (!violations.isEmpty()) {
            StringBuilder message = new StringBuilder();
            for (ConstraintViolation<T> violation : violations) {
                if (message.length() > 0) {
                    message.append("; ");
                }
                message.append(violation.getMessage());
            }
            throw new ParamException(ResultCodeEnum.PARAM_INVALID, message.toString());
        }
    }

    /**
     * 验证对象（指定分组）
     */
    public static <T> void validate(T object, Class<?>... groups) {
        if (object == null) {
            throw new ParamException(ResultCodeEnum.PARAM_INVALID, "验证对象不能为空");
        }
        
        Set<ConstraintViolation<T>> violations = validator.validate(object, groups);
        if (!violations.isEmpty()) {
            StringBuilder message = new StringBuilder();
            for (ConstraintViolation<T> violation : violations) {
                if (message.length() > 0) {
                    message.append("; ");
                }
                message.append(violation.getMessage());
            }
            throw new ParamException(ResultCodeEnum.PARAM_INVALID, message.toString());
        }
    }

    /**
     * 验证字符串不为空
     */
    public static void notEmpty(String value, String fieldName) {
        if (!StringUtils.hasText(value)) {
            throw new ParamException(ResultCodeEnum.PARAM_INVALID, fieldName + "不能为空");
        }
    }

    /**
     * 验证对象不为空
     */
    public static void notNull(Object value, String fieldName) {
        if (value == null) {
            throw new ParamException(ResultCodeEnum.PARAM_INVALID, fieldName + "不能为空");
        }
    }

    /**
     * 验证集合不为空
     */
    public static void notEmpty(Collection<?> collection, String fieldName) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new ParamException(ResultCodeEnum.PARAM_INVALID, fieldName + "不能为空");
        }
    }

    /**
     * 验证数组不为空
     */
    public static void notEmpty(Object[] array, String fieldName) {
        if (array == null || array.length == 0) {
            throw new ParamException(ResultCodeEnum.PARAM_INVALID, fieldName + "不能为空");
        }
    }

    /**
     * 验证字符串长度
     */
    public static void length(String value, int min, int max, String fieldName) {
        if (value == null) {
            return;
        }
        int length = value.length();
        if (length < min || length > max) {
            throw new ParamException(ResultCodeEnum.PARAM_INVALID, 
                fieldName + "长度必须在" + min + "-" + max + "个字符之间");
        }
    }

    /**
     * 验证字符串最小长度
     */
    public static void minLength(String value, int min, String fieldName) {
        if (value != null && value.length() < min) {
            throw new ParamException(ResultCodeEnum.PARAM_INVALID, 
                fieldName + "长度不能少于" + min + "个字符");
        }
    }

    /**
     * 验证字符串最大长度
     */
    public static void maxLength(String value, int max, String fieldName) {
        if (value != null && value.length() > max) {
            throw new ParamException(ResultCodeEnum.PARAM_INVALID, 
                fieldName + "长度不能超过" + max + "个字符");
        }
    }

    /**
     * 验证数字范围
     */
    public static void range(Number value, Number min, Number max, String fieldName) {
        if (value == null) {
            return;
        }
        double doubleValue = value.doubleValue();
        double minValue = min.doubleValue();
        double maxValue = max.doubleValue();
        
        if (doubleValue < minValue || doubleValue > maxValue) {
            throw new ParamException(ResultCodeEnum.PARAM_INVALID, 
                fieldName + "必须在" + min + "-" + max + "之间");
        }
    }

    /**
     * 验证最小值
     */
    public static void min(Number value, Number min, String fieldName) {
        if (value != null && value.doubleValue() < min.doubleValue()) {
            throw new ParamException(ResultCodeEnum.PARAM_INVALID, 
                fieldName + "不能小于" + min);
        }
    }

    /**
     * 验证最大值
     */
    public static void max(Number value, Number max, String fieldName) {
        if (value != null && value.doubleValue() > max.doubleValue()) {
            throw new ParamException(ResultCodeEnum.PARAM_INVALID, 
                fieldName + "不能大于" + max);
        }
    }

    /**
     * 验证正数
     */
    public static void positive(Number value, String fieldName) {
        if (value != null && value.doubleValue() <= 0) {
            throw new ParamException(ResultCodeEnum.PARAM_INVALID, fieldName + "必须为正数");
        }
    }

    /**
     * 验证非负数
     */
    public static void nonNegative(Number value, String fieldName) {
        if (value != null && value.doubleValue() < 0) {
            throw new ParamException(ResultCodeEnum.PARAM_INVALID, fieldName + "不能为负数");
        }
    }

    /**
     * 验证邮箱格式
     */
    public static void email(String email, String fieldName) {
        if (StringUtils.hasText(email) && !EMAIL_PATTERN.matcher(email).matches()) {
            throw new ParamException(ResultCodeEnum.PARAM_INVALID, fieldName + "格式不正确");
        }
    }

    /**
     * 验证手机号格式
     */
    public static void mobile(String mobile, String fieldName) {
        if (StringUtils.hasText(mobile) && !MOBILE_PATTERN.matcher(mobile).matches()) {
            throw new ParamException(ResultCodeEnum.PARAM_INVALID, fieldName + "格式不正确");
        }
    }

    /**
     * 验证身份证号格式
     */
    public static void idCard(String idCard, String fieldName) {
        if (StringUtils.hasText(idCard) && !ID_CARD_PATTERN.matcher(idCard).matches()) {
            throw new ParamException(ResultCodeEnum.PARAM_INVALID, fieldName + "格式不正确");
        }
    }

    /**
     * 验证正则表达式
     */
    public static void pattern(String value, String regex, String fieldName) {
        if (StringUtils.hasText(value) && !Pattern.matches(regex, value)) {
            throw new ParamException(ResultCodeEnum.PARAM_INVALID, fieldName + "格式不正确");
        }
    }

    /**
     * 验证正则表达式
     */
    public static void pattern(String value, Pattern pattern, String fieldName) {
        if (StringUtils.hasText(value) && !pattern.matcher(value).matches()) {
            throw new ParamException(ResultCodeEnum.PARAM_INVALID, fieldName + "格式不正确");
        }
    }

    /**
     * 验证布尔值
     */
    public static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new ParamException(ResultCodeEnum.PARAM_INVALID, message);
        }
    }

    /**
     * 验证布尔值
     */
    public static void assertFalse(boolean condition, String message) {
        if (condition) {
            throw new ParamException(ResultCodeEnum.PARAM_INVALID, message);
        }
    }

    /**
     * 验证包含关系
     */
    public static void contains(Collection<?> collection, Object value, String fieldName) {
        if (collection != null && !collection.contains(value)) {
            throw new ParamException(ResultCodeEnum.PARAM_INVALID, 
                fieldName + "必须为指定值之一");
        }
    }

    /**
     * 验证不包含关系
     */
    public static void notContains(Collection<?> collection, Object value, String fieldName) {
        if (collection != null && collection.contains(value)) {
            throw new ParamException(ResultCodeEnum.PARAM_INVALID, 
                fieldName + "不能为指定值");
        }
    }
}