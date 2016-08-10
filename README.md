# Messaging Server 

## Requirements

Following dependencies (with tested versions) are required to launch dev/demo env:
 - Git (2.9.2)
 - Vagrant (1.7.1)
 - vagrant-omnibus (1.4.1)
 - Chef DK (0.16.28)

## Dev env
	git clone https://github.com/zanni/messaging-server.git
	cd devops
	berks vendor; berks install
	cd env-dev
	vagrant up

## Demo env

	git clone https://github.com/zanni/messaging-server.git
	cd devops
	berks vendor; berks install
	cd env-demo
	vagrant up