FROM openjdk:8-alpine
WORKDIR /home/circleci/repo/
COPY build/libs/swadeshness-0.0.1-SNAPSHOT.jar .
CMD ["java", "-jar", "swadeshness-0.0.1-SNAPSHOT.jar", "--server.port=8082", "--words.git.branch=development"]
HEALTHCHECK CMD curl -sSk http://localhost:8082/actuator/health || exit 1
