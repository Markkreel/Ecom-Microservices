# Build stage
FROM eclipse-temurin:17-jdk-alpine as build

# Set working directory
WORKDIR /app

# Install build dependencies
RUN apk add --no-cache \
    curl \
    unzip \
    bash

# Copy Gradle files
COPY build.gradle settings.gradle ./
COPY gradle/ ./gradle/
COPY gradlew ./

# Make gradlew executable
RUN chmod +x ./gradlew

# Copy source code
COPY src/ ./src/

# Build the application
RUN ./gradlew build -x test

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

# Set working directory
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=prod

# Expose the port
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]