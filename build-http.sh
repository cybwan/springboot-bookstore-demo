#!/usr/bin/env bash

set -e

DOCKER_REPO=addozhang
TAG=0.3

if [ -n "$DOCKER_USERNAME" ] && [ -n "$DOCKER_PASSWORD" ]; then
    docker login -u $DOCKER_USERNAME -p $DOCKER_PASSWORD
    DOCKER_PUSH=true
fi

for p in eureka consul nacos; do
  docker run -it --rm -v $PWD:/project -v /opt/maven-repo:/root/.m2 -w /project maven:3.8.6-openjdk-8-slim mvn clean install -DskipTests=true -P $p
  for module in bookwarehouse bookstore bookbuyer bookthief curl httpbin gateway; do
    if [ "$DOCKER_PUSH" = true ]; then
        docker buildx build --platform linux/amd64,linux/arm64 --build-arg SERVICE_NAME=$module -t addozhang/$module-$p:$TAG -f ./Dockerfile ./$module --push
    else
        docker buildx build --platform linux/amd64,linux/arm64 --build-arg SERVICE_NAME=$module -t addozhang/$module-$p:$TAG -f ./Dockerfile ./$module
    fi
  done
done

