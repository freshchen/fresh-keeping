#!/usr/bin/env bash

if [ $# -lt 2 ]; then
    echo "usage: sh add_route.sh router-id segment"
    echo "usage: sh add_route.sh id 192.168.1.0/24"
    exit
fi
openstack_router_id=$1
segment=$2
qg_info=`ip netns exec qrouter-${openstack_router_id} ip a | grep 'global qg'`
external_ip=`echo ${qg_info} | awk '{print $2}' | cut -d '/' -f1`
brd_ip=`echo ${qg_info} | awk '{print $4}'`
phy_dev=`ip a | grep ${brd_ip} | awk '{print $7}'`
ip route add ${segment} via ${external_ip} dev ${phy_dev}
