# Stage 1: Build JAR with Maven
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /workspace

# Cache Maven wrapper and dependencies
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Compile and package application
COPY src/ src/
RUN ./mvnw clean package -DskipTests

# Stage 2: Minimal JRE runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Run as non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy packaged jar from builder
COPY --from=builder /workspace/target/*.jar app.jar

# Render dynamic port binding and production profile defaults
ENV PORT=8080
ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Dserver.port=${PORT} -jar app.jar"]

