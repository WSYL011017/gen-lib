# Gen-Lib 微服务使用指南

## 🎯 概述

Gen-Lib 为微服务架构提供了专门的支持，通过 `gen-lib-cloud-starter` 模块实现与 Spring Cloud 的深度集成，为分布式系统提供统一的基础组件和最佳实践。

## 🚀 快速集成

### 1. 添加依赖

```xml
<dependencies>
    <!-- Gen-Lib Cloud Starter - 微服务一站式集成 -->
    <dependency>
        <groupId>com.genlib</groupId>
        <artifactId>gen-lib-cloud-starter</artifactId>
    </dependency>
</dependencies>
```

### 2. 微服务配置

在 `application.yml` 中添加微服务专用配置：

```yaml
spring:
  application:
    name: user-service
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
        namespace: ${NACOS_NAMESPACE:dev}
      config:
        server-addr: localhost:8848
        file-extension: yaml
        namespace: ${NACOS_NAMESPACE:dev}

genlib:
  enabled: true
  
  # 微服务特定配置
  cloud:
    enabled: true
    service-discovery: true
    load-balancer: true
    circuit-breaker: true
    
  # 分布式缓存
  cache:
    enabled: true
    type: redis
    redis:
      cluster: true
      nodes: ${REDIS_NODES:localhost:6379,localhost:6380,localhost:6381}
      
  # 分布式配置
  config:
    enabled: true
    source: nacos
    auto-refresh: true
    
  # 链路追踪
  monitor:
    enabled: true
    tracing: true
    metrics: true
    zipkin:
      base-url: ${ZIPKIN_URL:http://localhost:9411}
```

## 🌐 核心功能详解

### 1. 服务间调用

#### 使用负载均衡的 RestTemplate

```java
@Service
@Slf4j
public class OrderService {
    
    @Autowired
    private LoadBalancerRestTemplate restTemplate;
    
    /**
     * 调用用户服务获取用户信息
     */
    public User getUserById(Long userId) {
        String url = "http://user-service/api/users/" + userId;
        
        try {
            ResponseEntity<Result<User>> response = restTemplate.exchange(
                url, 
                HttpMethod.GET, 
                null, 
                new ParameterizedTypeReference<Result<User>>() {}
            );
            
            Result<User> result = response.getBody();
            if (result != null && result.isSuccess()) {
                return result.getData();
            }
            assert result != null;
            throw new BusinessException("用户服务返回异常：" + result.getMessage());
            
        } catch (Exception e) {
            log.error("调用用户服务失败", e);
            throw new BusinessException("用户服务调用失败");
        }
    }
    
    /**
     * 调用支付服务处理支付
     */
    public PaymentResult processPayment(PaymentRequest request) {
        String url = "http://payment-service/api/payments";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Trace-Id", TraceContext.getTraceId());
        
        HttpEntity<PaymentRequest> entity = new HttpEntity<>(request, headers);
        
        ResponseEntity<Result<PaymentResult>> response = restTemplate.exchange(
            url, 
            HttpMethod.POST, 
            entity, 
            new ParameterizedTypeReference<Result<PaymentResult>>() {}
        );
        
        return response.getBody().getData();
    }
}
```

#### 使用 Feign 客户端

```java
// Feign 接口定义
@FeignClient(name = "user-service", fallback = UserServiceFallback.class)
public interface UserServiceClient {
    
        @GetMapping("/api/users/{id}")
        Result<User> getUser(@PathVariable("id") Long id);
        
        @PostMapping("/api/users")
        Result<User> createUser(@RequestBody CreateUserRequest request);
}

// 降级实现
@Component
public class UserServiceFallback implements UserServiceClient {
        
        @Override
        public Result<User> getUser(Long id) {
                return Result.error("用户服务不可用");
        }
        
        @Override
        public Result<User> createUser(CreateUserRequest request) {
                return Result.error("用户服务不可用，创建用户失败");
        }
}

// 在业务服务中使用
@Service
public class OrderService {

        @Autowired
        private UserServiceClient userServiceClient;
        
        public void createOrder(CreateOrderRequest request) {
                // 调用用户服务验证用户
                Result<User> userResult = userServiceClient.getUser(request.getUserId());
                if (!userResult.isSuccess()) {
                    throw new BusinessException("用户验证失败：" + userResult.getMessage());
                }
                User user = userResult.getData();
                // 创建订单逻辑...
        }
}
```

