FROM docker.io/library/gradle:7.6-jdk17 AS builder

WORKDIR /app

COPY . .

# Run the DataDog agent download script
RUN chmod +x dd-java-agent-download.sh && ./dd-java-agent-download.sh

RUN gradle build --no-daemon

FROM docker.io/library/openjdk:17-jdk-slim

WORKDIR /app
COPY --from=builder /app/test-app/build/libs/test-app.jar ./test-app.jar
COPY --from=builder /opt/dd-java-agent.jar /opt/dd-java-agent.jar

ENTRYPOINT ["java", "-javaagent:/opt/dd-java-agent.jar", "-jar", "test-app.jar"]
