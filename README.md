# Messaging Server 
[![Build Status](https://travis-ci.org/zanni/messaging-server.svg?branch=master)](https://travis-ci.org/zanni/messaging-server)

Signal exchange implementation using java J2EE webapp, RabbitMQ message broker and websocket

## Design

Session model:
- userId
- listeningAddress: where to establish web stomp connection
- listeningKey: queue name user is allowed to subscribe to
- ack flag

workflow 
- User creates a session by sending HTTP request to webapp "/connect" endpoint 
- User then attempts to connect to RabbitMQ using session's data given by webapp in previous HTTP response.
- RabbitMQ use webapp as authentification/authorization backend using HTTP endpoints. 
- User create and subscribe to an auto-delete queue on RabbitMQ if webapp has an active session matching {userId, listeningAddress, listeningKey}
- User exchange messages with another connected user by sending HTTP request to webapp "/exchange" enpoint. 
- webapp then relay the message by pushing to corresponding user's queue
- webapp has a listener on RabbitMQ queue creation/deletion events. When a queue is created, corresponding session is acked, when a queue is deleted, corresponding session is deleted

## Browser compatibility consideration

RabbitMQ can expose both Websocket and SockJS queue. SockJS is provided for compatibility purpose with old browser as it fallback to long pooling/pooling when websocket is unavailable

## Scalability consideration

This was not the purpose of this demo, but such a system need a real persistence storage layer providing ACID transaction in order to persist session's. Doing so, webapp is a stateless service and therefore can be distributed using a load balancer. RabbitMQ is already a distributed technology and provide native clusterization. It can be noted that broker clusterization can be done at webapp layer using listeningAddress field of session model. More complexe charge repartition algorithm can therefore be implemented if required

## Max concurrent connection per node

I don't know how many concurrent connections a single rabbitmq node can handle or if RAM or file descriptors will be the bottleneck.
Responses could be found using tsung (https://github.com/processone/tsung) in order to simulate concurrent websocket connections

## Security 
What's missing:

Application:
- webapp endpoints should use SSL
- SSLv3 should be disabled
- webapp RabbitMQAuth endpoints should implement whitelist logic on rabbitmq nodes addresses (maybe they should be in another app, not visible by internet network)
- Non acked session should have a TTL and should be deleted at TTL expiration

System:
- SSH public key authentification enable, SSH password authentification disable, custom port
- ports 443 && 15674 opened to internet network

## Requirements

Following dependencies (with tested versions) are required to launch demo env:
 - Git (2.9.2)
 - Vagrant (1.7.1)
 - vagrant-omnibus (1.4.1)
 - Chef DK (0.16.28)
 - VirtualBox (4.3)

## Demo env

Create an ubuntu trusty64 VM using vagrant with RabbitMQ and MessagingServer provisioned

Host and VM are linked using a private network on 24/192.168.33.0, with host 192.168.33.1 and VM 192.168.33.10.
	
	git clone https://github.com/zanni/messaging-server.git
	cd messaging-server; chmod +x setupDemo.sh; ./setupDemo.sh


MessagingServer should be accessible in a browser at
	http://192.168.33.10:8080/





