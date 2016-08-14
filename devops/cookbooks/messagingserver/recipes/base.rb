#
# Cookbook Name:: messagingserver
# Recipe:: base
#

node.default["tz"] = "GMT"
node.default["rabbitmq"]["use_distro_version"] = "3.6.1"

include_recipe "apt"
include_recipe "timezone-ii"
include_recipe "ntp"
include_recipe "hostnames"