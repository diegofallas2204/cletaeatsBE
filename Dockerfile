# ── Stage 1: Build ──────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Copiar pom.xml primero (cache de dependencias)
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Copiar código fuente y compilar
COPY src ./src
RUN mvn clean package -DskipTests -q

# ── Stage 2: Runtime ─────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copiar el Fat JAR desde el stage de build
COPY --from=builder /app/target/backend.jar ./backend.jar

# Copiar el panel de administración (se sirve como archivos estáticos)
COPY web-admin ./web-admin

# Puerto expuesto (Railway lo asigna via ENV PORT)
EXPOSE 8080

# Comando de inicio
CMD ["java", "-jar", "backend.jar"]
