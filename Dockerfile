# Stage 1: Build the app
FROM gradle:8.5-jdk17 AS build

WORKDIR /app

COPY . .

# Build jar and run tests
RUN gradle clean test bootJar --no-daemon

# Stage 2: Create the slim image
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy the built jar from the previous stage
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]