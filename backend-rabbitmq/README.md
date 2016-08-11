# Java webapp

Java J2EE spring web application orchestrating RabbitMQ message broker to provide a securised signal exchange between to clients using the same proxy

## Design

3 controllers:
- MessagingServerController: HTTP endpoints dealing with user connect/disconnect/exchange
- RabbitMQAuthController: HTTP endpoints acting as rabbitmq authentification/authorization backend
- RabbitmqQueueEventController: AMPQ listener on rabbitmv queue creation/deletion events

## Usage

require java 8

use maven in order to build:
	mvn clean package -Dmessagingserver.home={messagingserver.home}

You need to create application.properties file in {messagingserver.home} folder in order to run webapp
Webapp application use --messagingserver.home={messagingserver.home} as program input

this webapp provide a minimal demo UI accessible at root path: http://localhost:8080