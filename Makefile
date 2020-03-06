NAME := pixcell-clj
VERSION := 0.1.0-SNAPSHOT

IN_JAR := target/${NAME}-${VERSION}-standalone.jar
OUT_JAR := target/server.jar

${OUT_JAR}: ${IN_JAR}
	cp ${IN_JAR} ${OUT_JAR}

${IN_JAR}:
	lein ring uberjar

.PHONY: server
server:
	@lein ring server-headless
