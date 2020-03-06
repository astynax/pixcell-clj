include Makefile

TAG := dokku/${NAME}:latest
HOST := do

build:
	docker build --tag ${TAG} .

run:
	docker run --rm -p 8000:3000 ${TAG}

deploy: build
	docker save ${TAG} | ssh ${HOST} \
		"docker load | dokku tags:deploy ${NAME} latest"

.PHONY: build run deploy
