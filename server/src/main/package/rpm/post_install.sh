#!/bin/sh

(
############################
# Create service::
############################
chkconfig --add ${app.name}-${rpm.version}
)

