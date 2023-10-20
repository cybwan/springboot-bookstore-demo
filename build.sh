#!/usr/bin/env bash

DOCKER_REPO=addozhang

if [ -n "$DOCKER_USERNAME" ] && [ -n "$DOCKER_PASSWORD" ]; then
    docker login -u $DOCKER_USERNAME -p $DOCKER_PASSWORD
    DOCKER_PUSH=true
fi

for p in consul eureka; do
  docker run -it --rm -v $PWD:/project -v /opt/maven-repo:/root/.m2 -w /project maven:3.8.1-openjdk-8-slim mvn clean install -DskipTests=true -P $p
  for module in bookwarehouse bookstore bookbuyer bookthief; do
    docker buildx build --build-arg SERVICE_NAME=$module -t addozhang/$module-$p:latest -f ./Dockerfile ./$module
    if [ "$DOCKER_PUSH" = true ]; then
        docker push $DOCKER_REPO/$module-$p:latest
    fi
  done
done

