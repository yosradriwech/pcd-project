#!/bin/bash

# TODO: use a varible for the version, create smsw user

# Remove symbolic link, TODO: check if it exists
#rm -f /opt/application/smsw/current
# Create symbolic link with the new version
#ln -s /opt/application/smsw/2.0/ opt/application/smsw/current

# Create applicative logs folder
#mkdir -p /opt/application/smswrapper/logs/applicative
# Create technical logs folder
mkdir -p /opt/application/suma/logs/technical
# Create symbolic link for the applicative logs, continue even if symbolic exists
ln -s /logs/suma/$HOSTNAME/applicative /opt/application/suma/logs/applicative

# Change rights
chown -R suma:suma  /opt/application/suma/

# Move (force) smsw.service into /etc/systemd/systemd/
mv -f /opt/application/suma/deployment/suma.service /etc/systemd/system/
# Reload configuration
systemctl daemon-reload

# Remove deployment folder
rm -rf /opt/application/suma/deployment

