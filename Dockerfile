FROM eclipse-temurin:17-jdk-alpine
ADD target/shop-app-0.0.1-SNAPSHOT.jar shop-app.jar
ENTRYPOINT ["java","-jar","shop-app.jar"]
EXPOSE 8080