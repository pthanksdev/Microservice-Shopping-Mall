# Builder stage: Build the application
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Install Maven for building
RUN apk add --no-cache maven

# Copy the master POM and download dependencies to leverage Docker cache
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the rest of the backend source code
COPY . .

# Build the specific service module and its dependencies
# The '-am' flag ensures that required modules (like 'common') are also built
# The '-DskipTests' flag skips running tests during the build
RUN mvn package -pl :review-service -am -DskipTests

# Production stage: Create the final lightweight image
FROM eclipse-temurin:21-jre-alpine AS production

WORKDIR /app

# Argument to define the service name, used for finding the JAR file
ARG SERVICE_NAME=review-service

# Copy the built JAR from the builder stage
COPY --from=builder /app/${SERVICE_NAME}/target/*.jar app.jar

# Expose the port the service runs on
EXPOSE 8089

# Entrypoint to run the Java application
ENTRYPOINT ["java", "-jar", "app.jar"]


# Dev stage: An extension of the production image for development
FROM production AS dev

# Enable remote debugging on port 5089
ENV JAVA_TOOL_OPTIONS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5089"