FROM openjdk:21-jdk-slim

WORKDIR /app

COPY build/libs/storagerest-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]
