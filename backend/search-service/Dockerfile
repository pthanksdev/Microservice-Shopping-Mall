FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

RUN apk add --no-cache maven

COPY pom.xml .
COPY common ./common
COPY search-service ./search-service

RUN mvn -pl common install -DskipTests
RUN mvn -pl search-service package -DskipTests

FROM eclipse-temurin:21-jre-alpine AS production
WORKDIR /app
RUN addgroup -S mall && adduser -S mall -G mall
COPY --from=builder /app/search-service/target/*.jar app.jar
RUN chown mall:mall app.jar
USER mall
EXPOSE 8093
ENTRYPOINT ["java", "-jar", "app.jar"]