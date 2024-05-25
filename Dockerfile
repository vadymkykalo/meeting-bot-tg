FROM maven:3.8.1-openjdk-17-slim AS build

WORKDIR /app

RUN apt-get update && apt-get install -y dos2unix

COPY .env .env
RUN dos2unix .env

COPY . .

RUN set -a && . ./.env && mvn clean install

FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=build /app/target/meetingbot-0.0.1-SNAPSHOT.jar /app/meetingbot.jar
COPY tmp /app/tmp

EXPOSE 8080

CMD ["java", "-Dfile.encoding=UTF-8", "-jar", "/app/meetingbot.jar"]
