#!/usr/bin/env bash
# 文件作为假的IO设备，创建PV，VG，供cinder-volume使用
# usage: /create_loop_dev_for_openstack_cinder.sh 100

if [ $# == 0 ]; then
    echo "no size parameter"
    exit
fi
mkdir /vol
touch /vol/cinder-volumes
dd if=/dev/zero of=/vol/cinder-volumes bs=1G count=0 seek=$1
loopdev=$(losetup -f)
losetup $loopdev /vol/cinder-volumes
pvcreate $loopdev
vgcreate cinder-volumes $loopdev
pvdisplay

