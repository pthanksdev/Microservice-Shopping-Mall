FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
RUN apk add --no-cache maven

# Copy parent pom and common module
COPY pom.xml .
COPY common ./common
RUN mvn -pl common install -DskipTests

# Copy wishlist-service module
COPY wishlist-service ./wishlist-service
RUN mvn -pl wishlist-service package -DskipTests

FROM eclipse-temurin:21-jre-alpine AS production
WORKDIR /app
RUN addgroup -S mall && adduser -S mall -G mall
COPY --from=builder /app/wishlist-service/target/*.jar app.jar
RUN chown mall:mall app.jar
USER mall
EXPOSE 8091
ENTRYPOINT ["java", "-jar", "app.jar"]