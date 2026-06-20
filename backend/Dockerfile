# Stage 1: Build the application using Maven and OpenJDK 17
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Stage 2: Deploy the compiled WAR to Apache Tomcat 10
FROM tomcat:10.1-jdk17-openjdk-slim

RUN rm -rf /usr/local/tomcat/webapps/*

COPY --from=build /app/target/backend.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
CMD ["catalina.sh", "run"]