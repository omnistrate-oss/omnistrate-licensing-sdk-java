all: build

.PHONY: build
build:
	@echo "Building..."
	@mvn verify