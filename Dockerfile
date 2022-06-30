FROM adoptopenjdk/openjdk11:x86_64-alpine-jre-11.0.15_10

COPY target/cms-benchmarking-app-*.jar app.jar

ENTRYPOINT java -jar app.jar