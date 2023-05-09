FROM adoptopenjdk/openjdk11:alpine-jre
WORKDIR /app
COPY target/bpme-0.0.1-SNAPSHOT.jar app.jar
COPY pom.xml .
RUN ls -l /app
RUN apk update && apk add maven
RUN mvn clean package
RUN mvn package

CMD ["java", "-jar", "/app.jar"]
EXPOSE 8080