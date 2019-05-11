FROM openjdk:10
WORKDIR /home/circleci/repo/
COPY build/libs/swadeshness-0.0.1-SNAPSHOT.jar .
CMD ["java", "-jar", "swadeshness-0.0.1-SNAPSHOT.jar", "--server.port=8082"]
