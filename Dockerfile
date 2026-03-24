FROM eclipse-temurin:21-jre-alpine

EXPOSE 8080

COPY target/starWarsAPI.jar starWarsAPI.jar

ENTRYPOINT ["java", "-jar", "/starWarsAPI.jar", "--spring.mongodb.uri=mongodb://mongodb/starwars_db"]
