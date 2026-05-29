FROM docker.io/library/eclipse-temurin:21-jre-alpine AS production
WORKDIR /app

RUN addgroup -S mall && adduser -S mall -G mall

# Look straight inside the service's target folder for the compiled jar
COPY target/*.jar app.jar
RUN chown mall:mall app.jar

USER mall
EXPOSE 8087
ENTRYPOINT ["java", "-jar", "app.jar"]