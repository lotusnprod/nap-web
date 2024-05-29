FROM eclipse-temurin:21-jdk AS build

WORKDIR /app


RUN apt-get update && apt-get install -y unzip
RUN mkdir -p /app
COPY gradle /app/gradle
COPY build.gradle.kts gradle.properties gradlew settings.gradle.kts /app/
# So we keep gradle  downloaded
RUN ./gradlew
COPY assets src gradle /app/assets/
COPY src /app/src/
RUN ./gradlew installDist

FROM eclipse-temurin:21-jdk
WORKDIR /app
RUN mkdir -p /app
COPY --from=build /app/build/install/nap-web /app
ENTRYPOINT ["./bin/nap-web"]
