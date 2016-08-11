#
# Cookbook Name:: messagingserver
# Recipe:: rabbitmq
#

node.default["rabbitmq"]["conf"]["rabbitmq_auth_backend_http"] = '[{http_method,post},
    {user_path,"http://'+node['messagingserver']['webapp_host']+':'+node['messagingserver']['webapp_port']+'/auth/user"},
    {vhost_path,    "http://'+node['messagingserver']['webapp_host']+':'+node['messagingserver']['webapp_port']+'/auth/vhost"},
    {resource_path, "http://'+node['messagingserver']['webapp_host']+':'+node['messagingserver']['webapp_port']+'/auth/resource"}]'

include_recipe "rabbitmq"
include_recipe "rabbitmq::plugin_management"
include_recipe "rabbitmq::user_management"
include_recipe "rabbitmq::community_plugins"

