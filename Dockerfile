FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn -B package -DskipTests

FROM eclipse-temurin:21-jre-alpine
EXPOSE 8080
COPY --from=builder /build/target/starWarsAPI.jar starWarsAPI.jar
ENTRYPOINT ["java", "-jar", "/starWarsAPI.jar"]
