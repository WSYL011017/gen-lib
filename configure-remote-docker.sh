#!/bin/bash
# 配置远程Docker环境变量脚本
# 使用前请替换 <remote-host> 和 <port> 为实际的远程Docker主机地址和端口

export DOCKER_HOST=tcp://39.103.56.52:2379
echo "已设置 DOCKER_HOST=$DOCKER_HOST"

# 如果使用TLS加密连接，请取消下面几行的注释并配置相应参数
# export DOCKER_TLS_VERIFY=1
# export DOCKER_CERT_PATH=/path/to/certs

echo "远程Docker环境变量已配置完成"
echo "现在可以运行Maven测试命令:"
echo "  mvn clean test"
echo "  或"
echo "  mvn clean install"