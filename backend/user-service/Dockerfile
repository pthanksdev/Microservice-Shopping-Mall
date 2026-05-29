FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
RUN apk add --no-cache maven

# Copy parent pom and shared modules
COPY pom.xml .
COPY common ./common

# Install shared modules
RUN mvn -pl common install -DskipTests

# Copy and build user-service
COPY user-service ./user-service
RUN mvn -pl user-service package -DskipTests

FROM eclipse-temurin:21-jre-alpine AS production
WORKDIR /app
RUN addgroup -S mall && adduser -S mall -G mall
COPY --from=builder /app/user-service/target/*.jar app.jar
RUN chown mall:mall app.jar
USER mall
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]

FROM production AS dev
USER root
ENV JAVA_TOOL_OPTIONS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5081"
USER mall