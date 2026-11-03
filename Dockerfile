# syntax=docker/dockerfile:1
# =====================================================================
# Edicria — imagem única: Angular (nginx) + Spring Boot (Java 21)
# nginx escuta em $PORT, serve o frontend e repassa /api para o Spring
# (mesma origem => cookie de sessão funciona e não precisa de CORS).
# =====================================================================

# ---------- 1) Build do frontend (Angular) ----------
FROM node:22-alpine AS frontend-build
WORKDIR /app
COPY frontend/package.json frontend/package-lock.json frontend/.npmrc ./
RUN npm ci
COPY frontend/ ./
RUN npm run build -- --configuration production

# ---------- 2) Build do backend (Spring Boot) ----------
FROM maven:3.9-eclipse-temurin-21 AS backend-build
WORKDIR /app
COPY backend/pom.xml ./
RUN mvn -B -q dependency:go-offline
COPY backend/src ./src
RUN mvn -B -q package -DskipTests && mv target/*.jar app.jar

# ---------- 3) Imagem final ----------
FROM eclipse-temurin:21-jre
RUN apt-get update \
 && apt-get install -y --no-install-recommends nginx gettext-base curl \
 && rm -rf /var/lib/apt/lists/* /etc/nginx/sites-enabled/default

WORKDIR /app
COPY --from=backend-build /app/app.jar /app/app.jar
# Angular 21 gera os arquivos em dist/<projeto>/browser
COPY --from=frontend-build /app/dist/edicria-frontend/browser /usr/share/nginx/html
COPY docker/nginx.conf.template /etc/nginx/nginx.conf.template
COPY docker/entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

ENV PORT=10000 \
    JAVA_OPTS="-XX:MaxRAMPercentage=65 -XX:+UseSerialGC -Xss512k" \
    SPRING_JPA_HIBERNATE_DDL_AUTO=update

EXPOSE 10000
HEALTHCHECK --interval=30s --timeout=5s --start-period=90s --retries=3 \
  CMD curl -fs http://localhost:${PORT}/healthz || exit 1

ENTRYPOINT ["/entrypoint.sh"]
