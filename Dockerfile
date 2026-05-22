FROM eclipse-temurin:17-jre
COPY build/libs/sellers-rest-service-*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]