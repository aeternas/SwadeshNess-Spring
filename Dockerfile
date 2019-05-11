FROM openjdk:10
COPY build/libs/SwadeshNess-Application-0.0.1.jar .
CMD ["java", "-jar", "SwadeshNess-Application-0.0.1.jar", "--server.port=8082"]
