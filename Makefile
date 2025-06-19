#!make

BASE_DIR = $(dir $(abspath $(lastword $(MAKEFILE_LIST))))

DUBBO_IP_TO_REGISTRY   ?= 172.22.1.11
DUBBO_PORT_TO_REGISTRY ?= 6666

XMS ?= 16G
XMX ?= 16G
XSS ?= 256k

.PHONY: zk
zk:
	@if [ ! -d apache-zookeeper-3.6.2-bin ]; then \
	  tar zxf apache-zookeeper-3.6.2-bin.tar.gz; \
		mv apache-zookeeper-3.6.2-bin/conf/zoo_sample.cfg apache-zookeeper-3.6.2-bin/conf/zoo.cfg; \
	fi

.PHONY: fgw
fgw:
	@if [ ! -d fgw ]; then \
	  git clone git@github.com:flomesh-io/fgw.git; \
	fi

.PHONY: start-zk
start-zk: zk
	@apache-zookeeper-3.6.2-bin/bin/zkServer.sh start
	@sleep 3s
	@apache-zookeeper-3.6.2-bin/bin/zkServer.sh status | grep 2181

.PHONY: stop-zk
stop-zk: zk
	@apache-zookeeper-3.6.2-bin/bin/zkServer.sh stop

.PHONY: start-fgw-httpbin
start-fgw-httpbin:
	@export SPRING_CONFIG_LOCATION=file://$(BASE_DIR)config.httpbin/; \
	java -Xms$(XMS) -Xmx$(XMX) -Xss$(XSS) \
	-DDUBBO_IP_TO_REGISTRY=$(DUBBO_IP_TO_REGISTRY) \
	-DDUBBO_PORT_TO_REGISTRY=$(DUBBO_PORT_TO_REGISTRY) \
	-jar httpbin-dubbo.jar --spring.profiles.active=dubbo,dev

.PHONY: start-httpbin
start-httpbin:
	@export SPRING_CONFIG_LOCATION=file://$(BASE_DIR)config.httpbin/; \
	java -Xms$(XMS) -Xmx$(XMX) -Xss$(XSS) \
	-jar httpbin-dubbo.jar --spring.profiles.active=dubbo,dev

.PHONY: start-curl
start-curl:
	@export SPRING_CONFIG_LOCATION=file://$(BASE_DIR)config.curl/; \
	java -Xms$(XMS) -Xmx$(XMX) -Xss$(XSS) \
	-XX:MaxGCPauseMillis=200 \
	-XX:ParallelGCThreads=8 \
	-XX:+UseG1GC \
	-Ddubbo.netty.preferDirect=true \
	-Ddubbo.memory.watermark.high=0.7 \
	-jar curl-dubbo.jar --spring.profiles.active=dubbo,dev

.PHONY: start-fgw
start-fgw: fgw
	@ulimit -n 65536
	@pipy --admin-port=6060  fgw/src/main.js --reuse-port --threads=max --args --config dubbo-route.yaml

CTR_REGISTRY ?= cybwan
CTR_TAG      ?= latest
DOCKER_BUILDX_OUTPUT ?= type=registry
DOCKER_BUILDX_PLATFORM ?= linux/amd64

.PHONY: docker-build-fgw-dubbo-demo
docker-build-fgw-dubbo-demo:
	docker buildx build --builder fsm --platform=$(DOCKER_BUILDX_PLATFORM) -o $(DOCKER_BUILDX_OUTPUT) -t $(CTR_REGISTRY)/fgw-dubbo-demo:$(CTR_TAG) -f Dockerfile.demo .

docker-build-cross-fgw-dubbo-demo: DOCKER_BUILDX_PLATFORM=linux/amd64,linux/arm64
docker-build-cross-fgw-dubbo-demo: docker-build-fgw-dubbo-demo