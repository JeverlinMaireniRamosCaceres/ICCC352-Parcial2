# STAGE 1 - Build
FROM gradle:8.5-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle build -x test

# STAGE 2 - Run
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/parcial-2-1.0-SNAPSHOT.jar app.jar
EXPOSE 7000
ENTRYPOINT ["java", "-jar", "app.jar"]