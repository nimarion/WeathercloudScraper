FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /usr/src/app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -B

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /usr/src/app/target/WeathercloudScraper-*-SNAPSHOT-shaded.jar /app/weathercloud.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/weathercloud.jar"]