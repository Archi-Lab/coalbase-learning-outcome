FROM openjdk:8-slim

CMD apt update
CMD apt install -y curl

EXPOSE 8080
COPY target/learning-outcome-0.0.1-SNAPSHOT.jar app-learning.jar
ENTRYPOINT ["java","-Dspring.profiles.active=prod","-jar","/app-learning.jar" ]
HEALTHCHECK --interval=1m --timeout=3s --retries=2 \
  CMD curl -f http://localhost:8080/ || exit 1

