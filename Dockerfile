FROM gcr.io/distroless/java:8
ARG SERVICE_NAME

WORKDIR /app
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v1.28.0/opentelemetry-javaagent.jar .
COPY ./target/*.jar /app/app.jar

ENV JAVA_OPTS="-Xms256M -Xmx512M"
#ENV JAVA_TOOL_OPTIONS "-javaagent:./opentelemetry-javaagent.jar -Dotel.resource.attributes=service.name=$SERVICE_NAME}"
#ENTRYPOINT ["java","-Dotel.traces.exporter=logging","-Dotel.metrics.exporter=none","-Dotel.propagators=tracecontext,baggage,b3multi","-jar","app.jar"]
ENTRYPOINT ["java","-jar","app.jar"]