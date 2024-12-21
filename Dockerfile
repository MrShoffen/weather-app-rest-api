FROM eclipse-temurin:21-jdk-noble AS builder

WORKDIR /app
COPY gradlew .
COPY gradle /app/gradle
COPY build.gradle .
COPY settings.gradle .
COPY src /app/src
RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app
COPY --from=builder /app/build/libs/*.jar weather-rest.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "weather-rest.jar"]