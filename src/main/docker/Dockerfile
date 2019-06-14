FROM openjdk:8-slim

CMD apt update
CMD apt install -y curl

EXPOSE 8080
COPY target/learning-outcome-0.0.1-SNAPSHOT.jar app-learning.jar
ENTRYPOINT ["java","-Xms128m","-Xmx750m","-Dspring.profiles.active=prod","-jar","/app-learning.jar" ]
