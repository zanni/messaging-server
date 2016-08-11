#!/bin/sh
cd devops
berks vendor; berks install
cd env-dev
vagrant up