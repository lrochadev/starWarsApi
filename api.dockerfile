FROM openjdk:11

EXPOSE 8080

ADD target/starWarsAPI.jar starWarsAPI.jar

ENTRYPOINT ["java","-Dspring.data.mongodb.uri=mongodb://mongodb/starwars_db","-jar","/starWarsAPI.jar"]