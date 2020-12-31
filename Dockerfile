FROM gcr.io/distroless/java:11

COPY build/libs/zuul-0.0.1-SNAPSHOT.jar ZuulApiGateway.jar

ENTRYPOINT ["java", "-jar", "ZuulApiGateway.jar"]