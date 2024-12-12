#!make

DOCKER_BUILDX_PLATFORM ?= linux/amd64
DOCKER_BUILDX_OUTPUT ?= type=registry
CTR_REGISTRY ?= cybwan
CTR_TAG      ?= latest

.PHONY: docker-build-consul
docker-build-consul:
	docker buildx build --builder fsm --platform=$(DOCKER_BUILDX_PLATFORM) -o $(DOCKER_BUILDX_OUTPUT) -t $(CTR_REGISTRY)/springboot-consul-demo:$(CTR_TAG) -f Dockerfile.consul .

.PHONY: docker-build-eureka
docker-build-eureka:
	docker buildx build --builder fsm --platform=$(DOCKER_BUILDX_PLATFORM) -o $(DOCKER_BUILDX_OUTPUT) -t $(CTR_REGISTRY)/springboot-eureka-demo:$(CTR_TAG) -f Dockerfile.eureka .

.PHONY: docker-build-nacos
docker-build-nacos:
	docker buildx build --builder fsm --platform=$(DOCKER_BUILDX_PLATFORM) -o $(DOCKER_BUILDX_OUTPUT) -t $(CTR_REGISTRY)/springboot-nacos-demo:$(CTR_TAG) -f Dockerfile.nacos .

.PHONY: docker-build-dubbo
docker-build-dubbo:
	docker buildx build --builder fsm --platform=$(DOCKER_BUILDX_PLATFORM) -o $(DOCKER_BUILDX_OUTPUT) -t $(CTR_REGISTRY)/springboot-dubbo-demo:$(CTR_TAG) -f Dockerfile.dubbo .

.PHONY: docker-build
docker-build: docker-build-consul docker-build-eureka docker-build-nacos docker-build-dubbo

.PHONY: jar-build
jar-build:
	rm -rf dist
	mkdir dist
	mvn clean package -P nacos
	cp curl/target/curl-0.0.1-SNAPSHOT.jar dist/curl-nacos.jar
	cp httpbin/target/httpbin-0.0.1-SNAPSHOT.jar dist/httpbin-nacos.jar
	cp bookthief/target/bookthief-0.0.1-SNAPSHOT.jar dist/bookthief-nacos.jar
	cp bookbuyer/target/bookbuyer-0.0.1-SNAPSHOT.jar dist/bookbuyer-nacos.jar
	cp bookstore/target/bookstore-0.0.1-SNAPSHOT.jar dist/bookstore-nacos.jar
	cp bookwarehouse/target/bookwarehouse-0.0.1-SNAPSHOT.jar dist/bookwarehouse-nacos.jar
	mvn clean package -P eureka
	cp curl/target/curl-0.0.1-SNAPSHOT.jar dist/curl-eureka.jar
	cp httpbin/target/httpbin-0.0.1-SNAPSHOT.jar dist/httpbin-eureka.jar
	cp bookthief/target/bookthief-0.0.1-SNAPSHOT.jar dist/bookthief-eureka.jar
	cp bookbuyer/target/bookbuyer-0.0.1-SNAPSHOT.jar dist/bookbuyer-eureka.jar
	cp bookstore/target/bookstore-0.0.1-SNAPSHOT.jar dist/bookstore-eureka.jar
	cp bookwarehouse/target/bookwarehouse-0.0.1-SNAPSHOT.jar dist/bookwarehouse-eureka.jar
	mvn clean package -P consul
	cp curl/target/curl-0.0.1-SNAPSHOT.jar dist/curl-consul.jar
	cp httpbin/target/httpbin-0.0.1-SNAPSHOT.jar dist/httpbin-consul.jar
	cp bookthief/target/bookthief-0.0.1-SNAPSHOT.jar dist/bookthief-consul.jar
	cp bookbuyer/target/bookbuyer-0.0.1-SNAPSHOT.jar dist/bookbuyer-consul.jar
	cp bookstore/target/bookstore-0.0.1-SNAPSHOT.jar dist/bookstore-consul.jar
	cp bookwarehouse/target/bookwarehouse-0.0.1-SNAPSHOT.jar dist/bookwarehouse-consul.jar
	mvn clean package -P dubbo
	cp curl/target/curl-0.0.1-SNAPSHOT.jar dist/curl-dubbo.jar
	cp httpbin/target/httpbin-0.0.1-SNAPSHOT.jar dist/httpbin-dubbo.jar
	cp httpbin/target/httpbin-0.0.1-SNAPSHOT.jar dist/httpbin-dubbo.jar
	cp bookthief/target/bookthief-0.0.1-SNAPSHOT.jar dist/bookthief-dubbo.jar
	cp bookbuyer/target/bookbuyer-0.0.1-SNAPSHOT.jar dist/bookbuyer-dubbo.jar
	cp bookstore/target/bookstore-0.0.1-SNAPSHOT.jar dist/bookstore-dubbo.jar
	cp bookwarehouse/target/bookwarehouse-0.0.1-SNAPSHOT.jar dist/bookwarehouse-dubbo.jar