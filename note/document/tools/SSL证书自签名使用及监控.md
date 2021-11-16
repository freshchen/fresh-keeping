---
begin: 2021-11-16
status: done
rating: 1
---

# SSL证书自签名使用及监控

## 前言

### 证书简介

信息安全越来越受重视，HTTPS已经相当普及，要让我们的HTTP接口支持HTPPS，只需要一个SSL证书就可以啦

-  全称公钥证书（Public-Key Certificate, PKC）,里面保存着归属者的基本信息，以及证书过期时间、归属者的公钥，并由认证机构（Certification Authority, **CA**）施加数字签名，表明，某个认证机构认定该公钥的确属于此人 
-  自签名的证书，就是我们来充当 认证机构，这种证书推荐在测试开发过程中，生产环境直接上CA证书省心！

## 实践

### 证书生成

#### 准备

- 确保有openssl库

- 创建目录

  ```bash
  # 根证书目录
  [root@centos7 ~]# mkdir -p /var/ssl/crt/root
  # 服务器端证书目录
  [root@centos7 ~]# mkdir -p /var/ssl/crt/server
  ```

#### 生成根证书

- 私钥，RSA对称加密，aes比des更安全，密钥长度2048

  ```bash
  openssl genrsa -aes256 -out /var/ssl/crt/root/ca.key -passout pass:"123456" 2048
  ```

- 请求流程，包含证书信息,**其中比较关键的是CN，是填你的域名，根证书不起服务可以随便写**，

  ```bash
  openssl req -new -key /var/ssl/crt/root/ca.key -out /var/ssl/crt/root/ca.csr -subj "/C=CN/ST=myprovince/L=mycity/O=myorganization/OU=mygroup/CN=www.ca.crt.com/emailAddress=my@mail.com" -passin pass:"123456"
  ```

- 颁发证书，这个生成的就是可以用的证书了，注意不加v3_ca这个插件，浏览器导入不了

  ```bash
  openssl x509 -req -sha256 -extensions v3_ca -days 3650 -in /var/ssl/crt/root/ca.csr -out /var/ssl/crt/root/ca.crt -signkey /var/ssl/crt/root/ca.key -CAcreateserial -passin pass:"123456"
  ```

#### 生成服务端证书

有了根证书之后，我们将所有的服务端证书都从根证书签出，方便客户端用根证书统一访问

- 私钥，RSA对称加密，aes比des更安全，密钥长度2048

  ```bash
  openssl genrsa -aes256 -out /var/ssl/crt/server/svc1-server.key -passout pass:"123456" 2048
  ```

- 请求流程，包含证书信息,**其中比较关键的是CN，是填你的域名**

  ```bash
  openssl req -new -key /var/ssl/crt/server/svc1-server.key -out /var/ssl/crt/server/svc1-server.csr -subj "/C=CN/ST=myprovince/L=mycity/O=myorganization/OU=mygroup/CN=www.svc1.com/emailAddress=my@mail.com" -passin pass:"123456"
  ```

- 颁发证书，这个生成的就是可以用的证书了，注意不加v3_ca这个插件，浏览器导入不了

  ```bash
  openssl x509 -req -sha256 -extensions v3_req -days 3650 -in /var/ssl/crt/server/svc1-server.csr -out /var/ssl/crt/server/svc1-server.crt -signkey /var/ssl/crt/server/svc1-server.key -CAkey /var/ssl/crt/root/ca.key -CA /var/ssl/crt/root/ca.crt -CAcreateserial -passin pass:"123456"
  ```

- 验证证书

  ```bash
  openssl verify -CAfile /var/ssl/crt/root/ca.crt /var/ssl/crt/server/svc1-server.crt
  ```

### 证书使用

#### Unubtu为例


```bash
apt install nginx -y
```

#### 配置

```bash
vi /etc/nginx/sites-available/default

# 到最后加上如下内容
server {
    listen       443 ssl http2 default_server;
    listen       [::]:443 ssl http2 default_server;
    server_name  www.svc1.com;
    root         /usr/share/nginx/html;
    ssl_certificate "/var/ssl/crt/server/svc1-server.crt";
    ssl_certificate_key "/var/ssl/crt/server/svc1-server.key";
    ssl_session_cache shared:SSL:1m;
    ssl_session_timeout  10m;
    ssl_ciphers HIGH:!aNULL:!MD5;
    ssl_prefer_server_ciphers on;
    include /etc/nginx/default.d/*.conf;
    location / {
    }
    error_page 404 /404.html;
    location = /40x.html {
    }
    error_page 500 502 503 504 /50x.html;
    location = /50x.html {
    }
}
```

```bash
vi /etc/hosts

127.0.0.1 www.svc1.com
```

#### 启动

```bash
root@CN-00013965:/# sudo service nginx restart 
 * Restarting nginx nginx                                                                                                                                                           Enter PEM pass phrase:
Enter PEM pass phrase:                              [ OK ]
```

#### 测试

