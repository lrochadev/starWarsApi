FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN apk add --no-cache maven && mvn -B package -DskipTests

FROM eclipse-temurin:21-jre-alpine
EXPOSE 8080
COPY --from=builder /build/target/starWarsAPI.jar starWarsAPI.jar
ENTRYPOINT ["java", "-jar", "/starWarsAPI.jar"]
