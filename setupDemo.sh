#!/bin/sh
cd messaging-server/devops
berks vendor; berks install
cd env-demo
vagrant up