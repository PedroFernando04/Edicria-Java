#!/bin/sh
set -e

# Monta a URL JDBC a partir de DB_HOST/DB_PORT/DB_NAME (usado no Render),
# a menos que SPRING_DATASOURCE_URL já tenha sido definida.
if [ -z "$SPRING_DATASOURCE_URL" ] && [ -n "$DB_HOST" ]; then
  export SPRING_DATASOURCE_URL="jdbc:postgresql://${DB_HOST}:${DB_PORT:-5432}/${DB_NAME:-edicria}"
fi
[ -n "$DB_USER" ]     && export SPRING_DATASOURCE_USERNAME="$DB_USER"
[ -n "$DB_PASSWORD" ] && export SPRING_DATASOURCE_PASSWORD="$DB_PASSWORD"

export PORT="${PORT:-10000}"
envsubst '${PORT}' < /etc/nginx/nginx.conf.template > /etc/nginx/nginx.conf

# Spring Boot em segundo plano (porta interna 8080)
java $JAVA_OPTS -Dserver.port=8080 -jar /app/app.jar &
JAVA_PID=$!

# Se o Java cair, derruba o contêiner para o orquestrador reiniciar
( while kill -0 $JAVA_PID 2>/dev/null; do sleep 5; done; echo "Spring Boot encerrou"; kill -TERM 1 ) &

# nginx em primeiro plano (PID principal)
exec nginx -g 'daemon off;'
