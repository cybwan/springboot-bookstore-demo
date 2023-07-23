## Bookstore Demo

This is a demo project for Bookstore implemented with SpringCloud. You can get the architecture of this project from the following picture.

![Architect](https://github.com/addozhang/bookstore-demo/assets/2224492/090e8e0a-d582-4974-9f03-42f0e21e8994)

Currently, it takes Consul as discovery server and will support Eureka or Nacos in the future.

### How to run

First of all, you need to start Consul server. You can follow [official doc](https://www.consul.io/downloads.html) and start it.

Or run it with docker:

```bash
docker run -d --name consul -p 8500:8500 consul:1.5.3
```

Then, you can start the project with the following command:

```bash
mvn clean install
mvn spring-boot:run -f bookwarehouse #port 14001
mvn spring-boot:run -f bookorder #port 14002
mvn spring-boot:run -f bookbuyer #port 14003
mvn spring-boot:run -f bookthief #port 14004
```

All modules will run with profiles `dev` and `consul` by default. You can change it with `-Dspring.profiles.active=xxx` option.

> `dev` profile will assign different port for each module. `consul` profile will register the service to Consul server.

### Build docker image

You can build docker image with the following command. Note, you should execute this command on x86_64 platform.

```bash
./build.sh
```

The script will push images to Docker Hub automically if you set `DOCKER_USERNAME` and `DOCKER_PASSWORD` environment variables in advance.

### Run in Kubernetes

```bash
kubectl create namespace bookstore
kubectl create namespace bookbuyer
kubectl create namespace bookthief
kubectl create namespace bookwarehouse

kubectl apply -n default -f manifets/consul.yaml

kubectl apply -n bookwarehouse -f manifests/bookwarehouse.yaml
kubectl apply -n bookstore -f manifests/bookstore.yaml
kubectl apply -n bookbuyer -f manifests/bookbuyer.yaml
kubectl apply -n bookthief -f manifests/bookthief.yaml
```