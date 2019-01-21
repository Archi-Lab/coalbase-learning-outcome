FROM openjdk:11-slim

EXPOSE 8080
COPY coalbase-learning-outcome-0.0.1-SNAPSHOT.jar app-learning.jar
ENTRYPOINT ["java","-Djasypt.encryptor.password=_CoalbaseLearningOutcomeMS_2017","-Dspring.profiles.active=prod","-Djava.security.egd=file:/dev/./urandom","-jar","/app-learning.jar" ]

