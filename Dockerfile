FROM eclipse-temurin:21-jre-alpine
RUN apk update && apk upgrade
COPY target/bank-api-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
