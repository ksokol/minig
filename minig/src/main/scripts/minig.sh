#!/bin/bash
#-----------------------------------------------------
# minig
#-----------------------------------------------------
# Script for running minig as a service under initd.
#
# Usage: service minig {start|stop|restart|status}"
#
# Author: Kamill Sokol <dev@sokol-web.de>
#-----------------------------------------------------
### BEGIN INIT INFO
# Provides: minig
# Required-Start:
# Required-Stop:
# Default-Start: 2 3 4 5
# Default-Stop: 0 1 2 6
# Short-Description: init script for minig
# Description: init script for minig
# This should be placed in /etc/init.d
### END INIT INFO

minig_NAME=minig
MINIG_HOME=/opt/minig
minig_CONF=${MINIG_HOME}/config/application.properties
minig_USER=minig
minig_PORT=7130

if [ ! -e /etc/redhat-release ]; then
    echo "This is built for RHEL/Fedora/CentOS, you may need to alter it to fit your distribution"
    exit 1
fi
:
if [ -f ${minig_CONF} ]; then
    TMP=$(egrep -o "server.port=.*" ${MINIG_HOME}/config/application.properties | cut -b 13-19)
    if [ ! -z ${TMP} ]; then
        minig_PORT=${TMP}
    fi
fi

. /etc/rc.d/init.d/functions

case $1 in
start)
    PID=$(lsof -i:${minig_PORT} -a -u ${minig_USER} -t)

    if [ ${PID} ]; then
        echo "Already running ${minig_NAME}" && warning
        echo
        exit 1
    fi

    action "Starting ${minig_NAME}: " daemon --check minig --user ${minig_USER} "cd ${MINIG_HOME}; nohup java -server -jar minig.jar > ${MINIG_HOME}/logs/nohup.out 2>&1 &"
    echo
;;
stop)
    PID=$(lsof -i:${minig_PORT} -a -u ${minig_USER} -t)

    if [ ${PID} ]; then
        action "Shutting down ${NAME}: " kill ${PID}
    else
        echo -n "Service ${minig_NAME} not running!" && failure
        echo
    fi

    echo
;;
restart)
    $0 stop
    sleep 2
    $0 start
;;
status)
    PID=$(lsof -i:${minig_PORT} -a -u ${minig_USER} -t)

    if [ ${PID} ]; then
        echo "${minig_NAME} running PID: ${PID}"
        exit 0
    else
        echo "${minig_NAME} not running"
        exit 1
    fi
;;
*)
    echo "Usage: $0 {start|stop|restart|status}"
    exit 3
;;
esac