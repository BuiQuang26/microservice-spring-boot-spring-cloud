#!/usr/bin/env bash

echo "Build file jar"
mvn clean install

echo "Build image Discover Server"
Docker build -t discovery-server:1.0.0 ./DiscoveryServer/

echo "Build image Gateway api service"
Docker build -t gateway-service:1.0.0 ./ApiGateway/

echo "Build image authentication service"
Docker build -t authentication-service:1.0.0 ./authentication/

echo "Build image user service"
Docker build -t user-service:1.0.0 ./userService/
