# Stage 1: Build the application
FROM maven:3.9.5-eclipse-temurin-21-alpine AS build

WORKDIR /app

# Copy the pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the source code and build the application
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:21-jre-alpine

# Install wget for health checks
RUN apk add --no-cache wget

WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Create uploads directory
RUN mkdir -p /app/uploads

# Expose the application port
EXPOSE 8088

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

