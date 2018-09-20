#!/bin/bash
#
# Creates the test databases.
#
# This script requires a MySQL service, with the default super user, where the test 
# databases will be created.

set -o nounset
set -e

# MySQL tables
mysql -e "create database IF NOT EXISTS multimedia;" -uroot

# PostgreSQL tables
psql -c 'create database multimedia;' -U postgres

exit 0