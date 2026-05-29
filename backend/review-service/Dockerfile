FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Copy the parent pom from the root context
COPY pom.xml ./

# Copy the specific module directory
COPY pom.xml ./
COPY src ./src

# Run the build targeting just this specific module
RUN apk add --no-cache maven && mvn -pl review-service -am dependency:go-offline -B
RUN mvn -pl review-service package -DskipTests
FROM eclipse-temurin:21-jre-alpine AS production
WORKDIR /app
RUN addgroup -S mall && adduser -S mall -G mall
COPY --from=builder /app/target/*.jar app.jar
RUN chown mall:mall app.jar
USER mall
EXPOSE 8089
ENTRYPOINT ["java", "-jar", "app.jar"]
