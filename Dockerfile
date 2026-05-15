# Build stage: compile the app and build the fat jar with Maven
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy only pom and sources first for dependency caching
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Runtime stage: lightweight Java image with the built jar
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY --from=build /app/target/cletaeats-1.0-jar-with-dependencies.jar /app/app.jar
COPY web-admin /app/web-admin
ENV WEB_ADMIN_PATH=/app/web-admin

EXPOSE 8080

CMD ["java", "-jar", "/app/app.jar"]
