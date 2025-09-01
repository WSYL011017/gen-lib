package com.genlib.integration;

import com.genlib.cache.core.CacheManager;
import com.genlib.config.core.ConfigManager;
import com.genlib.core.enums.ResultCodeEnum;
import com.genlib.core.model.Result;
import com.genlib.utils.text.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Gen-Lib 完整集成测试
 * 测试所有核心功能的集成效果
 * 
 * @author Gen-Lib Team
 * @since 1.0.0
 */
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = IntegrationTestApplication.class
)
@ActiveProfiles("test")
@Testcontainers
@EnabledIf("isDockerAvailable")
public class GenLibIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired(required = false)
    private ConfigManager configManager;

    @Autowired(required = false)
    private CacheManager cacheManager;

    /**
     * 检查Docker环境是否可用
     */
    static boolean isDockerAvailable() {
        try {
            DockerClientFactory.instance().client();
            return true;
        } catch (Exception e) {
            System.out.println("Docker环境不可用，跳过集成测试: " + e.getMessage());
            return false;
        }
    }

    @BeforeAll
    static void checkDockerEnvironment() {
        if (!isDockerAvailable()) {
            System.out.println("警告: Docker环境不可用，集成测试将被跳过");
            System.out.println("请确保Docker已安装并运行，或配置远程Docker环境");
            System.out.println("配置方法：在testcontainers.properties中设置docker.host");
        }
    }

    // MySQL测试容器
    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
            .withDatabaseName("genlib_test")
            .withUsername("test")
            .withPassword("test123");

    // Redis测试容器
    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // MySQL配置
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.datasource.driver-class-name", mysql::getDriverClassName);

        // Redis配置
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379).toString());
    }

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
    public void testUserManagementIntegration() {
        // 测试用户管理API
        TestUser user = new TestUser();
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        // 创建用户
        ResponseEntity<Result> createResponse = restTemplate.postForEntity(
            "http://localhost:" + port + "/api/users", user, Result.class);
        
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(createResponse.getBody()).isNotNull();
        assertThat(createResponse.getBody().isSuccess()).isTrue();

        // 查询用户
        ResponseEntity<Result> getResponse = restTemplate.getForEntity(
            "http://localhost:" + port + "/api/users/1", Result.class);
        
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody().isSuccess()).isTrue();
    }

    @Test
    public void testErrorHandlingIntegration() {
        // 测试错误处理
        ResponseEntity<Result> response = restTemplate.getForEntity(
            "http://localhost:" + port + "/api/users/999999", Result.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getCode()).isEqualTo(ResultCodeEnum.DATA_NOT_FOUND.getCode());
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

    /**
     * 测试用户实体
     */
    public static class TestUser {
        private String username;
        private String email;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
}