### 2. 服务熔断和降级

```java
@Service
@Slf4j
public class PaymentService {

        @Autowired
        private PaymentServiceClient paymentClient;
        
        /**
         * 带熔断的支付处理
         */
        @CircuitBreaker(
                name = "payment-service", 
                fallbackMethod = "paymentFallback"
                )
        @TimeLimiter(name = "payment-service")
        @Retry(name = "payment-service")
        public CompletableFuture<PaymentResult> processPaymentAsync(PaymentRequest request) {
                return CompletableFuture.supplyAsync(() -> {
                        log.info("处理支付请求: {}", request);
                         return paymentClient.processPayment(request).getData();
                });
        }
                        
        /**
         * 熔断降级方法
         */
         public CompletableFuture<PaymentResult> paymentFallback(PaymentRequest request, Exception ex) {
                log.warn("支付服务熔断，执行降级逻辑", ex);
                        
                // 可以返回默认结果或者执行替代逻辑
                PaymentResult fallbackResult = PaymentResult.builder()
                        .success(false)
                        .message("支付服务暂时不可用，请稍后再试")
                        .errorCode("PAYMENT_SERVICE_UNAVAILABLE")
                        .build();
                return CompletableFuture.completedFuture(fallbackResult);
         }
}
```


### 3. 分布式缓存

```java
@Service
public class ProductService {
        
        /**
         * 跨服务缓存共享
         */
        @Cacheable(
                key = "product:#{id}", 
                expiration = 7200,
                cacheNames = "product-cache"
        )
        public Product getProduct(Long id) {
                log.info("从数据库查询产品: {}", id);
                return productRepository.findById(id).orElse(null);
        }
        
        /**
         * 缓存同步更新（通知所有服务实例）
         */
        @CacheEvict(
                key = "product:#{product.id}", 
                allServices = true,  // 通知所有服务清除缓存
                cacheNames = "product-cache"
        )
        public void updateProduct(Product product) {
                productRepository.save(product);
                // 发送缓存更新事件
                cacheEventPublisher.publishCacheEvict("product", product.getId());
        }
                                     
        /**
         * 分布式锁防止缓存击穿
         */
         @DistributedLock(key = "product:load:#{id}", waitTime = 3, leaseTime = 10)
         public Product loadProductWithLock(Long id) {
                      return productRepository.findById(id).orElse(null);
         }
}
```

### 4. 分布式配置管理

```java
@Component
@Slf4j
public class DynamicConfigExample {

// 基本配置注入
@ConfigValue("business.order.timeout")
private Long orderTimeout;

@ConfigValue("business.payment.enabled")
private Boolean paymentEnabled;

// 复杂对象配置
@ConfigValue("business.feature.flags")
private FeatureFlags featureFlags;

/**
* 监听配置变化
*/
@ConfigChangeListener("business.feature.flags")
public void onFeatureFlagsChange(String key, Object oldValue, Object newValue) {
log.info("功能开关发生变化: {} -> {}", oldValue, newValue);

// 动态调整业务逻辑
if (newValue instanceof FeatureFlags) {
 FeatureFlags flags = (FeatureFlags) newValue;
 adjustBusinessLogic(flags);
  }
   }
   
   /**
   * 配置刷新事件处理
   */
   @EventListener
   public void handleConfigRefresh(ConfigRefreshEvent event) {
   log.info("配置刷新事件: {}", event.getChangedKeys());
   // 执行配置刷新后的业务逻辑
      }
      
      private void adjustBusinessLogic(FeatureFlags flags) {
          // 根据功能开关调整业务逻辑
           if (flags.isNewAlgorithmEnabled()) {
           // 启用新算法
           }
           }
           }
```

### 5. 分布式链路追踪\

