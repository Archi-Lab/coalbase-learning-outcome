FROM openjdk:11-slim

EXPOSE 8080
COPY learning-outcome-0.0.1-SNAPSHOT.jar app-learning.jar
ENTRYPOINT ["java","-Dspring.profiles.active=prod","-jar","/app-learning.jar" ]

