FROM openjdk:17-jdk-slim
ARG JAR_FILE=target/order-service-0.0.1.jar
COPY ${JAR_FILE} app_order.jar
ENTRYPOINT ["java", "-jar", "app_order.jar"]