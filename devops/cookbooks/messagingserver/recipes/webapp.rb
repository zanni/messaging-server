#
# Cookbook Name:: messagingserver
# Recipe:: webapp
#

# setup oracle jdk8
node.default["java"]["install_flavor"] = "oracle"
node.default["java"]["jdk_version"] = "8"
node.default["java"]["oracle"]["accept_oracle_download_terms"] = true
include_recipe "java"

# default webapp -> rabbitmq connection credentials
node.default["messagingserver"]["rabbitmq_username"] = "trustedclient"
node.default["messagingserver"]["rabbitmq_password"] = "trustedclient"

# create application home, log and bin directory
directory "#{node['messagingserver']['home']}" do
  action :create
end

directory "#{node['messagingserver']['log']}" do
  action :create
end

directory "#{node['messagingserver']['bin']}" do
  action :create
end

# create application properties file
template "#{node['messagingserver']['home']}/application.properties" do
  source "application.properties.erb"
  action :create
end

# download jar
remote_file "#{node['messagingserver']['bin']}/RabbitMQMessagingServer.jar" do
  source "#{node['messagingserver']['bin_download_url']}"
end

# create upstart service
template "/etc/init.d/messagingserver" do
	source "java_upstart.erb"
	action :create
  mode '077'
	variables({
	 :name => "messagingserver",
	 :commande => "#{node['messagingserver']['bin']}/RabbitMQMessagingServer.jar",
	 :log => "messagingserver.log"
	})
end

# define messagingserver service
service "messagingserver" do
  supports :start => true, :stop => true
  action :nothing
end

# start messagingserver service
bash "start service" do
  user "root"
  code <<-EOH
  sudo service messagingserver start
  EOH
end

