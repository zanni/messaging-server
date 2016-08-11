node.default["messagingserver"]["home"]  = "/etc/messagingserver"
node.default["messagingserver"]["log"]  = "/var/log/messagingserver"
node.default["messagingserver"]["bin"]  = "/usr/lib/messagingserver"
node.default["messagingserver"]["bin_download_url"]  = "https://github.com/zanni/messaging-server/releases/download/v0.0.1-SNAPSHOT/RabbitMQMessagingServer-0.0.1-SNAPSHOT.jar"

node.default["messagingserver"]["webapp_host"]  = "localhost"
node.default["messagingserver"]["webapp_port"]  = "8080"
node.default["messagingserver"]["rabbitmq_host"]  = "localhost"
node.default["messagingserver"]["rabbitmq_port"]  = 5672
node.default["messagingserver"]["rabbitmq_username"]  = "username"
node.default["messagingserver"]["rabbitmq_password"]  = "password"
