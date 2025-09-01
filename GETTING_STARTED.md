# Gen-Lib 快速开始指南

## 🎯 概览

Gen-Lib 是一个零侵入、高扩展、标准化的企业级Java通用依赖库。本指南将帮助您快速上手。

## 🚀 5分钟快速集成

### 步骤1: 添加依赖管理

在项目的 `pom.xml` 中添加：

```xml
<properties>
    <gen-lib.version>1.0.0-SNAPSHOT</gen-lib.version>
</properties>

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.genlib</groupId>
            <artifactId>gen-lib-bom</artifactId>
            <version>${gen-lib.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### 步骤2: 选择集成方式

#### 方式一：一站式集成（推荐）

```xml
<dependency>
    <groupId>com.genlib</groupId>
    <artifactId>gen-lib-spring-boot-starter</artifactId>
</dependency>
```

#### 方式二：按需引入

```xml
<!-- 核心模块 -->
<dependency>
    <groupId>com.genlib</groupId>
    <artifactId>gen-lib-core</artifactId>
</dependency>

<!-- 工具类库 -->
<dependency>
    <groupId>com.genlib</groupId>
    <artifactId>gen-lib-utils</artifactId>
</dependency>
```

### 步骤3: 配置文件

在 `application.yml` 中添加：

```yaml
genlib:
  enabled: true
  
  # 缓存配置
  cache:
    enabled: true
    type: caffeine
    default-expiration: 3600
    
  # Web增强
  web:
    enabled: true
    global-exception-handler: true
```

### 步骤4: 开始使用

```java
@RestController
public class HelloController {
    
    @GetMapping("/hello")
    public Result<String> hello() {
        return Result.success("Hello Gen-Lib!");
    }
}
```

## 📚 核心功能示例

### 统一响应结果

```java
/*
import com.genlib.core.model.Result;

// 成功响应
return Result.success(data);
return Result.success("操作成功", data);

// 失败响应
return Result.error("操作失败");
return Result.error(ResultCodeEnum.BUSINESS_ERROR, "业务错误");
*/
```

### 工具类使用

```java
import com.genlib.utils.text.StringUtils;
import com.genlib.utils.time.DateUtils;
import com.genlib.utils.json.JsonUtils;

// 字符串工具
String camelCase = StringUtils.toCamelCase("user_name"); // userName
boolean isEmail = StringUtils.isValidEmail("test@example.com");

// 日期工具
String now = DateUtils.formatDateTime(LocalDateTime.now());
LocalDate firstDay = DateUtils.getFirstDayOfMonth();

// JSON工具
String json = JsonUtils.toJson(object);
MyObject obj = JsonUtils.fromJson(json, MyObject.class);
```

### 异常处理

```java
import com.genlib.core.exception.BusinessException;

public class Test{
    public void test() {
        // 抛出业务异常
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 使用枚举异常
        throw new BusinessException(ResultCodeEnum.DATA_NOT_FOUND, "数据未找到");
    }
}
```

### 缓存使用

```java
import com.genlib.cache.annotation.Cacheable;
import com.genlib.cache.annotation.CacheEvict;

@Service
public class UserService {
    
    @Cacheable(key = "user:#{id}", expiration = 3600)
    public User getUser(Long id) {
        return userRepository.findById(id);
    }
    
    @CacheEvict(key = "user:#{user.id}")
    public void updateUser(User user) {
        userRepository.save(user);
    }
}
```

## 🎯 最佳实践

### 1. 项目结构推荐

```
src/main/java/
├── controller/          # 控制层
├── service/            # 业务层
├── repository/         # 数据访问层
├── entity/             # 实体类
├── dto/                # 数据传输对象
├── enums/              # 枚举定义
├── exception/          # 自定义异常
└── config/             # 配置类
```

### 2. 统一响应格式

```java
// 推荐的Controller写法
@RestController
@RequestMapping("/api")
public class ApiController {
    
    @GetMapping("/users/{id}")
    public Result<User> getUser(@PathVariable Long id) {
        User user = userService.getUser(id);
        return Result.success(user);
    }
    
    @PostMapping("/users")
    public Result<User> createUser(@RequestBody @Valid CreateUserRequest request) {
        User user = userService.createUser(request);
        return Result.success("用户创建成功", user);
    }
}
```

### 3. 异常处理规范

```java
// 全局异常处理器已自动配置，直接抛出异常即可
@Service
public class UserService {
    
    public User getUser(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new BusinessException("用户不存在"));
    }
}
```

## 🔧 常见问题

### Q1: 如何自定义响应码？

```java
// 扩展ResultCodeEnum
public enum CustomResultCode implements ResultCode {
    CUSTOM_ERROR("CUSTOM_001", "自定义错误");
    
    private final String code;
    private final String message;
    
    // constructor and methods...
}
```

### Q2: 如何配置自定义缓存？

```yaml
genlib:
  cache:
    enabled: true
    type: redis
    redis:
      host: localhost
      port: 6379
      database: 0
```

### Q3: 如何禁用某些自动配置？

```yaml
genlib:
  web:
    global-exception-handler: false
  cache:
    enabled: false
```

## 🚀 下一步

- 查看 [完整文档](README.md)
- 🌐 浏览 [微服务使用指南](MICROSERVICE_GUIDE.md)
- 浏览 [示例项目](gen-lib-samples/)
- 阅读 [架构设计](框架设计文档.md)
- 参与 [项目贡献](CONTRIBUTING.md)

---

**需要帮助？** 请查看我们的 [Issues](https://github.com/your-org/gen-lib/issues) 或发送邮件到 gen-lib@example.com