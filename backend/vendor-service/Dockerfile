FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
RUN apk add --no-cache maven

# Copy the parent pom and shared common module
COPY pom.xml .
COPY common ./common

# Copy the specific service module
COPY vendor-service ./vendor-service

# Install common module and package the vendor-service
RUN mvn -pl common install -DskipTests -B
RUN mvn -pl vendor-service package -DskipTests -B

FROM eclipse-temurin:21-jre-alpine AS production
WORKDIR /app
RUN addgroup -S mall && adduser -S mall -G mall
COPY --from=builder /app/vendor-service/target/*.jar app.jar
RUN chown mall:mall app.jar
USER mall
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "app.jar"]