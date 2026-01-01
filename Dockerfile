# --- Étape 1 : Construction (Build) ---
# On utilise une image Maven pour compiler le code
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
# On génère le .jar (sans lancer les tests pour aller vite)
RUN mvn clean package -DskipTests

# --- Étape 2 : Exécution (Run) ---
# On utilise une image légère juste pour lancer Java
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# On récupère le .jar créé à l'étape 1
COPY --from=builder /app/target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
EXPOSE 8080