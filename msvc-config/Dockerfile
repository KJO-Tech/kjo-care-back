FROM maven:3.8.6-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
VOLUME /tmp
COPY --from=build /workspace/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]