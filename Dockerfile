FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src
COPY web-admin ./web-admin

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY --from=build /app/target/cletaeats-1.0-jar-with-dependencies.jar /app/app.jar
COPY --from=build /app/web-admin /app/web-admin

ENV WEB_ADMIN_PATH=/app/web-admin

EXPOSE 8080

CMD ["java", "-jar", "/app/app.jar"]
