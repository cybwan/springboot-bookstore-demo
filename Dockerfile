FROM maven:3-openjdk-8 AS builder
WORKDIR /app
COPY . .

ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v1.28.0/opentelemetry-javaagent.jar .

RUN --mount=type=cache,target=/root/.m2 mvn clean package -P nacos
RUN mv /app/curl/target/curl-0.0.1-SNAPSHOT.jar curl-nacos.jar
RUN mv /app/httpbin/target/httpbin-0.0.1-SNAPSHOT.jar httpbin-nacos.jar
RUN mv /app/bookthief/target/bookthief-0.0.1-SNAPSHOT.jar bookthief-nacos.jar
RUN mv /app/bookbuyer/target/bookbuyer-0.0.1-SNAPSHOT.jar bookbuyer-nacos.jar
RUN mv /app/bookstore/target/bookstore-0.0.1-SNAPSHOT.jar bookstore-nacos.jar
RUN mv /app/bookwarehouse/target/bookwarehouse-0.0.1-SNAPSHOT.jar bookwarehouse-nacos.jar

RUN --mount=type=cache,target=/root/.m2 mvn clean package -P eureka
RUN mv /app/curl/target/curl-0.0.1-SNAPSHOT.jar curl-eureka.jar
RUN mv /app/httpbin/target/httpbin-0.0.1-SNAPSHOT.jar httpbin-eureka.jar
RUN mv /app/bookthief/target/bookthief-0.0.1-SNAPSHOT.jar bookthief-eureka.jar
RUN mv /app/bookbuyer/target/bookbuyer-0.0.1-SNAPSHOT.jar bookbuyer-eureka.jar
RUN mv /app/bookstore/target/bookstore-0.0.1-SNAPSHOT.jar bookstore-eureka.jar
RUN mv /app/bookwarehouse/target/bookwarehouse-0.0.1-SNAPSHOT.jar bookwarehouse-eureka.jar

RUN --mount=type=cache,target=/root/.m2 mvn clean package -P consul
RUN mv /app/curl/target/curl-0.0.1-SNAPSHOT.jar curl-consul.jar
RUN mv /app/httpbin/target/httpbin-0.0.1-SNAPSHOT.jar httpbin-consul.jar
RUN mv /app/bookthief/target/bookthief-0.0.1-SNAPSHOT.jar bookthief-consul.jar
RUN mv /app/bookbuyer/target/bookbuyer-0.0.1-SNAPSHOT.jar bookbuyer-consul.jar
RUN mv /app/bookstore/target/bookstore-0.0.1-SNAPSHOT.jar bookstore-consul.jar
RUN mv /app/bookwarehouse/target/bookwarehouse-0.0.1-SNAPSHOT.jar bookwarehouse-consul.jar

RUN --mount=type=cache,target=/root/.m2 mvn clean package -P dubbo
RUN mv /app/curl/target/curl-0.0.1-SNAPSHOT.jar curl-dubbo.jar
RUN mv /app/httpbin/target/httpbin-0.0.1-SNAPSHOT.jar httpbin-dubbo.jar
RUN mv /app/bookthief/target/bookthief-0.0.1-SNAPSHOT.jar bookthief-dubbo.jar
RUN mv /app/bookbuyer/target/bookbuyer-0.0.1-SNAPSHOT.jar bookbuyer-dubbo.jar
RUN mv /app/bookstore/target/bookstore-0.0.1-SNAPSHOT.jar bookstore-dubbo.jar
RUN mv /app/bookwarehouse/target/bookwarehouse-0.0.1-SNAPSHOT.jar bookwarehouse-dubbo.jar

FROM openjdk:8-jre-alpine

WORKDIR /

RUN apk add --update bash
RUN apk add --update curl
RUN rm -rf /var/cache/apk/*

COPY --from=builder /app/*.jar .