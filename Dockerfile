FROM openjdk:8-jre-slim

WORKDIR /opt/pixcell-clj/

COPY target/server.jar /opt/pixcell-clj/

CMD [ "java", "-jar", "/opt/pixcell-clj/server.jar" ]
