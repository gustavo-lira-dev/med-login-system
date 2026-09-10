# ==========================================
# Build Stage
# ==========================================
FROM eclipse-temurin:25-jdk-alpine AS builder
WORKDIR /build

# Copy only the build configuration files first to leverage Docker layer caching
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies in an isolated package
RUN ./mvnw dependency:go-offline -B

# Copy the actual application source code
COPY src src

# Package the application into a JAR, skips tests for faster build speed
RUN ./mvnw clean package -DskipTests



# ==========================================
# Run Stage
# ==========================================
FROM eclipse-temurin:25-jre-alpine AS runner
WORKDIR /app

# Create a system user for maximum security and non-root execution
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy only the compiled JAR file from the builder stage
COPY --from=builder /build/target/*.jar app.jar

# Optimize JVM for containers and Virtual Threads (Java 25 performance flags)
ENV JAVA_OPTS="-XX:+UseG1GC -XX:+UseStringDeduplication"

# Document the network port the container listens on
EXPOSE 8080

# Execute the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
