#!make

DOCKER_BUILDX_PLATFORM ?= linux/amd64
DOCKER_BUILDX_OUTPUT ?= type=registry
CTR_REGISTRY ?= cybwan
CTR_TAG      ?= latest

.PHONY: docker-build
docker-build:
	docker buildx build --builder fsm --platform=$(DOCKER_BUILDX_PLATFORM) -o $(DOCKER_BUILDX_OUTPUT) -t $(CTR_REGISTRY)/smartdns-nacos-httpbin-demo-1:$(CTR_TAG) -f Dockerfile .
