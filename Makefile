#!/usr/bin/make

.DEFAULT_GOAL := help

help: ## Show this help
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {printf "  \033[36m%-15s\033[0m %s\n", $$1, $$2}' $(MAKEFILE_LIST)
	@echo "\n  Allowed for overriding next properties:\n\n\
	    Usage example:\n\
	        make run"

up: ## Start containers
	docker-compose up -d --build

down: ## Stop containers
	docker-compose down

remove: ## Remove offset file
	rm /tmp/offset.dat

restart: remove down up ## Remove offset file and recreate databases

