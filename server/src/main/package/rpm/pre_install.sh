#!/bin/sh
(
#
# Create user account.
HAVE_USER=`getent passwd ${app.runUser}`
if [ "$HAVE_USER" == "" ]
then
  useradd --no-create-home --system --user-group ${app.runUser}
fi

#
# For STIG'd group access enforcement
GROUP_ACCESS="/etc/security/groupaccess.conf"
HAVE_STIGGED_GROUPS=`ls $GROUP_ACCESS 2>/dev/null`
if [ "$HAVE_STIGGED_GROUPS" == "" ]
then
   echo "${app.runGroup}" > $GROUP_ACCESS
else
   ALREADY_HAVE=`grep ${app.runGroup} $GROUP_ACCESS 2>/dev/null`
   if [ "$ALREADY_HAVE" == "" ]
   then
     echo "${app.runGroup}" >> $GROUP_ACCESS
   fi
fi
)

