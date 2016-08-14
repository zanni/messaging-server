# Messaging Server 
[![Build Status](https://travis-ci.org/zanni/messaging-server.svg?branch=master)](https://travis-ci.org/zanni/messaging-server)

WebRTC compliant signal exchange implementation based on HTTP and STOMP protocols

<p align="center">
  <img src="https://github.com/zanni/messaging-server/raw/master/webrtc.png?raw=true" alt="Sublime's custom image"/>
</p>

	1: User A send message M for User B to server S
	2: S relay M to B
	3: B send response M2 for A to S
	4: S relay M2 to A
	5/6: Bidirectionnal peer connections are established

This prototype permits communications 1 to 4

## Implementation

**Session Model** {userId, listeningAddress, listeningKey, ack} with:
- **userId**: user id (authentification and authorization of user connecting to MessagingServer is out of the scope of this project)
- **listeningAddress**: web STOMP host address
- **listeningKey**: routing key user is allowed to subscribe to
- **ack**: session is acked when web STOMP connection have been settled between MessagingServer and User

**Workflow**:
- User inits connection by sending HTTP POST request to "/connect" endpoint, providing {userId}. A session is generated with a random {listeningKey} and an available {listeningAddress}.
- User then attempts to connect to STOMP "/ws" endpoint using session's data given by webapp in previous HTTP response. If an active session matches {userId, listeningAddress, listeningKey}, an auto-delete exclusive queue is created and user subscribe to that queue
- User exchanges messages with others connected users by sending HTTP POST requestS to "/exchange" enpoint. Messages are relayed by pushing on corresponding users queues
- User disconnects by either sending HTTP POST request to "/disconnect" endpoint or broking websocket TCP connection
- Queue creation/deletion events are monitored. When a queue is created, corresponding session is acked, when a queue is deleted, corresponding session is deleted

## Technologies
- Java J2EE (8) Spring (4.x) webapp with embedded Jetty
- RabbitMQ Message Broker (3.6.x)
- Chef/Vagrant for devops purpose

## Browser Compatibility 
tested with Chrome 52 & Safari 9

Refering to: http://caniuse.com/#feat=websockets, More than 90% of users (desktop/mobile) worldwide (96% in France) use a browser compatible with websocket HTML5 API and therefore can access MessagingServer using websocket

RabbitMQ can expose both Websocket and SockJS enpoints. SockJS is provided for compatibility purpose with old browser as it fallback to long pooling/pooling when websocket is unavailable.

Disabling SSLv3 can also be problematic for old browser.

## Horizontal Scalability

This was not the purpose of this demo, but such a system needs a real persistence storage layer providing ACID transactions (PostgresSQL...) in order to persist live sessions. Doing so, webapp is a stateless service and therefore can easily be distributed using a load balancer (HAProxy ...). 

As one queue and one websocket connection is created per concurrent user, scaling RabbitMQ is the real challenge of this architecture. RabbitMQ is already a distributed technology and provide native clusterization. However, I don't know how java rabbitmq driver deal with resource allocation in a clustered environment, but a simple round-robin might not be enought. Therefore, RabbitMQ clusterization can also be done at application layer using 'listeningAddress' field of session's model. More complexe resource allocation algorithm can be implemented, for example by implementing a MAX_CONCURRENT_CONNECTION by node. This design has several avantages. As RabbitMQ nodes do not form a native cluster, application can run over heterogeneous version of RabbitMQ nodes. Moreover, it might be easier to implement live session migration from one RabbitMQ node to another, and therefore would permits live rabbitmq cluster downscaling/upscaling

Distributed MessagingServer infrastructure example:

<p align="center">
  <img src="https://github.com/zanni/messaging-server/raw/master/messagingserver_distributed.png?raw=true" alt="Sublime's custom image"/>
</p>

## Max Concurrent Connections

I don't know how many concurrent connections a single rabbitmq node can handle or if RAM or file descriptors will be the bottleneck.
Responses could be found using tsung (https://github.com/processone/tsung) in order to simulate concurrent websocket connections

## Security 

What's missing:

**Application**:
- webapp endpoints should use SSL
- SSLv3 should be disabled
- webapp RabbitMQAuth endpoints should implement whitelist logic on rabbitmq nodes addresses (maybe they should be in another app, not visible by internet network)
- webapp RabbitMQAuth endpoints should use HTTP POST in order to avoid credential to be written in logs
- Non acked session should have a TTL and should be deleted at TTL expiration. A StrangeBehavior flag should be raised
- Acked session without corresponding RabbitMQ queue should be deleted using batch processing. A StrangeBehavior flag should be raised

**System**:
- SSH public key authentification enable, SSH password authentification disable, custom SSH port
- firewall: only ports 443 && 15674 opened to internet network

## Demo env

Following dependencies (with tested versions) are required to launch demo env:
 - Git (2.9.2)
 - Vagrant (1.8.5)
 - vagrant-omnibus (1.4.1)
 - vagrant-berkshelf (4.1.0)
 - VirtualBox (4.3)

The following lines should be executed in a SHELL env in order to setup demo env. It creates an ubuntu trusty64 VM using vagrant with 512m EAM, provisioned and running RabbitMQ/MessagingServer. Host and VM are linked using a private network on 24/192.168.33.0, with host 192.168.33.1 and VM 192.168.33.10.

```sh
git clone https://github.com/zanni/messaging-server.git
cd messaging-server/devops/env-demo
vagrant up
```

MessagingServer should be accessible in a browser at http://192.168.33.10:8080/

VM can be suspended,resumed,destroyed using 'vagrant suspend', 'vagrant resume' and 'vagrant destroy' CLI command

## Dev env

Following dependencies (with tested versions) are required to launch dev env:
 - Git (2.9.2)
 - Vagrant (1.8.5)
 - vagrant-omnibus (1.4.1)
 - vagrant-berkshelf (4.1.0)
 - VirtualBox (4.3)

The following lines should be executed in a SHELL env in order to setup dev env. It creates an ubuntu trusty64 VM using vagrant with 512m EAM, provisioned and running RabbitMQ. Host and VM are linked using a private network on 24/192.168.33.0, with host 192.168.33.1 and VM 192.168.33.10.

```sh
git clone https://github.com/zanni/messaging-server.git
cd messaging-server/devops/env-dev
vagrant up
```

You need to create application.properties file in {messagingserver.home} folder in order to run webapp
Webapp application use --messagingserver.home={messagingserver.home} as program input

VM can be suspended,resumed,destroyed using 'vagrant suspend', 'vagrant resume' and 'vagrant destroy' CLI command