# backend-spring

Hot upscaling and downscaling of both http and sockjs modules using etcd, confd, haproxy

How I stop worrying and love the TTL

Distributed MessagingServer infrastructure example:

<p align="center">
  <img src="https://github.com/zanni/messaging-server/raw/master/backend-springws/messagingserver_springws.png?raw=true" alt="Messaging server springws backend"/>
</p>


## Demo env

Following dependencies (with tested versions) are required to launch demo env:
 - Git (2.9.2)
 - Vagrant (1.8.5)
 - vagrant-omnibus (1.4.1)
 - vagrant-berkshelf (4.1.0)
 - VirtualBox (4.3)

The following lines should be executed in a SHELL env in order to setup demo env. It creates an ubuntu trusty64 VM using vagrant with 512m EAM, provisioned and running MessagingServer. Host and VM are linked using a private network on 24/192.168.33.0, with host 192.168.33.1 and VM 192.168.33.10.

```sh
git clone https://github.com/zanni/messaging-server.git
cd messaging-server/devops/env-springws-demo
vagrant up
```

MessagingServer should be accessible in a browser at http://192.168.33.10:8080/

VM can be suspended,resumed,destroyed using 'vagrant suspend', 'vagrant resume' and 'vagrant destroy' CLI command

Anothers websocket/http nodes can be launched on host using these commands (require Java 8)

```sh

wget https://github.com/zanni/messaging-server/releases/download/v0.0.1-SNAPSHOT/MessagingServerSpringWSWebsocketService-0.0.1-SNAPSHOT.jar
wget https://github.com/zanni/messaging-server/releases/download/v0.0.1-SNAPSHOT/MessagingServerSpringWSHTTPService-0.0.1-SNAPSHOT.jar

# start a new websocket worker
java -jar MessagingServerSpringWSWebsocketService-0.0.1-SNAPSHOT.jar \
	--messagingserver.webapp.host="192.168.33.1" \
	--messagingserver.etcd="http://192.168.33.10:4001,http://192.168.33.10001,http://192.168.33.10:6001" \
	--messagingserver.memcached.host="192.168.33.10" \
	--messagingserver.memcached.port="11211"

# start a new http worker
java -jar MessagingServerSpringWSHTTPService-0.0.1-SNAPSHOT.jar \
	--messagingserver.webapp.host="192.168.33.1" \
	--messagingserver.etcd="http://192.168.33.10:4001,http://192.168.33.10001,http://192.168.33.10:6001" \
	--messagingserver.memcached.host="192.168.33.10" \
	--messagingserver.memcached.port="11211"
```

## Performance test env

Following dependencies (with tested versions) are required to launch performance test env:
 - Git (2.9.2)
 - Vagrant (1.8.5)
 - vagrant-omnibus (1.4.1)
 - vagrant-berkshelf (4.1.0)
 - VirtualBox (4.3)
 - Tsung (1.6.0)

The following lines should be executed in a SHELL env in order to setup dev env. It creates an ubuntu trusty64 VM using vagrant with 512m EAM, provisioned and running RabbitMQ. Host and VM are linked using a private network on 24/192.168.33.0, with host 192.168.33.1 and VM 192.168.33.10.

```sh
git clone https://github.com/zanni/messaging-server.git
cd messaging-server/devops/env-springws-perf
vagrant up
tsung -f tsung_benchmark.xml start
```

Currently, tsung user session implement the following scenario:
- make http /connect request
- connect to sockjs
- send a message to himself
- disconnect sockjs connection

Test is programmed to last 10 min, with a new user connecting each 0.1 second

Tsung web GUI: http://localhost:8091/

**Due to tsung limitation of "change_type" configuration (https://github.com/processone/tsung/issues/196), websocket multi-node infrastructure cannot be tested yet**

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
cd messaging-server/devops/env-springws-dev
vagrant up
```

You need to create application.properties file in {messagingserver.home} folder in order to run webapp
Webapp application use --messagingserver.home={messagingserver.home} as program input

VM can be suspended,resumed,destroyed using 'vagrant suspend', 'vagrant resume' and 'vagrant destroy' CLI command