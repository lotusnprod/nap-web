FROM eclipse-temurin:21-jdk

WORKDIR /app


RUN apt-get update && apt-get install -y unzip


COPY nap-web.zip /app/nap-web.zip
RUN mkdir temp && \
    unzip nap-web.zip -d temp && \
    mv temp/*/* . && \
    rm -rf temp web-nap.zip

RUN chmod +x bin/nap-web

ENTRYPOINT ["./bin/nap-web"]
