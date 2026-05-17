FROM eclipse-temurin:21-jre-alpine

WORKDIR /app
RUN addgroup -S spring && adduser -S spring -G spring
COPY bosquevivo-service/target/bosquevivo-service-1.0.0-SNAPSHOT.jar /app/app.jar
USER spring

EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
