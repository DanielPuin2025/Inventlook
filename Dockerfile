FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app
COPY . .
RUN chmod +x mvnw && ./mvnw -B -DskipTests package

FROM eclipse-temurin:21-jre

WORKDIR /app
RUN useradd --system --uid 10001 appuser
COPY --from=build --chown=appuser:appuser /app/target/inventlook-*.jar /app/app.jar
USER appuser

EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]
