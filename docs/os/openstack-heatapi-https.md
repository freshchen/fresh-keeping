# Openstack的Heat服务api支持HTTPS

### 背景Openstack Queens版本

需要Openstack的Heat服务api支持HTTPS，首先Openstack各服务默认支持HTTP要能正常访问，然后支持HTTPS首先要有证书，证书的制作过程参考本人的另一篇博客。

[自签的SSL证书创建与验证过程](https://freshchen.github.io/2019/02/18/self-ssl-signing/)

### 创建endpoint

```bash
$ openstack endpoint create --region RegionOne \
  orchestration public https://controller:8004/v1/%\(tenant_id\)s
```

### 配置

#### 将生成好的证书复制一份并且赋予权限

```bash
$ cp /root/ssl/self/* /etc/heat/self/

$ chown -R heat:heat /etc/heat/self/
```

#### 修改**/etc/heat/heat.conf**

```bash
[heat_api]
bind_port = 8004
cert_file = /etc/heat/self/server.crt
key_file = /etc/heat/self/server.key

[clients_heat]
endpoint_type = publicURL
insecure = True
url = https://10.175.183.15:8004/v1/%(tenant_id)s
```

#### 重启服务

```bash
systemctl restart openstack-heat-api.service openstack-heat-api-cfn.service openstack-heat-engine.service
```

#### 查看**/var/log/heat/heat-api.log** 

看到请求变成https就可以了

```bash
eventlet.wsgi.server [-] (9705) wsgi starting up on https://0.0.0.0:8004
INFO heat.common.wsgi [-] Started child 9706
INFO eventlet.wsgi.server [-] (9706) wsgi starting up on https://0.0.0.0:8004
INFO heat.common.wsgi [-] Started child 9707
INFO eventlet.wsgi.server [-] (9707) wsgi starting up on https://0.0.0.0:8004
```

#### 修改dashboard配置文件

正当得意之时发现dashboard访问不了了，好奇怪，也没有报错

```bash
# 监控
journalctl -xf

# 发现如下报错
Feb 20 03:14:27 controller heat-api[38517]: Traceback (most recent call last):
Feb 20 03:14:27 controller heat-api[38517]: File "/usr/lib/python2.7/site-packages/eventlet/greenpool.py", line 88, in _spawn_n_impl
Feb 20 03:14:27 controller heat-api[38517]: func(*args, **kwargs)
Feb 20 03:14:27 controller heat-api[38517]: File "/usr/lib/python2.7/site-packages/eventlet/wsgi.py", line 734, in process_request
Feb 20 03:14:27 controller heat-api[38517]: proto.__init__(sock, address, self)
Feb 20 03:14:27 controller heat-api[38517]: File "/usr/lib64/python2.7/SocketServer.py", line 649, in __init__
Feb 20 03:14:27 controller heat-api[38517]: self.handle()
Feb 20 03:14:27 controller heat-api[38517]: File "/usr/lib64/python2.7/BaseHTTPServer.py", line 340, in handle
Feb 20 03:14:27 controller heat-api[38517]: self.handle_one_request()
Feb 20 03:14:27 controller heat-api[38517]: File "/usr/lib/python2.7/site-packages/eventlet/wsgi.py", line 339, in handle_one_request
Feb 20 03:14:27 controller heat-api[38517]: self.raw_requestline = self.rfile.readline(self.server.url_length_limit)
Feb 20 03:14:27 controller heat-api[38517]: File "/usr/lib64/python2.7/socket.py", line 476, in readline
Feb 20 03:14:27 controller heat-api[38517]: data = self._sock.recv(self._rbufsize)
Feb 20 03:14:27 controller heat-api[38517]: File "/usr/lib/python2.7/site-packages/eventlet/green/ssl.py", line 194, in recv
Feb 20 03:14:27 controller heat-api[38517]: return self._base_recv(buflen, flags, into=False)
Feb 20 03:14:27 controller heat-api[38517]: File "/usr/lib/python2.7/site-packages/eventlet/green/ssl.py", line 227, in _base_recv
Feb 20 03:14:27 controller heat-api[38517]: read = self.read(nbytes)
Feb 20 03:14:27 controller heat-api[38517]: File "/usr/lib/python2.7/site-packages/eventlet/green/ssl.py", line 139, in read
Feb 20 03:14:27 controller heat-api[38517]: super(GreenSSLSocket, self).read, *args, **kwargs)
Feb 20 03:14:27 controller heat-api[38517]: File "/usr/lib/python2.7/site-packages/eventlet/green/ssl.py", line 113, in _call_trampolining
Feb 20 03:14:27 controller heat-api[38517]: return func(*a, **kw)
Feb 20 03:14:27 controller heat-api[38517]: File "/usr/lib64/python2.7/ssl.py", line 651, in read
Feb 20 03:14:27 controller heat-api[38517]: v = self._sslobj.read(len or 1024)
Feb 20 03:14:27 controller heat-api[38517]: SSLError: [SSL: SSL_HANDSHAKE_FAILURE] ssl handshake failure (_ssl.c:1822)
```

问题很清楚，heat-api改成https了，dashboard认证失败，于是开始研究httpd，尝试了很多地方配证书都失败了，最好在dashboard的配置文件中找到了问题解决办法。

**首先要先确认有没有安装mod_ssl服务**

```bash
$ vi /etc/openstack-dashboard/local_settings
# 修改
# Disable SSL certificate checks (useful for self-signed certificates):
OPENSTACK_SSL_NO_VERIFY = True

$ systemctl restart httpd.service
```

### 测试

##### 在环境变量**~/adminrc**中加入,然后source一下

```bash
export OS_CACERT=/root/ssl/self/server.crt
```

##### CLI命令测试，正常输出就可以把HEAT过去的http的8004相关的endpoint删除了

```bash
$ openstack stack list

$ tail -f /var/log/heat/heat-api.log
```



