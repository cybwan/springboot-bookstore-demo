#!make

CTR_REGISTRY = cybwan
CTR_TAG      = v2
DOCKER_BUILDX_OUTPUT ?= type=registry
DOCKER_BUILDX_PLATFORM ?= linux/amd64

.PHONY: docker-build-fgw-dubbo-demo
docker-build-fgw-dubbo-demo:
	docker buildx build --builder fsm --platform=$(DOCKER_BUILDX_PLATFORM) -o $(DOCKER_BUILDX_OUTPUT) -t $(CTR_REGISTRY)/fgw-dubbo-demo:$(CTR_TAG) -f Dockerfile.demo .

docker-build-cross-fgw-dubbo-demo: DOCKER_BUILDX_PLATFORM=linux/amd64,linux/arm64
docker-build-cross-fgw-dubbo-demo: docker-build-fgw-dubbo-demo