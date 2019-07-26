#!/usr/bin/env bash

# start service
docker-compose -p learning-outcome \
  -f "./src/main/docker/docker-compose.yml" \
  -f "./src/main/docker/docker-compose-dev.yml" \
  down