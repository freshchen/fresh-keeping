# 自己制作Yum源

### 1背景

最近需要安装一套openstack，但是发现机器不能访问外网，我需要安装一些第三方服务，例如消息队列以及openstack各组件服务，这可真的头疼的问题。尝试源码安装，各种错误，依赖问题，心态爆炸。百度了一圈发现原来可以自制yum源给没网的机器用，这真的完美解决问题了，撒花。

### 2制作过程

首先准备一个和服务器相近版本的没有使用过的干净操作系统，我这里用的REHL7的操作系统，然后本机Docker基于准备的镜像起容器，下面的制作过程就在容器中进行。

#### 2.1启动缓存并安装yum源

一般的系统都自带yum了，我们只需要更改配置文件，

```bash
# vi /etc/yum.conf
[main]
cachedir=/var/cache/yum/$basearch/$releasever
keepcache=1
```

#### 2.2安装需要的服务

```bash
## 举例
yum install -y rabbitmq-server
```

#### 2.3打包

```bash
cd /var/cache/yum/x86_64/
tar -czvf <名字>.tar.gz 7Server/
```

### 3使用过程

我们将刚才打好的包传入不能连外网的服务器

#### 3.1安装createrepo 

REHL自带createrepo 

#### 3.2创建本地源

```bash
# 例如我想把包放在/home/pacakages下
mkdir -p /home/pacakages
# 解压
tar -xzvf /home/pacakages/<名字>.tar.gz
# 新建源目录
cd /home
createrepo pacakages/
```

#### 3.3配置源

```bash
cd /etc/yum.repos.d/
# 创建一个新的repo文件
vi base.repo
# 内容如下，主意名字就是createrepo的目录名字
[packages]
name=packages
baseurl=file:///home/packages/
enabled=1
gpgcheck=0
```

### 4.检查使用

可以使用以下命令来检验yum源是否安装成功

```bash
yum clean all
yum repolist
```

没有报错就可以开始安装啦

```bash
yum install -y rabbitmq-server
```