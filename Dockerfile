FROM openjdk:21

RUN mkdir -p /musicApp/tracks

RUN chown -R 1000:1000 /musicApp/tracks

ADD target/MusicApp-1.0.0-MVP.jar /musicApp/

WORKDIR /musicApp/

CMD [ "java", "-jar", "MusicApp-1.0.0-MVP.jar"]