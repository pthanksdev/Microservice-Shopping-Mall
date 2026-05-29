FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
RUN apk add --no-cache maven

# Copy parent pom and modules
COPY pom.xml .
COPY common ./common
COPY shipping-service ./shipping-service

# Install common module and build the service
RUN mvn -pl common install -DskipTests
RUN mvn -pl shipping-service package -DskipTests

FROM eclipse-temurin:21-jre-alpine AS production
WORKDIR /app
RUN addgroup -S mall && adduser -S mall -G mall
COPY --from=builder /app/shipping-service/target/*.jar app.jar
RUN chown mall:mall app.jar
USER mall
EXPOSE 8088
ENTRYPOINT ["java", "-jar", "app.jar"]