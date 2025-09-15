FROM openjdk:17-jdk-slim
ARG JAR_FILE=target/api-gateway-pato-0.0.1.jar
COPY ${JAR_FILE} app_gateway.jar
ENTRYPOINT ["java", "-jar", "app_gateway.jar"]