#
# Cookbook Name:: messagingserver
# Recipe:: springws.websocket
#

# setup oracle jdk8
node.default["java"]["install_flavor"] = "oracle"
node.default["java"]["jdk_version"] = "8"
node.default["java"]["oracle"]["accept_oracle_download_terms"] = true
include_recipe "java"

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

name = "springws_ws"
# download jar
remote_file "#{node['messagingserver']['bin']}/#{name}.jar" do
  source "#{node['messagingserver']['springws_websocket_bin_download_url']}"
end


# create upstart service
template "/etc/init.d/#{name}" do
  source "java_upstart.erb"
  action :create
  mode '077'
  variables({
   :name => "#{name}",
   :commande => "#{node['messagingserver']['bin']}/#{name}.jar --messagingserver.webapp.host=\"#{node['messagingserver']['webapp_host']}\" --messagingserver.etcd=\"#{node['messagingserver']['etcd']}\" --messagingserver.memcached.host=\"#{node['messagingserver']['memcached_host']}\" --messagingserver.memcached.port=\"#{node['messagingserver']['memcached_port']}\"",
   :log => "#{name}.log"
  })
end

# define messagingserver service
service "#{name}" do
  supports :start => true, :stop => true
  action :nothing
end

# start messagingserver service
bash "start service #{name}" do
  user "root"
  code <<-EOH
  sudo service #{name} start
  EOH
end