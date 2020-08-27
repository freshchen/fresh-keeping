# Python远程调用 Openstack 主要服务（keystone,nova,glance,neutron,heat）


由于Openstack跟新很快，现在准备搭建基于Queen版本的Openstack，Queen版本要求keystone版本为V3，所以之前大多数接口都不能用了，百度了一下都没有比较新的实例，官方文档又过于简单。所以简单总结下各客户端的调用方式，非常简单的案例，仅供参考。

## 1 前期准备

1确保已经安装了python

2根据需求安装openstack相关服务调用的python库

```
pip install python-openstackclient
```

**确保版本大于3.0.0，所有服务调用都基于keystone，一定要安装**

```
pip install python-keystoneclient
```

```
pip install python-heatclient
```

```
pip install python-glanceclient
```

```
pip install python-novaclient
```

```
pip install python-neutronclient
```

3如果安装的openstack的所有的入口ip都映射为controller，我们就需要在本机的/etc/hosts中也加一条映射

```
<ip> controller
```



## 2 获取客户端连接

### 1创建keystone session ：最近版本的openstack所有服务都只能keystone v3版本才能调用，并且推荐session的方式。

```python
from keystoneauth1.identity import v3
from keystoneauth1 import session

def get_keystone_session():
    # auth_url为keystone的endpoint入口，新版本openstack中(Tenant租户改名为project）
    auth = v3.Password(auth_url="http://<ip>:5000/v3", username="",password="", project_name="",user_domain_id="", project_domain_id="")
    sess = session.Session(auth=auth)
    return sess
```



### 2获取keystone客户端

```python
from keystoneclient.v3 import client as keyclient

def get_keystone_client():
    sess = get_keystone_session()
    keystone = keyclient.Client(session=sess)
    return keystone
```



### 3获取nova客户端

```python
from novaclient import client

def get_nova_client():
    sess = get_keystone_session()
    nova = client.Client(2, session=sess)
    return nova
```



### 4获取glance客户端

```python
from glanceclient import Client

def get_glance_client():
    sess = get_keystone_session()
    glance = Client('2', session=sess)
    return glance
```



### 5获取neutron客户端

```python
from neutronclient.v2_0 import client as ntclient

def get_neutron_client():
    sess = get_keystone_session()
    neutron = ntclient.Client(session=sess)
    return neutron
```



### 6获取heat客户端

heat客户端的获取比较复杂，尝试了多种方式发现只能通过keystone客户端返回的token的认证

```python
from heatclient import client as hclient

def get_heat_client():
    creds = {}
    creds['username'] = ''
    creds['password'] = ''
    creds['auth_url'] = 'http://<ip>:5000/v3'
    creds['project_name'] = ''
    ks_client = keyclient.Client(**creds)
    heat_endpoint = ks_client.service_catalog.url_for(service_type='orchestration', endpoint_type='publicURL')
    # 后来需求Heat服务单独改为HTTPS，可以在以下参数中加入 insecure=True
    heat = hclient.Client('1', heat_endpoint, token=ks_client.auth_token)
    return heat
```



## 3简单测试

之前的获取客户端方式写在openstackapi.py中方便调用

### 1keystone

```python
import openstackapi as api

keystone = api.get_keystone_client()
list = keystone.projects.list()
for p in list:
    print p
```



### 2nova

python-novaclient8之后network被移除，应该用neutron代替

```python
import openstackapi as api

def nova_create_flavor(nova, instance_name):
    nova.servers.create(instance_name)

def show_server_info(nova):
    instances = nova.servers.list()
    for instance in instances:
        print instance

def show_flavor_info(nova):
    flavors = nova.flavors.list()
    for flavor in flavors:
        print flavor

def get_instance_id(nova, instance_name):
    instances = nova.servers.list()
    for instance in instances:
        if instance.name == instance_name:
            return instance.id

def get_instance(nova, instance_id):
    return nova.servers.get(instance_id)

def get_flavor_id(nova, flavor_name):
    flavors = nova.flavors.list()
    for flavor in flavors:
        if flavor.name == flavor_name:
            return flavor.id

if __name__ == '__main__':
    nova = api.get_nova_client()
    show_server_info(nova)
    show_flavor_info(nova)
    id =  get_instance_id(nova, <name>)
    print id
    instance = get_instance(nova,id)
    print '################################'
    print instance

```



