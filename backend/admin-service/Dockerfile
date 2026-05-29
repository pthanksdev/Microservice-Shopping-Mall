FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
RUN apk add --no-cache maven
COPY pom.xml .
COPY common ./common
COPY admin-service ./admin-service
RUN mvn -pl common install -DskipTests -B
RUN mvn -pl admin-service package -DskipTests -B

FROM eclipse-temurin:21-jre-alpine AS production
WORKDIR /app
RUN addgroup -S mall && adduser -S mall -G mall
COPY --from=builder /app/admin-service/target/*.jar app.jar
RUN chown mall:mall app.jar
USER mall
EXPOSE 8092
ENTRYPOINT ["java", "-jar", "app.jar"]