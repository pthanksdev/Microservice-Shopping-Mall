FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

RUN apk add --no-cache maven

# Copy the parent pom
COPY pom.xml .

# Copy the common module and the notification-service module
COPY common ./common
COPY notification-service ./notification-service

# Install common module so notification-service can use it
RUN mvn -pl common install -DskipTests -B

# Package notification-service
RUN mvn -pl notification-service package -DskipTests -B

FROM eclipse-temurin:21-jre-alpine AS production
WORKDIR /app
RUN addgroup -S mall && adduser -S mall -G mall

COPY --from=builder /app/notification-service/target/*.jar app.jar
RUN chown mall:mall app.jar

USER mall
EXPOSE 8087
ENTRYPOINT ["java", "-jar", "app.jar"]