FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Install Maven
RUN apk add --no-cache maven

# Copy parent pom
COPY pom.xml .

# Copy shared modules
COPY common ./common

# Copy service module
COPY discount-service ./discount-service

# Install common module and package the service
RUN mvn -pl common install -DskipTests
RUN mvn -pl discount-service package -DskipTests

FROM eclipse-temurin:21-jre-alpine AS production
WORKDIR /app
RUN addgroup -S mall && adduser -S mall -G mall
COPY --from=builder /app/discount-service/target/*.jar app.jar
RUN chown mall:mall app.jar
USER mall
EXPOSE 8090
ENTRYPOINT ["java", "-jar", "app.jar"]