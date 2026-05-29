FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
RUN apk add --no-cache maven

# Copy the parent pom and build common first
COPY pom.xml .
COPY common ./common
RUN mvn -pl common install -DskipTests -B

# Copy payment-service and build it
COPY payment-service ./payment-service
RUN mvn -pl payment-service package -DskipTests -B

FROM eclipse-temurin:21-jre-alpine AS production
WORKDIR /app
RUN addgroup -S mall && adduser -S mall -G mall
COPY --from=builder /app/payment-service/target/*.jar app.jar
RUN chown mall:mall app.jar
USER mall
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]