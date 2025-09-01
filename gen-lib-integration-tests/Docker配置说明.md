# Docker环境配置说明

## 概述

Gen-Lib集成测试支持多种Docker环境配置，包括本地Docker和远程Docker。

## 配置方式

### 1. 本地Docker Desktop（Windows）

如果您使用Docker Desktop，通常无需特殊配置：

```properties
# testcontainers.properties
# 默认配置，通常自动检测
```

### 2. 远程Docker守护进程

在 `src/test/resources/testcontainers.properties` 中配置：

```properties
# 远程Docker主机（HTTP）
docker.host=tcp://your-remote-docker-host:2375

# 远程Docker主机（HTTPS with TLS）
docker.host=tcp://your-remote-docker-host:2376
docker.tls.verify=1
docker.cert.path=/path/to/certs
```

### 3. 环境变量配置

也可以通过环境变量配置：

```bash
# Windows PowerShell
$env:DOCKER_HOST = "tcp://your-remote-docker-host:2375"
$env:TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE = "/var/run/docker.sock"

# Linux/Mac
export DOCKER_HOST=tcp://your-remote-docker-host:2375
export TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE=/var/run/docker.sock
```

### 4. CI/CD环境配置

对于GitHub Actions、Jenkins等CI环境：

```properties
# testcontainers.properties
testcontainers.ryuk.disabled=true
testcontainers.reuse.enable=false
```

## 测试类型

### 基础集成测试（推荐）

不依赖Docker容器，测试核心功能：
- `GenLibBasicIntegrationTest` - 基础功能测试

### 完整集成测试

需要Docker环境，测试数据库和缓存集成：
- `GenLibIntegrationTest` - 完整功能测试（需要Docker）

## 故障排除

### 1. Docker环境检测失败

```
Could not find a valid Docker environment
```

**解决方案**：
1. 确认Docker已安装并运行
2. 检查Docker守护进程状态
3. 配置正确的`docker.host`
4. 运行基础集成测试而非完整集成测试

### 2. 网络连接问题

```
Connect to localhost:2375 failed
```

**解决方案**：
1. 检查防火墙设置
2. 确认Docker API端口开放
3. 使用正确的主机地址和端口

### 3. 权限问题

```
Permission denied while trying to connect to the Docker daemon
```

**解决方案**：
1. 确保用户在docker组中（Linux）
2. 以管理员身份运行（Windows）
3. 检查Docker socket权限

## 建议的使用方式

### 开发环境
- 使用本地Docker Desktop
- 运行基础集成测试进行快速验证
- 需要数据库测试时运行完整集成测试

### CI/CD环境
- 配置Docker-in-Docker或远程Docker
- 优先运行基础集成测试
- 在特定环境运行完整集成测试

### 生产部署验证
- 使用完整集成测试验证所有组件
- 配置真实的数据库和缓存环境
- 进行端到端测试