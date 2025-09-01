package com.genlib.core;

import com.genlib.core.constants.CommonConstants;
import com.genlib.core.enums.ResultCodeEnum;
import com.genlib.core.exception.BusinessException;
import com.genlib.core.model.PageResult;
import com.genlib.core.model.Result;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 核心模块测试类
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
class CoreModuleTest {

    @Test
    void testConstants() {
        assertEquals("0000", CommonConstants.SUCCESS_CODE);
        assertEquals("操作成功", CommonConstants.SUCCESS_MESSAGE);
        assertEquals(10, CommonConstants.DEFAULT_PAGE_SIZE);
        assertNotNull(CommonConstants.DEFAULT_CHARSET);
    }

    @Test
    void testResultCodeEnum() {
        ResultCodeEnum success = ResultCodeEnum.SUCCESS;
        assertEquals("0000", success.getCode());
        assertEquals("操作成功", success.getMessage());
        assertTrue(success.isSuccess());

        ResultCodeEnum error = ResultCodeEnum.BUSINESS_ERROR;
        assertEquals("3000", error.getCode());
        assertFalse(error.isSuccess());

        ResultCodeEnum found = ResultCodeEnum.getByCode("0000");
        assertEquals(ResultCodeEnum.SUCCESS, found);
    }

    @Test
    void testResult() {
        // 测试成功响应
        Result<String> successResult = Result.success("测试数据");
        assertTrue(successResult.isSuccess());
        assertFalse(successResult.isError());
        assertEquals("0000", successResult.getCode());
        assertEquals("测试数据", successResult.getData());

        // 测试错误响应
        Result<String> errorResult = Result.error("测试错误");
        assertFalse(errorResult.isSuccess());
        assertTrue(errorResult.isError());
        assertEquals("9999", errorResult.getCode());

        // 测试枚举响应
        Result<String> enumResult = Result.error(ResultCodeEnum.PARAM_ERROR);
        assertEquals("1000", enumResult.getCode());
        assertEquals("参数错误", enumResult.getMessage());
    }

    @Test
    void testPageResult() {
        // 测试分页结果
        List<String> data = Arrays.asList("item1", "item2", "item3");
        PageResult<String> pageResult = PageResult.of(1, 10, 100L, data);

        assertEquals(1, pageResult.getPageNum());
        assertEquals(10, pageResult.getPageSize());
        assertEquals(100L, pageResult.getTotal());
        assertEquals(10, pageResult.getPages());
        assertEquals(3, pageResult.getSize());
        assertTrue(pageResult.getIsFirst());
        assertFalse(pageResult.getIsLast());
        assertFalse(pageResult.getHasPrevious());
        assertTrue(pageResult.getHasNext());

        // 测试空分页
        PageResult<String> emptyResult = PageResult.empty();
        assertTrue(emptyResult.isEmpty());
        assertEquals(0, emptyResult.getSize());

        // 测试数据转换
        PageResult<Integer> mappedResult = pageResult.map(String::length);
        assertEquals(3, mappedResult.getSize());
        assertEquals(5, mappedResult.getRecords().get(0)); // "item1".length()
    }

    @Test
    void testBusinessException() {
        // 测试基本异常
        BusinessException ex1 = new BusinessException("测试异常");
        assertEquals("测试异常", ex1.getMessage());

        // 测试枚举异常
        BusinessException ex2 = new BusinessException(ResultCodeEnum.DATA_NOT_FOUND);
        assertEquals("3001", ex2.getCode());
        assertEquals("数据不存在", ex2.getMessage());

        // 测试静态创建方法
        BusinessException ex3 = BusinessException.of("静态创建异常");
        assertEquals("静态创建异常", ex3.getMessage());

        // 测试断言方法
        assertThrows(BusinessException.class, () -> {
            BusinessException.assertTrue(false, "条件不满足");
        });

        assertThrows(BusinessException.class, () -> {
            BusinessException.assertNotNull(null, "对象不能为空");
        });

        assertThrows(BusinessException.class, () -> {
            BusinessException.assertNotEmpty("", "字符串不能为空");
        });

        // 测试正常情况不抛异常
        assertDoesNotThrow(() -> {
            BusinessException.assertTrue(true, "条件满足");
            BusinessException.assertNotNull("not null", "对象不为空");
            BusinessException.assertNotEmpty("not empty", "字符串不为空");
        });
    }
}