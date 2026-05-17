FROM eclipse-temurin:21-jre-alpine

WORKDIR /app
RUN addgroup -S spring && adduser -S spring -G spring
COPY ai-service/modules/ai-bootstrap/target/ai-bootstrap-*.jar /app/app.jar
USER spring

EXPOSE 8091
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
