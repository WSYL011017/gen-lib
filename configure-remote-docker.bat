@echo off
REM 配置远程Docker环境变量脚本
REM 使用前请替换 <remote-host> 和 <port> 为实际的远程Docker主机地址和端口

set DOCKER_HOST=tcp://39.103.56.52:2376
echo 已设置 DOCKER_HOST=%DOCKER_HOST%

REM 如果使用TLS加密连接，请取消下面几行的注释并配置相应参数
REM set DOCKER_TLS_VERIFY=1
REM set DOCKER_CERT_PATH=C:\path\to\certs

echo 远程Docker环境变量已配置完成
echo 现在可以运行Maven测试命令:
echo   mvn clean test
echo   或
echo   mvn clean install