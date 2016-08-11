#!/bin/sh
git clone https://github.com/zanni/messaging-server.git
cd messaging-server/devops
berks vendor; berks install
cd env-demo
vagrant up