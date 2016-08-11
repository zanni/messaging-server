#
# Cookbook Name:: messagingserver
# Recipe:: default
#

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

service "messagingserver" do
  supports :start => true, :stop => true
  action :nothing
end

bash "start service" do
      user "root"
      code <<-EOH
      sudo service messagingserver start
      EOH
    end