```java
@RestController
@RequestMapping("/api/orders")
@Slf4j
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    /**
     * 创建订单 - 自动生成链路追踪
     */
    @TraceLog(operation = "create-order")
    @PostMapping
    public Result<Order> createOrder(@RequestBody CreateOrderRequest request) {
        // 自动生成 traceId，跨服务传播
        String traceId = TraceContext.getTraceId();
        log.info("创建订单请求, traceId: {}", traceId);
        
        Order order = orderService.createOrder(request);
        return Result.success(order);
    }
}

@Service
@Slf4j
public class OrderService {
    
    @Autowired
    private UserServiceClient userServiceClient;
    
    @Autowired
    private PaymentServiceClient paymentServiceClient;
    
    @TraceSpan("create-order-process")
    public Order createOrder(CreateOrderRequest request) {      
        // 1. 验证用户
        validateUser(request.getUserId());
        
        // 2. 创建订单
        Order order = buildOrder(request);
        
        // 3. 处理支付
        processPayment(order);
        
        return order;
    }
    
    @TraceSpan("validate-user")
    private void validateUser(Long userId) {
        // 调用用户服务，自动传播 traceId
        Result<User> result = userServiceClient.getUser(userId);
        if (!result.isSuccess()) {
            throw new BusinessException("用户验证失败");
        }
    }
    
    @TraceSpan("process-payment")
    private void processPayment(Order order) {
        // 调用支付服务，链路自动传播
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .orderId(order.getId())
                .amount(order.getAmount())
                .build();
        
        paymentServiceClient.processPayment(paymentRequest);
    }
}
```

### 6. 微服务网关集成

```java
/**
 * Gateway 过滤器 - 统一请求处理
 */
@Component
@Order(1)
public class GenLibGatewayFilter implements GlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {   
        ServerHttpRequest request = exchange.getRequest();
        
        // 生成链路追踪 ID
        String traceId = generateTraceId();
        
        // 添加通用请求头
        ServerHttpRequest modifiedRequest = request.mutate()
                .header("X-Trace-Id", traceId)
                .header("X-Request-Id", UUID.randomUUID().toString())
                .header("X-Gateway-Time", String.valueOf(System.currentTimeMillis()))
                .build();
        
        // 记录请求日志
        logRequest(modifiedRequest, traceId);
        
        return chain.filter(exchange.mutate().request(modifiedRequest).build())
                .doOnTerminate(() -> logResponse(exchange, traceId));
    }

   private String generateTraceId() {
        return TraceIdGenerator.generate();
    }
   
       private void logRequest(ServerHttpRequest request, String traceId) {
        log.info("Gateway请求: method={}, path={}, traceId={}", 
                request.getMethod(), request.getPath(), traceId);
    }
    
    private void logResponse(ServerWebExchange exchange, String traceId) {
        ServerHttpResponse response = exchange.getResponse();
        log.info("Gateway响应: status={}, traceId={}", 
                response.getStatusCode(), traceId);
    }
}

/**
 * 自定义路由配置
 */
@Configuration
public class GatewayConfig {
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {   
        return builder.routes()
        // 用户服务路由
        .route("user-service", r -> r
                .path("/api/users/**")
                .filters(f -> f
                        .addRequestHeader("X-Service-Name", "user-service")
                        .addResponseHeader("X-Response-Time", String.valueOf(System.currentTimeMillis()))
                )
                .uri("lb://user-service") 
        )
        // 订单服务路由
        .route("order-service", r -> r
                .path("/api/orders/**")
                .filters(f -> f
                        .addRequestHeader("X-Service-Name", "order-service")
                        .circuitBreaker(config -> config
                                .setName("order-service-cb")
                                .setFallbackUri("/fallback/orders")
                        )
                )
                .uri("lb://order-service")
        )
                .build();
    }
}
```

### 7. 微服务监控和指标

