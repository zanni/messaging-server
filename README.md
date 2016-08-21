# Messaging Server 
[![Build Status](https://travis-ci.org/zanni/messaging-server.svg?branch=master)](https://travis-ci.org/zanni/messaging-server) [![Code Climate](https://codeclimate.com/github/zanni/messaging-server/badges/gpa.svg)](https://codeclimate.com/github/zanni/messaging-server)

WebRTC compliant signal exchange implementations based on HTTP and SockJS

<p align="center">
  <img src="https://github.com/zanni/messaging-server/raw/master/webrtc.png?raw=true" alt="Sublime's custom image"/>
</p>

	1: User A send message M for User B to server S
	2: S relay M to B
	3: B send response M2 for A to S
	4: S relay M2 to A
	5/6: Bidirectionnal peer connections are established

Those prototypes permits communications 1 to 4

## Design

**Session Model** {userId, listeningAddress, listeningKey, ack} with:
- **userId**: user id (authentification and authorization of user connecting to MessagingServer is out of the scope of this project)
- **listeningAddress**: SockJS host address
- **listeningKey**: routing key user is allowed to subscribe to
- **ack**: session is acked when authentification connection have been opened between MessagingServer and User

**Workflow**:
- User inits connection by sending HTTP POST request to "/connect" endpoint, providing {userId}. A session is generated with a random {listeningKey} and an available {listeningAddress}.
- User then attempts to connect to SockJS "/ws" endpoint using session's data given in previous HTTP response. If an active session matches {userId, listeningAddress, listeningKey}, SockJS connection is opened
- User exchanges messages with others connected users by sending JSON messages through SockJS connection. Messages are relayed between SockJS nodes using internal HTTP POST
- SockJS connection/disconnection events are monitored. When a connection is opened, corresponding session is acked, when a connection is broken, corresponding session is deleted

## Implementations

This project provide two differents implementations of this design:
- [backend-rabbitmq](https://github.com/zanni/messaging-server/tree/master/backend-rabbitmq): Spring http webapp + RabbitMQ + In-Memory session (this implementation came first )
- [backend-springws](https://github.com/zanni/messaging-server/tree/master/backend-springws): Spring http webapp + Spring SockJS webapp + Memcached session + Etcd (hot SockJS/HTTP cluster upscaling/downscaling)