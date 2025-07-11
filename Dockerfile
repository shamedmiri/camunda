FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/javaworkerapplication-1.jar ./javaworkerapplication-1.jar
ENTRYPOINT ["java", "-jar", "/app/javaworkerapplication-1.jar"]
