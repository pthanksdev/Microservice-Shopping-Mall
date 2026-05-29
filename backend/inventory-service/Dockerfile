FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
RUN apk add --no-cache maven

# Copy the parent pom and shared module
COPY pom.xml .
COPY common ./common

# Copy the specific service module
COPY inventory-service ./inventory-service

# Install the common module to local repository and package inventory-service
RUN mvn -pl common install -DskipTests -B
RUN mvn -pl inventory-service package -DskipTests -B

FROM eclipse-temurin:21-jre-alpine AS production
WORKDIR /app
RUN addgroup -S mall && adduser -S mall -G mall
COPY --from=builder /app/inventory-service/target/*.jar app.jar
RUN chown mall:mall app.jar
USER mall
EXPOSE 8086
ENTRYPOINT ["java", "-jar", "app.jar"]