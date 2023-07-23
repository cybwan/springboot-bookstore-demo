#!/usr/bin/env bash

DOCKER_REPO=addozhang

#mvn clean install -DskipTests
docker run -it --rm -v $PWD:/project -v /opt/maven-repo:/root/.m2 -w /project maven:3.8.1-openjdk-8-slim mvn clean install -DskipTests=true

if [ -n "$DOCKER_USERNAME" ] && [ -n "$DOCKER_PASSWORD" ]; then
    docker login -u $DOCKER_USERNAME -p $DOCKER_PASSWORD
    DOCKER_PUSH=true
fi

for module in bookwarehouse bookstore bookbuyer bookthief; do
  docker build --platform linux/arm64 --build-arg SERVICE_NAME=$module -t addozhang/$module:latest -f ./Dockerfile ./$module
  if [ "$DOCKER_PUSH" = true ]; then
      docker push $DOCKER_REPO/$module:latest
  fi
done
