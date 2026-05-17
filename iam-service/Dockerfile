FROM eclipse-temurin:21-jre-alpine

WORKDIR /app
RUN addgroup -S spring && adduser -S spring -G spring
COPY iam-service/target/iam-service-1.0.0-SNAPSHOT.jar /app/app.jar
USER spring

EXPOSE 8089
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
