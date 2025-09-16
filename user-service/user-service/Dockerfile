FROM openjdk:17-jdk-slim
ARG JAR_FILE=target/user-service-0.0.1.jar
COPY ${JAR_FILE} app_user.jar
ENTRYPOINT ["java", "-jar", "app_user.jar"]