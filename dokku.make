include Makefile

TAG := dokku/${NAME}:latest
HOST := do

build:
	podman build --tag ${TAG} .

run:
	podman run --rm -p 8000:3000 ${TAG}

deploy: build
	podman save ${TAG} | ssh ${HOST} \
		"docker load | dokku tags:deploy ${NAME} latest"

.PHONY: build run deploy
