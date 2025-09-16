FROM openjdk:17-jdk-slim
ARG JAR_FILE=target/payment-service-0.0.1.jar
COPY ${JAR_FILE} app_payment.jar
ENTRYPOINT ["java", "-jar", "app_payment.jar"]