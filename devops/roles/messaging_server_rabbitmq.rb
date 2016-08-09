name        "messaging_server_rabbitmq"
description "Configuration for messaging-server rabbitmq nodes"

run_list    "recipe[rabbitmq]",
			"recipe[rabbitmq::plugin_management]",
			"recipe[rabbitmq::user_management]"

default_attributes(
	:rabbitmq => {
		:use_distro_version => false,
		:version => "3.6.1",
		:enabled_plugins => ['rabbitmq_management','rabbitmq_management_visualiser', 'rabbitmq_event_exchange', 'rabbitmq_stomp', 'rabbitmq_web_stomp'],
		:enabled_users => [{
			:name => "admin",
			:password => "admin",
			:tag => "administrator",
			:rights => [
				{
					:vhost => "/",
  					:conf => ".*",
  					:write => ".*",
  					:read => ".*"
				}
			]
		}, {
			:name => "trustedclient",
			:password => "trustedclient",
			:tag => "trustedclient",
			:rights => [
				{
					:vhost => "/",
  					:conf => "^(queue-*|messagingserver)",
  					:write => "amq.default",
  					:read => "amq.rabbitmq.log"
				}
			]
		}, {
			:name => "anonymous",
			:password => "anonymous",
			:tag => "anonymous",
			:rights => [
				{
					:vhost => "/",
  					:conf => "^queue-*",
  					:write => "^$",
  					:read => "^queue-*"
				}
			]
		}]
	}
)        
