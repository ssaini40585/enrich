FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml ./
COPY common/pom.xml common/pom.xml
COPY demand-service/pom.xml demand-service/pom.xml
COPY staffing-service/pom.xml staffing-service/pom.xml
COPY allocation-service/pom.xml allocation-service/pom.xml
COPY common/src common/src
COPY demand-service/src demand-service/src
RUN mvn -pl demand-service -am clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /build/demand-service/target/demand-service-*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app/app.jar"]
