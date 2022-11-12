#FROM maven:3.8.1-openjdk-17
FROM registry.vaslapp.com/docker/maven:3.8.1-openjdk-17
COPY ./src/main/resources/application.properties /app/application.properties
WORKDIR /app
COPY ./target/core-bank-*.jar /app/core-bank.jar
EXPOSE 8081
CMD ["java", "-jar", "core-bank.jar", "--spring.config.location=application.properties"]
