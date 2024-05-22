FROM maven:3.8.1-openjdk-17 AS build

WORKDIR /app

COPY . .
COPY .env .env

# Set environment variables from .env file
RUN export $(cat .env | xargs) && mvn clean install

FROM openjdk:17-alpine

WORKDIR /app

COPY --from=build /app/target/meetingbot-0.0.1-SNAPSHOT.jar /app/meetingbot.jar
COPY tmp /app/tmp

EXPOSE 8080

CMD ["java", "-Dfile.encoding=UTF-8", "-jar", "/app/meetingbot.jar"]
