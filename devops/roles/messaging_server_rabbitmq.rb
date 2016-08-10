name        "messaging_server_rabbitmq"
description "Configuration for messaging-server rabbitmq nodes"

run_list    "recipe[rabbitmq]",
			"recipe[rabbitmq::plugin_management]",
			"recipe[rabbitmq::user_management]",
			"recipe[rabbitmq::community_plugins]"

default_attributes(
	:rabbitmq => {
		:use_distro_version => false,
		:version => "3.6.1",
		:community_plugins => {
			:rabbitmq_auth_backend_http => "http://www.rabbitmq.com/community-plugins/v3.6.x/rabbitmq_auth_backend_http-3.6.x-3dfe5950.ez"
		},
		:additional_rabbit_configs => {
			:auth_backends => "[rabbit_auth_backend_internal,rabbit_auth_backend_http]"
		},
		:conf => {
			:rabbitmq_auth_backend_http => '[{http_method,post},
    {user_path,"http://192.168.33.1:8080/auth/user"},
    {vhost_path,    "http://192.168.33.1:8080/auth/vhost"},
    {resource_path, "http://192.168.33.1:8080/auth/resource"}]'
		},
		:enabled_plugins => [
			'rabbitmq_management',
			'rabbitmq_management_visualiser',
			'rabbitmq_event_exchange',
			'rabbitmq_stomp',
			'rabbitmq_web_stomp'
		],
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
  					:conf => "queue_events|amq.rabbitmq.event",
  					:write => "amq.default|queue_events",
  					:read => "amq.rabbitmq.event|queue_events"
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
