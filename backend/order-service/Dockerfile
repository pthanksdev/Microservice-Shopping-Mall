FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
RUN apk add --no-cache maven

# Copy parent pom and common module
COPY pom.xml .
COPY common ./common
RUN mvn -pl common install -DskipTests -B

# Copy order-service module
COPY order-service ./order-service
RUN mvn -pl order-service package -DskipTests -B

FROM eclipse-temurin:21-jre-alpine AS production
WORKDIR /app
RUN addgroup -S mall && adduser -S mall -G mall
COPY --from=builder /app/order-service/target/*.jar app.jar
RUN chown mall:mall app.jar
USER mall
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]