```java
@Component
@Slf4j
public class BusinessMetrics {  
    private final MeterRegistry meterRegistry;
    private final Counter orderCreatedCounter;
    private final Timer orderProcessingTimer;
    
    public BusinessMetrics(MeterRegistry meterRegistry) {  
        this.meterRegistry = meterRegistry;
        this.orderCreatedCounter = Counter.builder("orders.created")
                .description("订单创建数量")
                .register(meterRegistry);
        this.orderProcessingTimer = Timer.builder("orders.processing.time")
                .description("订单处理时间")
                .register(meterRegistry);
    }


    /**
    * 记录订单创建指标
     */
    public void recordOrderCreated(String productType, String channel) {
        orderCreatedCounter.increment(
                Tags.of(
                        "product.type", productType,
                        "channel", channel
                )
        );
    }
    /**
    * 记录处理时间
     */
    public void recordProcessingTime(String operation, Duration duration) {   
        Timer.Sample sample = Timer.start(meterRegistry);
        sample.stop(Timer.builder("business.processing.time")
                .tag("operation", operation)
                .register(meterRegistry));
    }
    /**
    * 自定义业务指标
     */
    @EventListener
    public void handleOrderEvent(OrderCreatedEvent event) {
        recordOrderCreated(event.getProductType(), event.getChannel());
        // 记录业务指标
        Gauge.builder("orders.amount")
                .tag("currency", event.getCurrency())
                .register(meterRegistry, event.getAmount());
    }
}
```

## 🏗️ 微服务架构示例

### 典型电商微服务架构
```
┌─────────────────────────────────────────────────────────────────────────────┐
│                            微服务集群                                        │
├─────────────────────────────────────────────────────────────────────────────┤
│  ┌──────────────────┐   ┌──────────────────┐   ┌──────────────────┐       │
│  │   Gateway        │   │   User-Service   │   │  Order-Service   │       │
│  │                  │   │                  │   │                  │       │
│  │ + gen-lib-web    │   │ + gen-lib-core   │   │ + gen-lib-core   │       │
│  │ + cloud-starter  │   │ + gen-lib-web    │   │ + gen-lib-web    │       │
│  │ + circuit-break  │   │ + cloud-starter  │   │ + cloud-starter  │       │
│  └──────────────────┘   └──────────────────┘   └──────────────────┘       │
│                                                                             │
│  ┌──────────────────┐   ┌──────────────────┐   ┌──────────────────┐       │
│  │ Payment-Service  │   │ Product-Service  │   │ Notification     │       │
│  │                  │   │                  │   │ Service          │       │
│  │ + gen-lib-core   │   │ + gen-lib-core   │   │ + gen-lib-core   │       │
│  │ + gen-lib-web    │   │ + gen-lib-cache  │   │ + gen-lib-web    │       │
│  │ + cloud-starter  │   │ + cloud-starter  │   │ + cloud-starter  │       │
│  └──────────────────┘   └──────────────────┘   └──────────────────┘       │
├─────────────────────────────────────────────────────────────────────────────┤
│                           共享基础设施                                      │
│  ┌──────────────────┐   ┌──────────────────┐   ┌──────────────────┐       │
│  │ Redis Cluster    │   │ Nacos Registry   │   │ Zipkin Tracing   │       │
│  │   (缓存/会话)    │   │   (服务发现)     │   │   (链路追踪)     │       │
│  └──────────────────┘   └──────────────────┘   └──────────────────┘       │
│                                                                             │
│  ┌──────────────────┐   ┌──────────────────┐   ┌──────────────────┐       │
│  │ MySQL Cluster    │   │ RabbitMQ         │   │ Prometheus       │       │
│  │   (主数据库)     │   │   (消息队列)     │   │   (监控指标)     │       │
│  └──────────────────┘   └──────────────────┘   └──────────────────┘       │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 服务部署配置

```yaml
# docker-compose.yml\nversion: '3.8'\n\nservices:\n  # 服务注册中心\n  nacos:\n    image: nacos/nacos-server:latest\n    environment:\n      - MODE=standalone\n      - SPRING_DATASOURCE_PLATFORM=mysql\n      - MYSQL_SERVICE_HOST=mysql\n      - MYSQL_SERVICE_DB_NAME=nacos\n    ports:\n      - \"8848:8848\"\n    depends_on:\n      - mysql\n      \n  # 网关服务\n  gateway:\n    image: gateway-service:latest\n    environment:\n      - SPRING_PROFILES_ACTIVE=docker\n      - NACOS_SERVER_ADDR=nacos:8848\n    ports:\n      - \"8080:8080\"\n    depends_on:\n      - nacos\n      \n  # 用户服务\n  user-service:\n    image: user-service:latest\n    environment:\n      - SPRING_PROFILES_ACTIVE=docker\n      - NACOS_SERVER_ADDR=nacos:8848\n      - REDIS_NODES=redis-cluster:6379\n    depends_on:\n      - nacos\n      - redis-cluster\n      - mysql\n      \n  # 订单服务\n  order-service:\n    image: order-service:latest\n    environment:\n      - SPRING_PROFILES_ACTIVE=docker\n      - NACOS_SERVER_ADDR=nacos:8848\n      - REDIS_NODES=redis-cluster:6379\n    depends_on:\n      - nacos\n      - redis-cluster\n      - mysql\n      \n  # Redis 集群\n  redis-cluster:\n    image: redis:alpine\n    ports:\n      - \"6379:6379\"\n      \n  # MySQL 数据库\n  mysql:\n    image: mysql:8.0\n    environment:\n      - MYSQL_ROOT_PASSWORD=root\n      - MYSQL_DATABASE=genlib_demo\n    ports:\n      - \"3306:3306\"\n      \n  # 链路追踪\n  zipkin:\n    image: openzipkin/zipkin\n    ports:\n      - \"9411:9411\"
```

## 🔧 最佳实践

### 1. 服务拆分原则

- **单一职责**: 每个服务只负责一个业务领域
- **数据独立**: 每个服务拥有独立的数据库
- **接口标准**: 使用统一的 API 规范和响应格式
- **无状态设计**: 服务实例之间无状态依赖

### 2. 配置管理策略

```yaml
# 环境相关配置
spring:
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:dev}