### 3glance

创建上传qcow2镜像，删除镜像

```python
import openstackapi as api

def list_image():
    list = glance.images.list()
    for image in list:
        print image.name, image.id, image.status

def get_id_by_name(name):
    list = glance.images.list()
    id = ''
    for image in list:
        if image.name == name:
            id = image.id
            return id

if __name__ == "__main__":
    glance = api.get_glance_client()
    print '#######   list  #########'
    list_image()
    name = "test-py-api"
    glance.images.create(name=name, disk_format="qcow2", container_format="bare", is_public="true")
    print '#######   list after create #########'
    list = list_image()
    id = get_id_by_name(name)
    glance.images.upload(id , open('/目录/***.qcow2', 'rb'))
    print '#######   list after upload qcow2   #########'
    list_image()
    glance.images.delete(id)
    print '#######   list after delete qcow2   #########'
    list_image()
```



### 4neutron

```python
import openstackapi as api

def show_network_info(neutron):
    print ' ####  network list ####'
    networks = neutron.list_networks()
    for network in networks:
        print network

if __name__ == '__main__':
    neutron  = api.get_neutron_client()
    show_network_info(neutron)
```



### 5heat

首先写好一个heat编排模板，然后通过接口上传并且自动执行编排

```python
import openstackapi as api
from heatclient.common import template_utils

def get_heat_file():
    path = "/目录/***.yaml"
    tpl_files, template = template_utils.get_template_contents(path)
    create_fields = {
        'stack_name': '',
        'disable_rollback': 'false',
        'parameters': '',
        'template': template,
        'files': dict(list(tpl_files.items()))
    }
    return create_fields

def list_stack(heat):
    list = heat.stacks.list()
    for stack in list:
        print stack


if __name__ == "__main__":
    heat = api.get_heat_client()
    create_fields = get_heat_file()
    heat.stacks.create(**create_fields)
    list_stack(heat)

```

## 4接口汇总

```python
from keystoneauth1.identity import v3
from keystoneauth1 import session
from keystoneauth1 import loading
from keystoneclient.v3 import client as keyclient
from glanceclient import Client
from novaclient import client
from neutronclient.v2_0 import client as ntclient
from heatclient import client as hclient


def get_keystone_session():
    loader = loading.get_plugin_loader('password')
    auth = v3.Password(auth_url="http://<Openstack Controller Ip>:5000/v3", username="admin",password="<pwd>", project_name="admin",user_domain_id="default", project_domain_id="default")
    sess = session.Session(auth=auth)
    return sess

def get_nova_client():
    sess = get_keystone_session()
    nova = client.Client(2, session=sess)
    return nova

def get_glance_client():
    sess = get_keystone_session()
    glance = Client('2', session=sess)
    return glance

def get_keystone_client():
    sess = get_keystone_session()
    keystone = keyclient.Client(session=sess)
    return keystone

def get_neutron_client():
    sess = get_keystone_session()
    neutron = ntclient.Client(session=sess)
    return neutron

def get_heat_client():
    creds = {}
    creds['username'] = 'admin'
    creds['password'] = 'admin1234'
    creds['auth_url'] = 'http://<Openstack Controller Ip>:5000/v3'
    creds['project_name'] = 'admin'
    ks_client = keyclient.Client(**creds)
    heat_endpoint = ks_client.service_catalog.url_for(service_type='orchestration', endpoint_type='publicURL')
    heat = hclient.Client('1', heat_endpoint, token=ks_client.auth_token)
    return heat

def list_images():
    glance = get_glance_client()
    list = glance.images.list()
    return list

def show_images():
    list = list_images()
    for image in list:
        print image.name, image.id, image.status

        def get_image_id_by_name(image_name):
    list = list_images()
    id = ''
    for image in list:
        if image.name == image_name:
            id = image.id
            return id
    return id

def upload_image(image_name, image_path):
    glance = get_glance_client()
    glance.images.create(name=image_name, disk_format="qcow2", container_format="bare", is_public="true")
    id = get_image_id_by_name(image_name)
    glance.images.upload(id , open(image_path, 'rb'))

def delete_image(image_name):
    glance = get_glance_client()
    id = get_image_id_by_name(image_name)
    if id != '':
        glance.images.delete(id)

```

