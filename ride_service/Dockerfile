FROM azul/zulu-openjdk-alpine:11
ARG JAVA_OPTS
ENV JAVA_OPTS=$JAVA_OPTS
COPY target/RideService-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT exec java $JAVA_OPTS -jar app.jar
# For Spring-Boot project, use the entrypoint below to reduce Tomcat startup time.
#ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar assg1.jar
