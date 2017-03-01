#!/bin/sh

#
# This script takes the backed up install and assignes a Date Timestamp.
#
(
   DTS=`date "+_%Y_%m_%d_%H_%M_%S_%s"`
   APP=${rpm.install.dir}/${app.name}-${project.parent.version}
   mv ${APP}_bkp ${APP}_bkp$DTS
   if [ -e "${APP}" ]
   then
        rm -rf ${APP}
   fi
)