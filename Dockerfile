FROM adoptopenjdk/openjdk11:alpine-jre

COPY wait-for.sh wait-for.sh
COPY build/libs/zuul-0.0.1-SNAPSHOT.jar ZuulApiGateway.jar

#ENTRYPOINT ["java", "-jar", "ZuulApiGateway.jar"]
ENV DISCOVERY_HOST=""
ENTRYPOINT ["sh", "-c", "echo 'waiting for 40 seconds for '${DISCOVERY_HOST:localhost}':8010 to be accessable before starting application' && /wait-for.sh -t 40 ''${DISCOVERY_HOST:localhost}':8010' -- java -jar ZuulApiGateway.jar"]
