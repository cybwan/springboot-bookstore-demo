# Bookstore Demo

This is a demo project for Bookstore implemented with SpringCloud. You can get the architecture of this project from the following picture.

![Architect](docs/springboot-consul+eureka.png)

Currently, it takes Consul and Eureka as discovery server and will support Nacos or others in the future.

At the same time, the communication among services supports HTTP and gRPC. `bookstore/bookwarehouse` exposes HTTP and gRPC services, `bookbuyer/bookthief` only exposes HTTP service.

**By default `bookbuyer/bookthief` calls `bookstore` via HTTP. In order to switch to gRPC, an environment `protocol=grpc` is required.**

## How to run

This demo supports two discovery servers: Consul and Eureka. You can choose one of them to run.

### Maven profile

You need to specify the profile for both Maven and Spring Boot. The profile can be `consul`, `eureka` or `nacos`. Such as:

```bash
# enable Consul dependencies
mvn clean install -P consul 
# enable Eureka dependencies
mvn clean install -P eureka 
# enable Naocs dependencies
mvn clean install -P nacos 
```

### Spring Boot profile

All modules will run with profiles `dev` and `consul` by default. You can change it with `-Dspring.profiles.active=xxx` option (it's `-Dspring-boot.run.profiles` with `mvn spring-boot:run`).

> - `dev` profile will assign different port for each module.
> - `prod` profile will assign same HTTP port `14001` for each module. It suits for running in Kubernetes. The bookstore and bookwarehouse also listen on gRPC port `9090`.
> - `consul` profile will register the service to Consul server. Combining with `dev` profile, its address is `localhost:8500`. Combining with `prod` profile, its address is `consul.default:8500`.
> - `eureka` profile will register the service to Eureka server. Combining with `dev` profile, its address is `localhost:8761`. Combining with `prod` profile, its address is `eureka.default:8761`.
> - `nacos` profile will register the service to Nacos server. Combining with `dev` profile, its address is `localhost:8848`. Combining with `prod` profile, its address is `nacos.default:8848`.


```bash
# enable Consul profile
mvn spring-boot:run -f bookwarehouse -P consul -Dspring-boot.run.profiles=consule,dev
# enable Eureka profile
mvn spring-boot:run -f bookwarehouse -P eureka -Dspring-boot.run.profiles=eureka,dev
# enable Nacos profile
mvn spring-boot:run -f bookwarehouse -P nacos -Dspring-boot.run.profiles=nacos,dev
```

### Consul

First of all, you need to start Consul server. You can follow [official doc](https://www.consul.io/downloads.html) and start it.

Or run it with docker:

```bash
docker run -d --name consul -p 8500:8500 consul:1.5.3
```

Then, you can start the project with the following command:

```bash
mvn spring-boot:run -f bookwarehouse -P consul -Dspring-boot.run.profiles=consule,dev
mvn spring-boot:run -f bookstore -P consul -Dspring-boot.run.profiles=consule,dev
mvn spring-boot:run -f bookbuyer -P consul -Dspring-boot.run.profiles=consule,dev
```

### Eureka

It's same to start Eureka server first. You can choose [official doc](https://spring.io/projects/spring-cloud-netflix) to run it, or run with Docker

```bash
docker run -d --name eureka -p 8761:8761 flomesh/samples-discovery-server:latest
```

Then, you can start the project with the following command:

```bash
mvn spring-boot:run -f bookwarehouse -P eureka -Dspring-boot.run.profiles=eureka,dev
mvn spring-boot:run -f bookstore -P eureka -Dspring-boot.run.profiles=eureka,dev
mvn spring-boot:run -f bookbuyer -P eureka -Dspring-boot.run.profiles=eureka,dev
```

### Nacos

Referring to [official doc](https://nacos.io/en-us/docs/v2/quickstart/quick-start-docker.html), start a Nacos server with Docker:

```bash
docker run --rm --name nacos -e MODE=standalone -p 8848:8848 nacos/nacos-server:v2.3.0-slim
```

Then, you can start the project with the following command:

```bash
mvn spring-boot:run -f bookwarehouse -P nacos -Dspring-boot.run.profiles=nacos,dev
mvn spring-boot:run -f bookstore -P nacos -Dspring-boot.run.profiles=nacos,dev
mvn spring-boot:run -f bookbuyer -P nacos -Dspring-boot.run.profiles=nacos,dev
```

### Dubbo

Start a Zookeeper server with Docker:

```bash
docker run --rm --name zookeeper -p 2181:2181 zookeeper
```

Then, you can start the project with the following command:

```bash
mvn spring-boot:run -f bookwarehouse -P dubbo -Dspring-boot.run.profiles=dubbo,dev
mvn spring-boot:run -f bookstore -P dubbo -Dspring-boot.run.profiles=dubbo,dev
mvn spring-boot:run -f bookbuyer -P dubbo -Dspring-boot.run.profiles=dubbo,dev
```

```bash
mvn clean package -P dubbo

java -jar bookwarehouse/target/bookwarehouse-0.0.1-SNAPSHOT.jar --spring.profiles.active=dubbo,dev
java -jar bookstore/target/bookstore-0.0.1-SNAPSHOT.jar --spring.profiles.active=dubbo,dev
java -jar bookbuyer/target/bookbuyer-0.0.1-SNAPSHOT.jar --spring.profiles.active=dubbo,dev
```

## Build docker image

You can build docker image with the following command. Note, you should execute this command on x86_64 platform.

It will build and push images for all modules for both Consul and Eureka on one execution. 

```bash
./build-http.sh #for http
./build-dubbo.sh #for dubbo
```

The script will push images to Docker Hub automatically if you set `DOCKER_USERNAME` and `DOCKER_PASSWORD` environment variables in advance.

### Run in Kubernetes

Running in Kubernetes is very easy. You can run it with the following command: 

```bash
kubectl create namespace bookstore
kubectl create namespace bookbuyer
kubectl create namespace bookwarehouse

kubectl apply -n default -f manifets/consul.yaml

kubectl apply -n bookwarehouse -f manifests/consul/bookwarehouse-consul.yaml
kubectl apply -n bookstore -f manifests/consul/bookstore-consul.yaml
kubectl apply -n bookbuyer -f manifests/consul/bookbuyer-consul.yaml
```

All applications will running in `prod` profile. 

**You can try with Eureka by changing `consul` to `eureka` in the above commands.**
