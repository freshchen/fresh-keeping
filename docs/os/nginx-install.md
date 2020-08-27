# Nginx安装-基于腾讯云Centos7


### 1Nginx简介

Nginx是一款自由的、开源的、高性能的HTTP服务器和反向代理服务器；同时也是一个IMAP、POP3、SMTP代理服务器；Nginx可以作为一个HTTP服务器进行网站的发布处理，另外Nginx可以作为反向代理进行负载均衡的实现。正向代理，反向代理，负载均衡等概念参考如下博文。

[Nginx介绍](https://www.cnblogs.com/wcwnina/p/8728391.html)

### 2Nginx安装

[安装Nginx参考](./self-ssl-signing.md)

安装过程没遇到什么问题 一切顺利！

```bash
yum -y install make zlib zlib-devel gcc-c++ libtool  openssl openssl-devel
$ cd /usr/local/src/
$ wget http://downloads.sourceforge.net/project/pcre/pcre/8.35/pcre-8.35.tar.gz
$ tar zxvf pcre-8.35.tar.gz
$ cd pcre-8.35
$ ./configure
$ make && make install
#check
$ pcre-config --version
$ cd /usr/local/src/
$ wget http://nginx.org/download/nginx-1.6.2.tar.gz
$ tar zxvf nginx-1.6.2.tar.gz
$ cd nginx-1.6.2
$ ./configure --prefix=/usr/local/webserver/nginx --with-http_stub_status_module --with-http_ssl_module --with-pcre=/usr/local/src/pcre-8.35
$ make && make install
```



### 3常用命令

```bash
# 启动
[root@localhost ~]# /usr/local/webserver/nginx/sbin/nginx
# 停止/重启
[root@localhost ~]# /usr/local/webserver/nginx/sbin/nginx -s stop(quit、reload)
# 命令帮助
[root@localhost ~]# /usr/local/webserver/nginx/sbin/nginx -h
# 验证配置文件
[root@localhost ~]# /usr/local/webserver/nginx/sbin/nginx -t
# 配置文件
[root@localhost ~]# vim /usr/local/webserver/nginx/conf/nginx.conf
```

#### 4测试

正常启动了Nginx之后，访问自己服务器的ip，看到如下页面就成功了，至此安装成功

![](https://cdn.jsdelivr.net/gh/freshchen/resource@master/img/nginx-install.png)