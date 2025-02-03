FROM openjdk:17-slim
COPY build/libs/*.jar app.jar
EXPOSE 8080 9010
ENTRYPOINT ["java", \
            "-Dcom.sun.management.jmxremote", \
            "-Dcom.sun.management.jmxremote.port=9010", \
            "-Dcom.sun.management.jmxremote.rmi.port=9010", \
            "-Dcom.sun.management.jmxremote.authenticate=false", \
            "-Dcom.sun.management.jmxremote.ssl=false", \
            "-Djava.rmi.server.hostname=0.0.0.0", \
            "-jar", \
            "app.jar"]