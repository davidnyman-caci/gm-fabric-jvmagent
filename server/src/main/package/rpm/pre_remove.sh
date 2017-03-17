#!/bin/sh

#
# Copyright 2017 Decipher Technology Studios LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

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
