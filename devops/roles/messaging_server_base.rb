name        "messaging_server_base"
description "System setup"

run_list   "recipe[hostnames]",
		   "recipe[timezone-ii]",
		   "recipe[ntp]",
		   "recipe[apt]"


 override_attributes(
	 :tz => "GMT"
)