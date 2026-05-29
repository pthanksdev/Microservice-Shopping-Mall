FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
RUN apk add --no-cache maven

# Copy parent pom and shared modules
COPY pom.xml .
COPY common ./common

# Copy current module
COPY review-service ./review-service

# Build common then review-service
RUN mvn -pl common install -DskipTests -B
RUN mvn -pl review-service package -DskipTests -B

FROM eclipse-temurin:21-jre-alpine AS production
WORKDIR /app
RUN addgroup -S mall && adduser -S mall -G mall
COPY --from=builder /app/review-service/target/*.jar app.jar
RUN chown mall:mall app.jar
USER mall
EXPOSE 8089
ENTRYPOINT ["java", "-jar", "app.jar"]