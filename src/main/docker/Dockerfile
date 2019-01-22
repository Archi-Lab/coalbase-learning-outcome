FROM openjdk:8-slim

EXPOSE 8080
COPY target/learning-outcome-0.0.1-SNAPSHOT.jar app-learning.jar
ENTRYPOINT ["java","-Dspring.profiles.active=prod","-jar","/app-learning.jar" ]

