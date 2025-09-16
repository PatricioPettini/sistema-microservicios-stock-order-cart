FROM openjdk:17-jdk-slim
ARG JAR_FILE=target/invoice-service-0.0.1.jar
COPY ${JAR_FILE} app_invoice.jar
ENTRYPOINT ["java", "-jar", "app_invoice.jar"]