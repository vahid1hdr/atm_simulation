#FROM maven:3.8.1-openjdk-17
FROM registry.vaslapp.com/docker/maven:3.8.1-openjdk-17
COPY ./src/main/resources/application.properties /app/application.properties
WORKDIR /app
COPY ./target/bank-client-*.jar /app/bank-client.jar
EXPOSE 8082
CMD ["java", "-jar", "bank-client.jar", "--spring.config.location=application.properties"]
