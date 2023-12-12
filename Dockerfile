FROM maven:3-openjdk-8 AS builder
WORKDIR /app
COPY . .
RUN --mount=type=cache,target=/root/.m2 mvn clean package

FROM openjdk:8-jre-alpine
ARG SERVICE_NAME

WORKDIR /

RUN apk add --update bash
RUN apk add --update curl && rm -rf /var/cache/apk/*

COPY --from=builder /app/curl/target/curl-0.0.1-SNAPSHOT.jar .
COPY --from=builder /app/httpbin/target/httpbin-0.0.1-SNAPSHOT.jar .
COPY --from=builder /app/bookthief/target/bookthief-0.0.1-SNAPSHOT.jar .
COPY --from=builder /app/bookbuyer/target/bookbuyer-0.0.1-SNAPSHOT.jar .
COPY --from=builder /app/bookstore/target/bookstore-0.0.1-SNAPSHOT.jar .
COPY --from=builder /app/bookwarehouse/target/bookwarehouse-0.0.1-SNAPSHOT.jar .

ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v1.28.0/opentelemetry-javaagent.jar .

ENV JAVA_OPTS="-Xms256M -Xmx512M"
ENV JAVA_TOOL_OPTIONS "-javaagent:./opentelemetry-javaagent.jar -Dotel.resource.attributes=service.name=$SERVICE_NAME}"
ENTRYPOINT ["java","-Dotel.traces.exporter=logging","-Dotel.metrics.exporter=none","-Dotel.propagators=tracecontext,baggage,b3multi","-jar","$SERVICE_NAME-0.0.1-SNAPSHOT.jar"]