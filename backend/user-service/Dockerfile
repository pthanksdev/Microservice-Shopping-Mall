# Multi-stage Dockerfile — user-service
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY pom.xml .
# Download dependencies separately to leverage Docker layer caching
RUN apk add --no-cache maven && mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests

FROM eclipse-temurin:21-jre-alpine AS production
WORKDIR /app
RUN addgroup -S mall && adduser -S mall -G mall
COPY --from=builder /app/target/*.jar app.jar
RUN chown mall:mall app.jar
USER mall
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]

FROM production AS dev
USER root
ENV JAVA_TOOL_OPTIONS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5081"
USER mall
