#!/bin/sh
cd devops
berks vendor; berks install
cd env-demo
vagrant up