FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Copy parent pom
COPY pom.xml .

# Copy shared common module
COPY common ./common

# Copy media-service module
COPY media-service ./media-service

# Install Maven and build
RUN apk add --no-cache maven && \
    mvn -pl common install -DskipTests && \
    mvn -pl media-service package -DskipTests

FROM eclipse-temurin:21-jre-alpine AS production
WORKDIR /app

RUN addgroup -S mall && adduser -S mall -G mall

# Copy jar from the specific module's target directory
COPY --from=builder /app/media-service/target/*.jar app.jar

RUN chown mall:mall app.jar
USER mall
EXPOSE 8094

ENTRYPOINT ["java", "-jar", "app.jar"]