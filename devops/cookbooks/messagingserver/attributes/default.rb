node.default["messagingserver"]["home"]  = "/etc/messagingserver"
node.default["messagingserver"]["log"]  = "/var/log/messagingserver"
node.default["messagingserver"]["bin"]  = "/usr/lib/messagingserver"

node.default["messagingserver"]["rabbitmq_webapp_bin_download_url"]  = "https://github.com/zanni/messaging-server/releases/download/v0.0.1-SNAPSHOT/RabbitMQMessagingServer-0.0.1-SNAPSHOT.jar"
node.default["messagingserver"]["webapp_host"]  = "localhost"
node.default["messagingserver"]["webapp_port"]  = "8080"
node.default["messagingserver"]["rabbitmq_host"]  = "localhost"
node.default["messagingserver"]["rabbitmq_port"]  = 5672
node.default["messagingserver"]["rabbitmq_username"]  = "username"
node.default["messagingserver"]["rabbitmq_password"]  = "password"

node.default["messagingserver"]["springws_http_bin_download_url"]  = "https://github.com/zanni/messaging-server/releases/download/v0.0.1-SNAPSHOT/MessagingServerSpringWSHTTPService-0.0.1-SNAPSHOT.jar"
node.default["messagingserver"]["springws_websocket_bin_download_url"]  = "https://github.com/zanni/messaging-server/releases/download/v0.0.1-SNAPSHOT/MessagingServerSpringWSWebsocketService-0.0.1-SNAPSHOT.jar"
node.default["messagingserver"]["etcd"]  = "http://192.168.33.10:4001,http://192.168.33.10:5001,http://192.168.33.10:6001"
node.default["messagingserver"]["memcached_host"]  = "192.168.33.10"
node.default["messagingserver"]["memcached_port"]  = 11211

