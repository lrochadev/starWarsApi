FROM java:8

EXPOSE 8080

ADD target/starWarsAPI.jar starWarsAPI.jar

ENTRYPOINT ["java","-Dspring.data.mongodb.uri=mongodb://mongodb/starwars_db", "-Djava.security.egd=file:/dev/./urandom","-jar","/starWarsAPI.jar"]