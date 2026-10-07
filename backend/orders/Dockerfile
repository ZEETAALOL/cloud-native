# Etapa 1: Build
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copiar solo pom.xml primero para aprovechar caché de Docker
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar código fuente y compilar
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Crear usuario no-root para seguridad
RUN addgroup -g 1001 -S appgroup && adduser -u 1001 -S appuser -G appgroup

# Copiar JAR desde etapa de build
COPY --from=build /app/target/*.jar app.jar

# Cambiar a usuario no-root
USER appuser

# Exponer puerto
EXPOSE 8081

# Variables de entorno por defecto
ENV SPRING_PROFILES_ACTIVE=default
ENV JAVA_OPTS="-Xmx256m -Xms128m"

# Healthcheck
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8081/clientes || exit 1

# Comando de inicio
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
