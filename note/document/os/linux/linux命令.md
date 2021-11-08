---
begin: 2021-11-02
status: ongoing
rating: 1
---

# linux命令
```bash
操作系统和内核版本
uname -a
hostnamectl
cat /proc/version
cat /etc/redhat-release

查看磁盘
df -h

查看分区
fdisk -l /dev/xvdb

分区
fdisk /dev/xvdb

磁盘格式化
mkfs.ext4 /dev/xvdb1

手动挂载
新建目录/u01`mkdir -p /u01`  
挂载设备到目录/u01`mount /dev/xvdb1 /u01`

开机自动挂载
修改`/etc/fstab`配置文件,末尾添加一行:
/dev/xvdb1              /u01                    ext4    defaults        0 0
```
## 参考链接
https://www.cnblogs.com/jyzhao/p/4778657.html

##### 标签
#linux
