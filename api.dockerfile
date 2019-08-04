FROM java:8

EXPOSE 8080

ADD target/starWarsAPI.jar starWarsAPI.jar

ENTRYPOINT ["java","-jar","starWarsAPI.jar"]