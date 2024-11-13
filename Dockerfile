# 使用官方 Java 运行时作为基础镜像
FROM openjdk:21-jdk-slim
LABEL authors="王梓铭"

# 设置工作目录
WORKDIR /app

# 将项目的 JAR 文件复制到容器中
COPY target/dell-fans-management-system-1.0.0.jar /app/dell-fans-management-system-1.0.0.jar

# 设置容器启动命令
CMD ["java", "-jar", "dell-fans-management-system-1.0.0.jar"]

# 对外暴露端口
EXPOSE 8080