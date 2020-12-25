FROM maven:3.6.3-openjdk-15 AS build  
COPY src /usr/src/app/src  
COPY pom.xml /usr/src/app  
RUN mvn -f /usr/src/app/pom.xml clean package

FROM openjdk:15-jdk-slim
COPY --from=build /usr/src/app/target/WeathercloudScraper-*-SNAPSHOT-shaded.jar weathercloud.jar

ENTRYPOINT ["java", "-jar", "weathercloud.jar" ]