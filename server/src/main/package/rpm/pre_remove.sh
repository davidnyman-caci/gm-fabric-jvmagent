#!/bin/sh

echo "Stopping service [﻿${app.name}-${rpm.version} ]..."
service ${app.name}-${rpm.version} stop || true

#
# Backup the current install. Remove the unneeded directories.
# Doing this to keep the configuration and logs of the install being removed.
# May want to re-use the configureation & review the logs.
#
APP=${rpm.install.dir}/${app.name}-${project.parent.version}
if [ -e "${APP}" ]
then
  cd ${rpm.install.dir}
  cp -a ${APP} ${APP}_bkp
  rm -rf ${APP}_bkp/{bin,lib}
fi

