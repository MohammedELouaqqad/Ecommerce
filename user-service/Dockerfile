# Étape 1 : Utiliser une image Java 17 pour construire l'application
FROM maven:3.9.4-eclipse-temurin-17 AS builder

# Définir le dossier de travail dans Docker
WORKDIR /app

# Copier le pom.xml et télécharger les dépendances (pour accélérer les futurs builds)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copier le reste du code source
COPY src ./src

# Construire le fichier .jar (en sautant les tests pour aller plus vite)
RUN mvn clean package -DskipTests

# ==========================================

# Étape 2 : Utiliser une image plus légère juste pour faire tourner le .jar
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copier le .jar construit à l'étape 1
COPY --from=builder /app/target/*.jar app.jar

# Exposer le port 8081
EXPOSE 8081

# Lancer l'application
ENTRYPOINT ["java", "-jar", "app.jar"]