- 不用证书 失败

  ```bash
  root@CN-00013965:/# wget https://www.svc1.com
  --2019-10-18 16:37:48--  https://www.svc1.com/
  Resolving www.svc1.com (www.svc1.com)... 127.0.0.1
  Connecting to www.svc1.com (www.svc1.com)|127.0.0.1|:443... connected.
  ERROR: cannot verify www.svc1.com's certificate, issued by ‘emailAddress=my@mail.com,CN=www.ca.crt.com,OU=mygroup,O=myorganization,L=mycity,ST=myprovince,C=CN’:
    Unable to locally verify the issuer's authority.
  To connect to www.svc1.com insecurely, use `--no-check-certificate'.
  ```

- 用根证书访问 成功

  ```bash
  root@CN-00013965:/# wget --ca-certificate=/var/ssl/crt/root/ca.crt https://www.svc1.com
  --2019-10-18 16:39:50--  https://www.svc1.com/
  Resolving www.svc1.com (www.svc1.com)... 127.0.0.1
  Connecting to www.svc1.com (www.svc1.com)|127.0.0.1|:443... connected.
  HTTP request sent, awaiting response... 200 OK
  Length: 612 [text/html]
  Saving to: ‘index.html.1’
  
  index.html.1                                         100%[===================================================================================================================>]     612  --.-KB/s    in 0s      
  
  2019-10-18 16:39:50 (32.4 MB/s) - ‘index.html.1’ saved [612/612]
  ```

#### 导入Java应用

java应用要读取服务端证书需要通过pkcs12格式的keystore文件，我们可以把不同的服务端证书用别名区分。然后我们读取trustkeystore去访问HTTPS其他服务

- **生成keystore**

  ```bash
  openssl pkcs12 -export -clcerts -in /var/ssl/crt/server/svc1-server.crt -inkey /var/ssl/crt/server/svc1-server.key -out /var/ssl/crt/server/svc1-server.p12 -name svc1 -passin pass:"123456" -password pass:"123456"
  ```

  keytool命令是JDK自带的到${JAVA_HOME}/bin下执行，-srcstorepass是我们证书的密码，其他两个是keystore的密码

  ```bash
  keytool -importkeystore -trustcacerts -noprompt -deststoretype pkcs12 -srcstoretype pkcs12 -srckeystore /var/ssl/crt/server/svc1-server.p12 -destkeystore /var/ssl/crt/server/svc1-server.keystore -alias svc1 -deststorepass "123456" -destkeypass "123456" -srcstorepass "123456"
  ```

- **生成trustkeystore**

  ```bash
  keytool -import -trustcacerts -noprompt -alias CA -file /var/ssl/crt/root/ca.crt -keystore /var/ssl/crt/root/ca.trustkeystore -storepass "123456"
  ```

### 监控

证书起到服务端口上了，我们怎么查看证书信息，或者实时检查证书过期信息呢，已默认443端口为例

```bash
root@CN-00013965:/# echo 'Q' | timeout 5 openssl s_client -connect 127.0.0.1:443  2>/dev/null | openssl x509 -noout -subject -dates

subject=C = CN, ST = myprovince, L = mycity, O = myorganization, OU = mygroup, CN = www.svc1.com, emailAddress = my@mail.com
notBefore=Oct 18 08:09:32 2019 GMT
notAfter=Oct 15 08:09:32 2029 GMT
```

如果在用postgresql数据库起的HTTPS，那么直接openssl不能直接拿到端口证书，我们可以借助python脚本，脚本是Github上找的

```python
#!/usr/bin/env python

import argparse
import socket
import ssl
import struct
import subprocess
import sys

try:
    from urlparse import urlparse
except ImportError:
    from urllib.parse import urlparse


def main():
    args = get_args()
    target = get_target_address_from_args(args)
    sock = socket.create_connection(target)
    try:
        certificate_as_pem = get_certificate_from_socket(sock)
        print(certificate_as_pem.decode('utf-8'))
    except Exception as exc:
        sys.stderr.write('Something failed while fetching certificate: {0}\n'.format(exc))
        sys.exit(1)
    finally:
        sock.close()


def get_args():
    parser = argparse.ArgumentParser()
    parser.add_argument('database', help='Either an IP address, hostname or URL with host and port')
    return parser.parse_args()


def get_target_address_from_args(args):
    specified_target = args.database
    if '//' not in specified_target:
        specified_target = '//' + specified_target
    parsed = urlparse(specified_target)
    return (parsed.hostname, parsed.port or 5432)


def get_certificate_from_socket(sock):
    request_ssl(sock)
    ssl_context = get_ssl_context()
    sock = ssl_context.wrap_socket(sock)
    sock.do_handshake()
    certificate_as_der = sock.getpeercert(binary_form=True)
    certificate_as_pem = encode_der_as_pem(certificate_as_der)
    return certificate_as_pem


def request_ssl(sock):
    version_ssl = postgres_protocol_version_to_binary(1234, 5679)
    length = struct.pack('!I', 8)
    packet = length + version_ssl

    sock.sendall(packet)
    data = read_n_bytes_from_socket(sock, 1)
    if data != b'S':
        raise Exception('Backend does not support TLS')


def get_ssl_context():
    for proto in ('PROTOCOL_TLSv1_2', 'PROTOCOL_TLSv1', 'PROTOCOL_SSLv23'):
        protocol = getattr(ssl, proto, None)
        if protocol:
            break
    return ssl.SSLContext(protocol)


def encode_der_as_pem(cert):
    cmd = ['openssl', 'x509', '-inform', 'DER']
    pipe = subprocess.PIPE
    process = subprocess.Popen(cmd, stdin=pipe, stdout=pipe, stderr=pipe)
    stdout, stderr = process.communicate(cert)
    if stderr:
        raise Exception('OpenSSL error when converting cert to PEM: {0}'.format(stderr))
    return stdout.strip()


def read_n_bytes_from_socket(sock, n):
    buf = bytearray(n)
    view = memoryview(buf)
    while n:
        nbytes = sock.recv_into(view, n)
        view = view[nbytes:] # slicing views is cheap
        n -= nbytes
    return buf


def postgres_protocol_version_to_binary(major, minor):
    return struct.pack('!I', major << 16 | minor)


if __name__ == '__main__':
    main()
```

使用方法：复制上面脚本，文件名get_postgres_cert.py

```bash
python get_postgres_cert.py 127.0.0.1:5432
```

## 参考链接


##### 标签
#tools #linux 