# 服务发现配置
genlib:
  cloud:
    service-discovery:
      health-check-interval: 10s
        metadata:
          version: "1.0.0"
            zone: ${ZONE:default}
```

### 3. 错误处理策略

```java
@ControllerAdvice
public class MicroserviceExceptionHandler {  
    
    @ExceptionHandler(ServiceUnavailableException.class)
    public Result<Void> handleServiceUnavailable(ServiceUnavailableException e) {
        return Result.error("SERVICE_UNAVAILABLE", "服务暂时不可用，请稍后重试");
    }  
    @ExceptionHandler(CircuitBreakerOpenException.class)
    public Result<Void> handleCircuitBreakerOpen(CircuitBreakerOpenException e) { 
        return Result.error("SERVICE_DEGRADED", "服务降级中，请稍后重试");
    }
}
```

### 4. 性能监控

```java
// 关键指标监控
@Aspect
@Component
public class PerformanceMonitorAspect {
    
    @Around("@annotation(TraceLog)")
    public Object monitorPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String method = joinPoint.getSignature().getName();
        
        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - startTime;
            
            // 记录性能指标
            recordMetrics(method, duration, "success");
            
            return result;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            recordMetrics(method, duration, "error");
            throw e;
        }
    }
}

```

## 📈 运维和监控

### 健康检查配置

```java
@Component
public class CustomHealthIndicator implements HealthIndicator {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public Health health() {
        try {
            // 检查 Redis 连接
            redisTemplate.opsForValue().get("health-check");

            return Health.up()
                    .withDetail("redis", "UP")
                    .withDetail("timestamp", LocalDateTime.now())
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("redis", "DOWN")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
```

### 日志聚合

```yaml
# logback-spring.xml
logging:
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%X{traceId}] [%thread] %-5level %logger{36} - %msg%n"
      level:
        com.genlib: DEBUG
          org.springframework.cloud: INFO
```
---

## 🔗 相关链接
- [Gen-Lib 主文档](README.md)
- [快速开始指南](GETTING_STARTED.md)
- [架构设计文档](框架设计文档.md)
- [示例项目](gen-lib-samples/microservice-demo/)
- 
- **需要更多帮助？** 请查看我们的 [微服务示例项目](gen-lib-samples/microservice-demo/) 或访问 [GitHub Issues](https://github.com/your-org/gen-lib/issues)。