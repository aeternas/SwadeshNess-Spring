FROM openjdk:8-alpine
RUN apk --no-cache add curl
WORKDIR /home/circleci/repo/
COPY build/libs/swadeshness-0.0.1-SNAPSHOT.jar .
CMD ["java", "-jar", "swadeshness-0.0.1-SNAPSHOT.jar", "--server.port=8085", "--words.git.branch=master"]
HEALTHCHECK CMD curl -sSk http://localhost:8085/actuator/health || exit 1
