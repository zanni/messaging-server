#
# Cookbook Name:: messagingserver
# Recipe:: rabbitmq
#

node.default["rabbitmq"]["use_distro_version"] = false
node.default["rabbitmq"]["version"] = "3.6.1"

node.default["rabbitmq"]["enabled_plugins"] = [
			'rabbitmq_management',
			'rabbitmq_management_visualiser',
			'rabbitmq_event_exchange',
			'rabbitmq_stomp',
			'rabbitmq_web_stomp'
		]

node.default["rabbitmq"]["enabled_users"] = [{
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

node.default["rabbitmq"]["community_plugins"]["rabbitmq_auth_backend_http"] = "http://www.rabbitmq.com/community-plugins/v3.6.x/rabbitmq_auth_backend_http-3.6.x-3dfe5950.ez"
node.default["rabbitmq"]["additional_rabbit_configs"]["auth_backends"] = "[rabbit_auth_backend_internal,rabbit_auth_backend_http]"
node.default["rabbitmq"]["conf"]["rabbitmq_auth_backend_http"] = '[{http_method,post},
    {user_path,"http://'+node['messagingserver']['webapp_host']+':'+node['messagingserver']['webapp_port']+'/auth/user"},
    {vhost_path,    "http://'+node['messagingserver']['webapp_host']+':'+node['messagingserver']['webapp_port']+'/auth/vhost"},
    {resource_path, "http://'+node['messagingserver']['webapp_host']+':'+node['messagingserver']['webapp_port']+'/auth/resource"}]'


include_recipe "rabbitmq"
include_recipe "rabbitmq::plugin_management"
include_recipe "rabbitmq::user_management"
include_recipe "rabbitmq::community_plugins"

