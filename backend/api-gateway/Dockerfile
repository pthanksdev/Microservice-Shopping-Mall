FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Install Maven
RUN apk add --no-cache maven

# Copy parent pom
COPY pom.xml .

# Copy common module and install it
COPY common ./common
RUN mvn -pl common install -DskipTests

# Copy api-gateway module and package it
COPY api-gateway ./api-gateway
RUN mvn -pl api-gateway package -DskipTests

FROM eclipse-temurin:21-jre-alpine AS production
WORKDIR /app
COPY --from=builder /app/api-gateway/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

FROM production AS dev
ENV JAVA_TOOL_OPTIONS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5080"