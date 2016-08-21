#
# Cookbook Name:: messagingserver
# Recipe:: springws.confdhaproxy
#

# conf HAProxy as springws_http load balancer, using etcd/confd for dynamic configuration 



node.default['haproxy']['source']['version'] = '1.4.22'

directory "/usr/local/etc/haproxy" do
  action :create
end

path = "/usr/local/etc/haproxy/haproxy.cfg"

template "#{path}" do
  source "haproxy.cfg.erb"
  action :create
  
end


include_recipe "haproxy::install_package"





node.default['confd']['config']['backend'] = 'etcd'
node.default['confd']['config']['nodes'] = ["192.168.33.10:4001","192.168.33.10:5001","192.168.33.10:6001"]
node.default['confd']['config']['interval'] = 10
node.default['confd']['config']['onetime'] = false
node.default['confd']['service_name'] = "confd"
node.default['confd']['config_file'] = "#{path}"

include_recipe "confd::default"

confd_template "#{path}" do
  template_source "haproxy.tmpl.erb"
  prefix "/"
  keys ["/messagingserver_springws/http_service", "/messagingserver_springws/ws_service"]

  check_command "sudo /usr/sbin/haproxy -c -f /usr/local/etc/haproxy/haproxy.cfg"
  reload_command "sudo /usr/sbin/haproxy -f /usr/local/etc/haproxy/haproxy.cfg -p /var/run/haproxy.pid -D -sf $(cat /var/run/haproxy.pid)"

  notifies :restart, 'confd_service[confd]', :delayed
end


