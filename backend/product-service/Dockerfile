FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

RUN apk add --no-cache maven

COPY pom.xml .
COPY common ./common
COPY product-service ./product-service

RUN mvn -pl common install -DskipTests
RUN mvn -pl product-service package -DskipTests

FROM eclipse-temurin:21-jre-alpine AS production
WORKDIR /app
RUN addgroup -S mall && adduser -S mall -G mall
COPY --from=builder /app/product-service/target/*.jar app.jar
RUN chown mall:mall app.jar
USER mall
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]