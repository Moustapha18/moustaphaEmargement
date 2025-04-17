# Étape 1 : Construire l'application avec Maven
FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Étape 2 : Exécuter l'application avec OpenJDK
FROM eclipse-temurin:21
WORKDIR /app
COPY --from=build /app/target/projetdemargement-1.0-SNAPSHOT-jar-with-dependencies.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
