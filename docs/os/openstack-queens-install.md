# Queens版本Openstack安装记录

### 准备工作

这次安装的是openstack的queens版本，操作系统是REHL7.6，安装前确保根目录下分配了足够的空间，主要参看[官方文档](https://docs.openstack.org/queens/install/)进行安装，记录遇到问题的解决方案。

[如果不能连外网可以参考另外一篇博客自制YUM源](https://freshchen.github.io/2019/02/14/yumsource/)

需要安装的包如下

```sh
yum install -y https://repos.fedorapeople.org/repos/openstack/openstack-queens/rdo-release-queens-1.noarch.rpm
yum install -y chrony 
yum install -y mariadb mariadb-server python2-PyMySQL
yum install -y rabbitmq-server
yum install -y memcached python-memcached
yum install -y etcd
yum install -y python-openstackclient
# [keystone]
yum install -y openstack-keystone httpd mod_wsgi mod_ssl
# [glance]
yum install -y openstack-glance
# [nova]
yum install -y openstack-nova-api openstack-nova-conductor openstack-nova-console openstack-nova-novncproxy openstack-nova-scheduler openstack-nova-placement-api
yum install -y openstack-nova-compute
# [neutron]
yum install -y openstack-neutron openstack-neutron-ml2 ebtables ipset
# 使用 openstack-neutron-linuxbridge 或者 openstack-neutron-openvswitch. [SRIOV可选]
yum install -y openstack-neutron-linuxbridge
yum install -y openstack-neutron-openvswitch
yum install -y openstack-neutron-sriov-nic-agent
# [horizon]
yum install -y openstack-dashboard
# [cinder]
yum install -y openstack-cinder targetcli python-keystone
# [heat]
yum install -y openstack-heat-api openstack-heat-api-cfn openstack-heat-engine
```

### 基础服务安装

```sh
[root@localhost ~]# hostname
controller
[root@localhost ~]# cat /etc/hosts
127.0.0.1   localhost localhost.localdomain localhost4 localhost4.localdomain4
::1         localhost localhost.localdomain localhost6 localhost6.localdomain6
<IP> controller


# 关闭防火墙：
[root@controller ~]# systemctl status firewalld.service
[root@controller ~]# systemctl stop firewalld.service
[root@controller ~]# systemctl disable firewalld.service

# 已经开启了ip forward:
[root@controller ~]# sysctl -a | grep net.ipv4.ip_forward
net.ipv4.ip_forward = 1

#移除默认的libvirt网络
virsh net-destroy default
virsh net-autostart --disable default
virsh net-undefine default
virsh net-list

#数据库配置
mysql_secure_installation
mysql -u root


systemctl enable rabbitmq-server.service
systemctl start rabbitmq-server.service
systemctl status rabbitmq-server.service

rabbitmqctl add_user openstack RABBIT_PASS {RABBIT_PASS = openstack1234}
rabbitmqctl add_user openstack openstack1234
rabbitmqctl set_permissions openstack ".*" ".*" ".*"

# 检查：
[root@controller ~]# rabbitmqctl list_users
Listing users ...
openstack	[]
guest	[administrator]


systemctl restart memcached
systemctl status memcached
```

### 安装Openstack组件

主要按照官网来，没遇到问题就不做记录

#### keystone

[官网](https://docs.openstack.org/keystone/queens/install/)

```
mysql -u root -p

CREATE DATABASE keystone;
GRANT ALL PRIVILEGES ON keystone.* TO 'keystone'@'localhost' IDENTIFIED BY '密码';
GRANT ALL PRIVILEGES ON keystone.* TO 'keystone'@'%' IDENTIFIED BY '密码';
```

```
vi /etc/keystone/keystone.conf

[database]
# 加上connection
connection = mysql+pymysql://keystone:密码@controller/keystone
[token]
provider = fernet
```

```
su -s /bin/sh -c "keystone-manage db_sync" keystone
```

```
keystone-manage fernet_setup --keystone-user keystone --keystone-group keystone
keystone-manage credential_setup --keystone-user keystone --keystone-group keystone
```

```
keystone-manage bootstrap --bootstrap-password admin1234 \
  --bootstrap-admin-url http://controller:5000/v3/ \
  --bootstrap-internal-url http://controller:5000/v3/ \
  --bootstrap-public-url http://controller:5000/v3/ \
  --bootstrap-region-id RegionOne
```

```
vi /etc/httpd/conf/httpd.conf
ServerName controller

########################################################################
# 改完可能会apache起不来没有35357和5000端口，重启服务就好了 apachectl restart #
########################################################################
```

```
ln -s /usr/share/keystone/wsgi-keystone.conf /etc/httpd/conf.d/
```

```
systemctl enable httpd.service
systemctl start httpd.service
```

```
vi ~/admin-openrc

export OS_USERNAME=admin
export OS_PASSWORD=ADMIN_PASS
export OS_PROJECT_NAME=admin
export OS_USER_DOMAIN_NAME=Default
export OS_PROJECT_DOMAIN_NAME=Default
export OS_AUTH_URL=http://controller:5000/v3
export OS_IDENTITY_API_VERSION=3
```

```
. ~/admin-openrc
```

测试一下有正常返回基本就没问题了,有问题可以检查数据库

```
openstack token issue
```

```sh
# 重启
systemctl restart httpd.service
```

#### nova

[官网](https://docs.openstack.org/nova/queens/install/)

```bash
mysql -u root -p
```

```bash
CREATE DATABASE nova_api;
CREATE DATABASE nova;
CREATE DATABASE nova_cell0;
GRANT ALL PRIVILEGES ON nova_api.* TO 'nova'@'localhost' IDENTIFIED BY '';
GRANT ALL PRIVILEGES ON nova_api.* TO 'nova'@'%' IDENTIFIED BY '';
GRANT ALL PRIVILEGES ON nova.* TO 'nova'@'localhost' IDENTIFIED BY '';
GRANT ALL PRIVILEGES ON nova.* TO 'nova'@'%' IDENTIFIED BY '';
GRANT ALL PRIVILEGES ON nova_cell0.* TO 'nova'@'localhost' IDENTIFIED BY '';
GRANT ALL PRIVILEGES ON nova_cell0.* TO 'nova'@'%' IDENTIFIED BY '';
```

```bash
openstack user create --domain default --password-prompt nova
```

```bash
 openstack role add --project service --user nova admin
```

```bash
openstack service create --name nova --description "OpenStack Compute" compute
```

```bash
openstack endpoint create --region RegionOne compute public http://controller:8774/v2.1
openstack endpoint create --region RegionOne compute internal http://controller:8774/v2.1
openstack endpoint create --region RegionOne compute admin http://controller:8774/v2.1
```

```
openstack user create --domain default --password-prompt placement
```

```
openstack role add --project service --user placement admin
```

```
openstack service create --name placement --description "Placement API" placement
```

```
openstack endpoint create --region RegionOne placement public http://controller:8778
openstack endpoint create --region RegionOne placement internal http://controller:8778
openstack endpoint create --region RegionOne placement admin http://controller:8778
```

```
vi  /etc/nova/nova.conf
[DEFAULT]
# ...
enabled_apis = osapi_compute,metadata
transport_url = rabbit://openstack:RABBIT_PASS@controller
my_ip = 10.0.0.11
use_neutron = True
firewall_driver = nova.virt.firewall.NoopFirewallDriver
[api_database]
# ...
connection = mysql+pymysql://nova:NOVA_DBPASS@controller/nova_api

[database]
# ...
connection = mysql+pymysql://nova:NOVA_DBPASS@controller/nova
[api]
# ...
auth_strategy = keystone

[keystone_authtoken]
# ...
auth_url = http://controller:5000/v3
memcached_servers = controller:11211
auth_type = password
project_domain_name = default
user_domain_name = default
project_name = service
username = nova
password = NOVA_PASS
[vnc]
enabled = true
# ...
server_listen = $my_ip
server_proxyclient_address = $my_ip
[glance]
# ...
api_servers = http://controller:9292
[oslo_concurrency]
# ...
lock_path = /var/lib/nova/tmp
[placement]
# ...
region_name = RegionOne
project_domain_name = Default
project_name = service
auth_type = password
user_domain_name = Default
auth_url = http://controller:5000/v3
username = placement
password = PLACEMENT_PASS
```

```
创消息队列用户
rabbitmqctl add_user openstack openstack1234
rabbitmqctl set_permissions openstack ".*" ".*" ".*"
rabbitmqctl list_users
```

```
vi /etc/httpd/conf.d/00-nova-placement-api.conf
<Directory /usr/bin>
   <IfVersion >= 2.4>
      Require all granted
   </IfVersion>
   <IfVersion < 2.4>
      Order allow,deny
      Allow from all
   </IfVersion>
</Directory>
```

```
systemctl restart httpd
```

```
su -s /bin/sh -c "nova-manage api_db sync" nova
```

```
su -s /bin/sh -c "nova-manage cell_v2 map_cell0" nova
```

```
su -s /bin/sh -c "nova-manage cell_v2 create_cell --name=cell1 --verbose" nova
```

```
su -s /bin/sh -c "nova-manage db sync" nova
```

```
# 检查
nova-manage cell_v2 list_cells
```

```
systemctl enable openstack-nova-api.service \
  openstack-nova-consoleauth.service openstack-nova-scheduler.service \
  openstack-nova-conductor.service openstack-nova-novncproxy.service

systemctl start openstack-nova-api.service openstack-nova-consoleauth.service openstack-nova-scheduler.service openstack-nova-conductor.service openstack-nova-novncproxy.service
```

```
openstack compute service list
```

```
openstack catalog list
```

```
nova-status upgrade check
```

```sh
# 忽略python警告
sed -n '325,333p' /usr/lib/python2.7/site-packages/oslo_db/sqlalchemy/enginefacade.py
```

```sh
# 重启
systemctl restart openstack-nova-api.service openstack-nova-consoleauth.service openstack-nova-scheduler.service openstack-nova-conductor.service openstack-nova-novncproxy.service
```

#### glance

[官网](https://docs.openstack.org/glance/queens/install/)

```
mysql -u root -p
CREATE DATABASE glance;
GRANT ALL PRIVILEGES ON glance.* TO 'glance'@'localhost' IDENTIFIED BY '密码';
GRANT ALL PRIVILEGES ON glance.* TO 'glance'@'%' IDENTIFIED BY '密码';
```

```
. admin-openrc
openstack user create --domain default --password-prompt glance
openstack role add --project service --user glance admin
openstack service create --name glance --description "OpenStack Image" image
openstack endpoint create --region RegionOne image public http://controller:9292
openstack endpoint create --region RegionOne image admin http://controller:9292
```

```
vi /etc/glance/glance-api.conf 
[database]
# 加上connection
connection = mysql+pymysql://glance:密码@controller/glance
[keystone_authtoken]
# ...
auth_uri = http://controller:5000
auth_url = http://controller:5000
memcached_servers = controller:11211
auth_type = password
project_domain_name = Default
user_domain_name = Default
project_name = service
username = glance
password = GLANCE_PASS(创建glance用户时候密码)
[paste_deploy]
# ...
flavor = keystone
[glance_store]
# ...
stores = file,http
default_store = file
filesystem_store_datadir = /var/lib/glance/images/
```

```bash
vi /etc/glance/glance-registry.conf
[database]
# ...
connection = mysql+pymysql://glance:密码@controller/glance
[keystone_authtoken]
# ...
auth_uri = http://controller:5000
auth_url = http://controller:5000
memcached_servers = controller:11211
auth_type = password
project_domain_name = Default
user_domain_name = Default
project_name = service
username = glance
password = GLANCE_PASS

[paste_deploy]
# ...
flavor = keystone
```

```
su -s /bin/sh -c "glance-manage db_sync" glance
```

```
systemctl enable openstack-glance-api.service openstack-glance-registry.service
systemctl start openstack-glance-api.service openstack-glance-registry.service
```

```sh
# 问题一
[root@controller ~]# openstack image create "cirros-rhel7.4" --file /home/rhel-server-7.4-x86_64-kvm.qcow2 --disk-format qcow2 --container-format bare --public
503 Service Unavailable: The server is currently unavailable. Please try again at a later time. (HTTP 503)

[root@controller ~]# vi /var/log/glance/api.log
2019-01-24 21:42:48.597 27505 CRITICAL keystonemiddleware.auth_token [-] Unable to validate token: Identity server rejected authorization necessary to fetch token data: ServiceError: Identity server rejected authorization necessary to fetch token data

# 出现以上情况应该是配置文件中的密码错了，应该填写创建用户时候的密码，忘记了只能改密码，然后重启服务
openstack user set --password GLANCE_PWD  (user id)

```

```sh
# 重启
systemctl restart openstack-glance-api.service openstack-glance-registry.service
```

#### neutron

[官网](https://docs.openstack.org/neutron/queens/install/)

```sh
# 重启
ystemctl restart neutron-server.service neutron-openvswitch-agent.service neutron-l3-agent.service neutron-dhcp-agent.service neutron-metadata-agent.service
```

#### heat

[官网](https://docs.openstack.org/heat/queens/install/)

```sh
# 重启
systemctl restart openstack-heat-api.service openstack-heat-api-cfn.service openstack-heat-engine.service
```

#### horizon

[官网](https://docs.openstack.org/horizon/queens/install/)

```sh
# 重启
systemctl restart httpd.service memcached.service
```

#### tacker

[官网](https://docs.openstack.org/tacker/queens/install/)

#### cinder

使用官网推荐的LVM作为后端

```bash
mysql -u root -p
MariaDB [(none)]> CREATE DATABASE cinder;
MariaDB [(none)]> GRANT ALL PRIVILEGES ON cinder.* TO 'cinder'@'localhost' \
  IDENTIFIED BY 'CINDER_DBPASS';
MariaDB [(none)]> GRANT ALL PRIVILEGES ON cinder.* TO 'cinder'@'%' \
  IDENTIFIED BY 'CINDER_DBPASS';

openstack user create --domain default --password-prompt cinder
openstack role add --project service --user cinder admin
openstack service create --name cinderv2 \
  --description "OpenStack Block Storage" volumev2
openstack service create --name cinderv3 \
  --description "OpenStack Block Storage" volumev3
openstack endpoint create --region RegionOne \
  volumev2 public http://controller:8776/v2/%\(project_id\)s
openstack endpoint create --region RegionOne \
  volumev2 internal http://controller:8776/v2/%\(project_id\)s
openstack endpoint create --region RegionOne \
  volumev2 admin http://controller:8776/v2/%\(project_id\)s
openstack endpoint create --region RegionOne \
  volumev3 public http://controller:8776/v3/%\(project_id\)s
openstack endpoint create --region RegionOne \
  volumev3 internal http://controller:8776/v3/%\(project_id\)s
openstack endpoint create --region RegionOne \
  volumev3 admin http://controller:8776/v3/%\(project_id\)s

yum install lvm2 device-mapper-persistent-data -y
yum install openstack-cinder targetcli python-keystone -y

systemctl enable lvm2-lvmetad.service
systemctl start lvm2-lvmetad.service

pvcreate /dev/sdb
vgcreate cinder-volumes /dev/sdb

vi /etc/lvm/lvm.conf
->
filter = [ "a/sda/", "a/sdb/", "r/.*/"]
<-

cat /etc/cinder/cinder.conf | grep -v ^# | grep .
->
[DEFAULT]
enabled_backends = lvm
glance_api_servers = http://controller:9292
transport_url = rabbit://openstack:密码!@controller
auth_strategy = keystone
my_ip = ip
[database]
connection = mysql+pymysql://cinder:密码@controller/cinder
[keystone_authtoken]
auth_uri = http://controller:5000
auth_url = http://controller:5000
memcached_servers = controller:11211
auth_type = password
project_domain_id = default
user_domain_id = default
project_name = service
username = cinder
password = 密码
[lvm]
volume_driver = cinder.volume.drivers.lvm.LVMVolumeDriver
volume_group = cinder-volumes
iscsi_protocol = iscsi
iscsi_helper = lioadm 
[oslo_concurrency]
lock_path = /var/lib/cinder/tmp
<-

su -s /bin/sh -c "cinder-manage db sync" cinder

Configure Compute to use Block Storage¶
Edit the /etc/nova/nova.conf file and add the following to it:
[cinder]
os_region_name = RegionOne


systemctl restart openstack-nova-api.service
systemctl enable openstack-cinder-volume.service target.service
systemctl start openstack-cinder-volume.service target.service
systemctl enable openstack-cinder-api.service openstack-cinder-scheduler.service
systemctl start openstack-cinder-api.service openstack-cinder-scheduler.service

systemctl status openstack-cinder-volume.service target.service openstack-cinder-api.service openstack-cinder-scheduler.service
systemctl restart openstack-cinder-volume.service target.service openstack-cinder-api.service openstack-cinder-scheduler.service
```

问题：

因为机器上只有一块磁盘，所以需要做一个虚拟磁盘。

```bash
dd if=/dev/zero of=/vol/cinder-volumes bs=1 count=0 seek=10G   
# Mount the file.   
loopdev=`losetup -f`   
losetup $loopdev /vol/cinder-volumes   
# Initialize as a physical volume.   
pvcreate $loopdev   
# Create the volume group.   
vgcreate cinder-volumes $loopdev   
# Verify the volume has been created correctly.   
pvscan 
```

个性化配置：

[官方配置文档](https://docs.openstack.org/cinder/rocky/configuration/index.html)

1块存储服务**openstack-cinder-api**是单进程运行的 ， 限制了速度，可以更改cinder配置文件，或者命令修改

```bash
openstack-config --set /etc/cinder/cinder.conf \
  DEFAULT osapi_volume_workers CORES
```

CORES：机器上CPU的核数或者线程数



### Troubleshooting

#### 问题1 

##### 描述：检查keystone是否成功安装，没正常反馈

```bash
[root@controller ~]# openstack token issue
An unexpected error prevented the server from fulfilling your request. (HTTP 500) (Request-ID: req-9b2cf24c-b63b-4f66-a069-abba4e3cb766)
```

```bash
vi /var/log/keystone/keystone.log 

2019-04-24 16:32:59.961 23037 ERROR keystone OperationalError: (pymysql.err.OperationalError) (1045, u"Access denied for user 'keystone'@'controller' (using password: YES)") (Background on this error at: http://sqlalche.me/e/e3q8)
```

发现是数据库认证问题，可是明明给keystone用户加权限了啊。

##### 解决方案

经过多次测试，如果在mysql安全初始化时，不移除匿名用户、删除test数据库，就会出现以上授权问题

```bash
# mysql_secure_installation<<EOF
n
Y
Y
Y
Y
EOF
```

然后继续执行,查看keystone数据库中有没有表生成

```bash
su -s /bin/sh -c "keystone-manage db_sync" keystone
```

