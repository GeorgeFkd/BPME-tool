FROM eclipse-temurin:17-alpine
WORKDIR /usr/app

COPY pom.xml .
RUN ls -l
RUN apk update && apk add maven && apk add openjdk17-jre-headless
RUN mvn clean package
RUN mvn package
COPY target/bpme-0.0.1-SNAPSHOT.jar /usr/app/app.jar
RUN chmod +x app.jar
CMD ["java", "-jar", "app.jar"]
EXPOSE 8080