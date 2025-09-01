package com.genlib.integration;

import com.genlib.cache.core.CacheManager;
import com.genlib.config.core.ConfigManager;
import com.genlib.core.enums.ResultCodeEnum;
import com.genlib.core.model.Result;
import com.genlib.utils.text.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Gen-Lib 基础集成测试（不依赖Docker）
 * 测试核心功能的基本集成效果
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = IntegrationTestApplication.class
)
@ActiveProfiles("test")
public class GenLibBasicIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired(required = false)
    private ConfigManager configManager;

    @Autowired(required = false)
    private CacheManager cacheManager;

    @Test
    public void testApplicationStartup() {
        // 验证应用能够正常启动
        assertThat(port).isGreaterThan(0);
    }

    @Test
    public void testCoreComponentsIntegration() {
        // 测试核心工具类
        String result = StringUtils.toCamelCase("hello_world", "_");
        assertThat(result).isEqualTo("helloWorld");

        // 测试响应结果
        Result<String> successResult = Result.success("test data");
        assertThat(successResult.isSuccess()).isTrue();
        assertThat(successResult.getData()).isEqualTo("test data");

        Result<Void> errorResult = Result.error(ResultCodeEnum.PARAM_INVALID);
        assertThat(errorResult.isSuccess()).isFalse();
        assertThat(errorResult.getCode()).isEqualTo(ResultCodeEnum.PARAM_INVALID.getCode());
    }

    @Test
    public void testConfigManagerIntegration() {
        if (configManager != null) {
            // 测试配置管理
            String appName = configManager.getObject("spring.application.name", String.class, "default-app");
            assertThat(appName).isNotNull();
        }
    }

    @Test
    public void testCacheManagerIntegration() {
        if (cacheManager != null) {
            // 测试缓存管理
            var cacheOptional = cacheManager.getCache("testCache");
            
            if (cacheOptional.isPresent()) {
                var cache = cacheOptional.get();
                // 测试缓存操作
                cache.put("key1", "value1");
                assertThat(cache.get("key1")).isPresent();
                assertThat(cache.get("key1").orElse(null)).isEqualTo("value1");
                
                // 测试缓存删除
                cache.evict("key1");
                assertThat(cache.get("key1")).isEmpty();
            }
        }
    }

    @Test
    public void testWebEndpointsIntegration() {
        // 测试健康检查端点
        ResponseEntity<String> response = restTemplate.getForEntity(
            "http://localhost:" + port + "/actuator/health", String.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void testPerformanceIntegration() {
        // 性能测试 - 批量操作
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < 100; i++) {
            String result = StringUtils.toCamelCase("test_string_" + i, "_");
            assertThat(result).startsWith("testString");
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // 验证性能要求（100次操作应该在100ms内完成）
        assertThat(duration).isLessThan(100);
    }

    @Test
    public void testStringUtilsComprehensive() {
        // 测试字符串工具类的各种方法
        assertThat(StringUtils.isEmpty(null)).isTrue();
        assertThat(StringUtils.isEmpty("")).isTrue();
        assertThat(StringUtils.isEmpty("  ")).isFalse();
        
        assertThat(StringUtils.isBlank(null)).isTrue();
        assertThat(StringUtils.isBlank("")).isTrue();
        assertThat(StringUtils.isBlank("  ")).isTrue();
        
        assertThat(StringUtils.toPascalCase("hello_world", "_")).isEqualTo("HelloWorld");
        assertThat(StringUtils.toUnderscoreCase("HelloWorld")).isEqualTo("hello_world");
        assertThat(StringUtils.toKebabCase("HelloWorld")).isEqualTo("hello-world");
        
        assertThat(StringUtils.maskMobile("13812345678")).isEqualTo("138****5678");
        assertThat(StringUtils.maskEmail("test@example.com")).isEqualTo("t***t@example.com");
    }

    @Test
    public void testResultCodeEnum() {
        // 测试响应码枚举
        assertThat(ResultCodeEnum.SUCCESS.getCode()).isEqualTo("0000");
        assertThat(ResultCodeEnum.SUCCESS.getMessage()).isEqualTo("操作成功");
        assertThat(ResultCodeEnum.SUCCESS.isSuccess()).isTrue();

        assertThat(ResultCodeEnum.PARAM_INVALID.getCode()).isEqualTo("1000");
        assertThat(ResultCodeEnum.PARAM_INVALID.isSuccess()).isFalse();

        // 测试通过代码查找枚举
        assertThat(ResultCodeEnum.getByCode("0000")).isEqualTo(ResultCodeEnum.SUCCESS);
        assertThat(ResultCodeEnum.getByCode("9999")).isEqualTo(ResultCodeEnum.SYSTEM_ERROR);
    }
}