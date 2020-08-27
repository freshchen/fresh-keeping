# Docker我的Hexo构建源码


## 前言

折腾了两天搭建出了自己的Hexo博客，Hexo要本地生成静态网站再推到github，这就太依赖本地环境了。最近打算换电脑，怎么解决这个问题呢，第一反应上云！

## 步骤

首先准备一个干净的系统基础镜像，例如centos，然后通过Dockerfile或者docker commit在基础镜像中完成如下操作：

1安装Git	安装node.js	安装hexo	

2docker cp 把hexo源代码复制到镜像中去

3配置git的host key

4写自动化跟新脚本，简单示例如下：

```bash

#!/bin/bash

tmpdir="/tmp/hexo/blog"
rm -rf $tmpdir
mkdir -p $tmpdir
cd $tmpdir
git clone git@github.com:freshchen/hexo-resource-for-docker.git

rm -rf /home/hexo/blog/source/_posts/*
rm -rf /home/hexo/blog/themes/next/source/images/

cp -r $tmpdir/hexo-resource-for-docker/blog/articles/* /home/hexo/blog/source/_posts/
cp -r $tmpdir/hexo-resource-for-docker/blog/resource@master/images/ /home/hexo/blog/themes/next/source/

cd  /home/hexo/blog
hexo g && hexo d

rm -rf $tmpdir
```

5Github仓库设置更新的webhook

6本机监听webhook启动更新，本地监听服务如下

```bash
# 主机上的systemd文件如下
#[Unit]
#After=network.target

#[Service]
#User=root
#Type=simple
#ExecStart=/bin/bash -xc 'nc -l 0.0.0.0 17732 && docker run --rm <github name>/hexo:blog sh /home/hexo/script/update-blog.sh > /var/log/update-hexo.log 2>&1 '
#Restart=always
#RestartSec=5
#StartLimitInterval=1min
#StartLimitBurst=60

#[Install]
#WantedBy=multi-user.target
```

