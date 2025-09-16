FROM openjdk:17-jdk-slim
ARG JAR_FILE=target/notification-service-0.0.1.jar
COPY ${JAR_FILE} app_notification.jar
ENTRYPOINT ["java", "-jar", "app_notification.jar"]