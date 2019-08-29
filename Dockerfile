FROM openjdk:8u212-jdk-alpine3.9

EXPOSE 8080

RUN apk -U add curl

ARG DEPENDENCY=target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app

ENTRYPOINT ["java","-Xms128m","-Xmx750m","-Dspring.profiles.active=prod","-cp","app:app/lib/*",\
  "io.archilab.coalbase.learningoutcomeservice.CoalbaseLearningOutcomeApplication"]
