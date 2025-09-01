# 远程Docker环境配置指南

本指南介绍如何配置Gen-Lib项目使用远程Docker环境运行Testcontainers测试。

## 1. 配置远程Docker主机

### 1.1 Windows系统

1. 编辑 `configure-remote-docker.bat` 文件，将 `<remote-host>` 和 `<port>` 替换为实际的远程Docker主机地址和端口：

```batch
set DOCKER_HOST=tcp://your-remote-docker-host.com:2376
```

2. 如果使用TLS加密连接，还需要配置证书路径：

```batch
set DOCKER_TLS_VERIFY=1
set DOCKER_CERT_PATH=C:\path\to\your\certs
```

3. 运行配置脚本：

```cmd
configure-remote-docker.bat
```

### 1.2 Linux/macOS系统

1. 编辑 `configure-remote-docker.sh` 文件，将 `<remote-host>` 和 `<port>` 替换为实际的远程Docker主机地址和端口：

```bash
export DOCKER_HOST=tcp://your-remote-docker-host.com:2376
```

2. 如果使用TLS加密连接，还需要配置证书路径：

```bash
export DOCKER_TLS_VERIFY=1
export DOCKER_CERT_PATH=/path/to/your/certs
```

3. 运行配置脚本：

```bash
chmod +x configure-remote-docker.sh
./configure-remote-docker.sh
```

## 2. 验证Docker连接

配置完成后，可以通过以下命令验证连接是否成功：

```bash
docker version
```

如果连接成功，您将看到远程Docker服务器的版本信息。

## 3. 运行测试

配置好环境变量后，可以直接运行Maven测试命令：

```bash
# 运行所有测试
mvn clean test

# 或者运行特定模块的测试
mvn clean install
```

## 4. CI/CD环境配置

在CI/CD环境中，可以直接在构建脚本中设置环境变量：

```bash
export DOCKER_HOST=tcp://your-remote-docker-host.com:2376
mvn clean test
```

或者在Maven命令中直接指定：

```bash
DOCKER_HOST=tcp://your-remote-docker-host.com:2376 mvn clean test
```

## 5. 故障排除

如果遇到连接问题，请检查：

1. 远程Docker守护进程是否正在运行并接受远程连接
2. 防火墙是否允许访问指定端口
3. TLS证书是否正确配置（如果使用TLS）
4. 网络连接是否正常