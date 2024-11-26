FROM docker.io/eclipse-temurin:21-jdk AS build

WORKDIR /app

RUN apt-get update && apt-get install -y unzip
RUN mkdir -p /app
COPY gradle /app/gradle
COPY gradle.properties gradlew /app/
# So we keep gradle  downloaded
RUN ./gradlew
COPY build.gradle.kts settings.gradle.kts /app/
COPY assets src gradle /app/assets/
COPY src /app/src/
RUN ./gradlew installDist

FROM docker.io/eclipse-temurin:21-jdk
WORKDIR /app
RUN mkdir -p /app
COPY --from=build /app/build/install/nap-web /app
ENV ENVIRONMENT=production
ENV SPARQL_SERVER=http://nap-sparql:3030/raw/sparql
ENTRYPOINT ["./bin/nap-web"]
