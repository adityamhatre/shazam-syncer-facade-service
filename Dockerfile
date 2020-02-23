FROM openjdk:8-jre-alpine
COPY build/libs/shazamfacade-1.0.0.jar /shazamfacade.jar
CMD ["/usr/bin/java", "-agentlib:jdwp=transport=dt_socket,address=5005,server=y,suspend=n", "-jar", "/shazamfacade.jar"]
