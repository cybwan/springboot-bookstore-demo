#!make

DOCKER_BUILDX_PLATFORM ?= linux/amd64
DOCKER_BUILDX_OUTPUT ?= type=registry
CTR_REGISTRY ?= cybwan
CTR_TAG      ?= latest

.PHONY: docker-build-bookwarehouse
docker-build-bookwarehouse:
	docker buildx build --build-arg SERVICE_NAME=bookwarehouse --builder fsm --platform=$(DOCKER_BUILDX_PLATFORM) -o $(DOCKER_BUILDX_OUTPUT) -t $(CTR_REGISTRY)/springboot-demo-bookwarehouse:$(CTR_TAG) -f Dockerfile .

.PHONY: docker-build-bookstore
docker-build-bookstore:
	docker buildx build --build-arg SERVICE_NAME=bookstore --builder fsm --platform=$(DOCKER_BUILDX_PLATFORM) -o $(DOCKER_BUILDX_OUTPUT) -t $(CTR_REGISTRY)/springboot-demo-bookstore:$(CTR_TAG) -f Dockerfile .

.PHONY: docker-build-bookbuyer
docker-build-bookbuyer:
	docker buildx build --build-arg SERVICE_NAME=bookbuyer --builder fsm --platform=$(DOCKER_BUILDX_PLATFORM) -o $(DOCKER_BUILDX_OUTPUT) -t $(CTR_REGISTRY)/springboot-demo-bookbuyer:$(CTR_TAG) -f Dockerfile .

.PHONY: docker-build-bookthief
docker-build-bookthief:
	docker buildx build --build-arg SERVICE_NAME=bookthief --builder fsm --platform=$(DOCKER_BUILDX_PLATFORM) -o $(DOCKER_BUILDX_OUTPUT) -t $(CTR_REGISTRY)/springboot-demo-bookthief:$(CTR_TAG) -f Dockerfile .

.PHONY: docker-build-httpbin
docker-build-httpbin:
	docker buildx build --build-arg SERVICE_NAME=httpbin --builder fsm --platform=$(DOCKER_BUILDX_PLATFORM) -o $(DOCKER_BUILDX_OUTPUT) -t $(CTR_REGISTRY)/springboot-demo-httpbin:$(CTR_TAG) -f Dockerfile .

.PHONY: docker-build-curl
docker-build-curl:
	docker buildx build --build-arg SERVICE_NAME=curl --builder fsm --platform=$(DOCKER_BUILDX_PLATFORM) -o $(DOCKER_BUILDX_OUTPUT) -t $(CTR_REGISTRY)/springboot-demo-curl:$(CTR_TAG) -f Dockerfile .
