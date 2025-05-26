# Usa una imagen oficial de Java con Maven para construir el proyecto
# Build con Maven
FROM maven:3.9.3-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Imagen final
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copia el .jar
COPY --from=build /app/target/*.jar app.jar

# Copia la base de datos SQLite
COPY db/simuladorBancario.db ./db/simuladorBancario.db

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
