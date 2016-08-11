name        "messaging_server_webapp"
description "Configuration for messaging-server webapp nodes"

run_list    "recipe[java]",
			"recipe[messagingserver]"

default_attributes(
	:messagingserver => {
		:rabbitmq_username => "trustedclient",
		:rabbitmq_password => "trustedclient"
	},
	:java => {
      :install_flavor => "oracle",
      :jdk_version =>  "8",
      :oracle => {
      	:accept_oracle_download_terms => true
      }
    },
)        
