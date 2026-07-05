FROM maven:3.9.8-eclipse-temurin-21-alpine AS builder
WORKDIR /workspace

COPY pom.xml .
COPY src ./src
RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /workspace/target/*.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

