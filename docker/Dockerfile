# Utilise une image Java 21 officielle légère
FROM openjdk:21-jdk-bookworm

# Définit le répertoire de travail dans le conteneur
WORKDIR /app

# Copie le JAR construit dans le conteneur
COPY app.jar app.jar

# Expose le port sur lequel tourne l'application (par défaut 8080)
EXPOSE 8080

# Commande pour lancer l'application
ENTRYPOINT ["java", "-jar", "app.jar"]
