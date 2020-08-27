# Common Commands

Linux服务器常用命令

## Common Commands

### 文本处理

```bash

# 目录dos2unix转换格式
find . -type f -exec dos2unix {} \;

########################      $      ########################

# 变量要保留其原来的换行符要加双引号，建议所有变量引用都用双引号加大括号圈上
echo "${var}"
# 变量长度
echo "${#var}"
# 接受所有参数
$@
# 查看数组中所有元素
${list[@]}
# 查看数组长度
${#list[@]}
# 去掉全路径的文件名，只保留目录
${path%/*}

########################      =      ########################

# 定义数组
list=("1" "2" "3")
# 定义map
declare -A map=(["1"]="name" ["2"]="age")

########################     grep      ########################

# 删除空白行和注释行
cat <file> | grep -v ^# | grep .
cat <file> | grep -Ev '^$|^#'

########################     grep      ########################

# 去掉行尾巴空格
echo ${var} | sed 's/[ \t]*$//g'
# 去掉单引号
echo ${var} | sed $'s/\'//g'

########################      awk       ########################

# 例如查看状态是UNCONN,Recv-Q是0的端口信息

ss -ln | awk '($2=="UNCONN" && $3=="0") {print $0}'

# 统计状态是UNCONN,Recv-Q是0的端口的netid和出现的次数

ss -ln | awk '($2=="UNCONN" && $3=="0") {netids[$1]++}END{for(i in netids)print i "\t" netids[i]}'

########################      tr       ########################

# 大写转小写
echo ${var} | tr 'A-Z' 'a-z'

########################      od       ########################

# 字符串转ASCLL码
echo "${var}" | tr -d "\n" | od -An -t dC

```

### 系统管理

```bash
########################     process      ########################

# 查看后台job
jobs
# 后台运行
( cmd ) &
# 唤醒
fg %<job_num>
# 暂停放入后台
ctrl z
# 唤醒stop的job
bg %<job_num>
# 发送信号，优先15SIGTERM，不行再9SIGKILL
kill -SIGTERM <PID>
# 杀用户所有进程
pkill -SIGTERM -u <user_name>
# 杀父进程
pkill -P <PID>
# 杀终端
pkill -SIGTERM -u <tty_name>
# 查看cpu信息
cat /proc/cpuinfo

########################     systemctl      ########################

# 查看服务单元
systemctl
systemctl --type service
systemctl list-units
# 判断状态
systemctl <is-active|is-enabled|is-failed|isolate|is-system-running> <unit_name>
# 看错误信息
systemctl --failed
systemctl status <unit_name> -l
# 看enable disable static的单元
systemctl list-unit-files

########################     journalctl      ########################

# 查看systemd日志
journalctl
# 指定级别
journalctl -p <err|debug|info|warning...>
# 持续打印
journalctl -f
# 指定单元
journalctl -u

########################     find      ########################

# 根据名字找
find <dir> -name <org>
# 根据用户找
find <dir> -user <org>
# 根据组找
find <dir> -group <org>
# 根据权限找
find <dir> -perm <org>
# 根据大小找
find <dir> -size <org>
# 根据更改找
find <dir> -mmin <org>
# 根据类型找
find <dir> -type <l|b|f>


########################     timedatectl      ########################

# 当前时钟时区
timedatectl
# 设置
timedatectl <set-ntp|set-time|set-timezone|set-local-rtc> 

########################     hostname      ########################

# 查看hostname信息
hostnamectl status
# 本地域名解析位置
cat /etc/hosts
cat /etc/resolv.conf

########################     ip      ########################

# 检查网络设备
ip addr show <eno>
# 查看网络性能
ip -s link <eno>
# 跟踪请求路径
tracepath
tracepath6

########################     nmcli      ########################

# 查看网络连接
nmcli con show
# 查看网络设备信息
nmcli dev show <eno>
# 修改网络接口
nmcli con add
nmcli con mod
# 激活/取消连接
nmcli con up "<id>"
nmcli con down "<id>"
# 网络配置文件位置
ls /etc/sysconfig/network-scripts/


########################      yum        ########################

yum <repolist|list|search|install|remove|update>

########################      rpm        ########################

# 查找rpm包
rpm -qa | grep <name>
# 查看rpm信息
rpm -qi <name>
# 解压rpm包
rpm2cpio <rpm> | cpio -id

########################     user:group      ########################

# 用户信息
cat /etc/passwd
# 组信息
cat /etc/group
# 更改用户或组
chown -R <user> <dir>
chown -R :<group> <dir>
chown -R <user>:<group> <dir>
# 改权限
chmod -R 750 <file>

########################     ssh      ########################

# 显示当前登录信息
w -f
# 公钥存放位置
cat ~/.ssh/known_hosts
# 私钥位置
ls /etc/ssh/ssh_host_*
# 创建私钥公钥对
ssh-keygen
# 将公钥复制到远程机器实现互信
ssh-copy-id <user>@<host>

########################     fs      ########################

# 检测文件挂载点
df -h
# 检测目录使用空间信息
du -h <dir>
# 文件系统挂在
mount <dir> <dir>
# 查看目录中所有打开的文件和正在运行的进程
lsof <dir>
# 取消挂载
umount <dir>

########################     ln      ########################

# 创建硬连接
ln <exist_path> <path>
# 创建软连接
ln -s <exist_path> <path>
```


### 应用

```bash
########################      docker        ########################

# 删除tag和name为none的坏掉的image
docker rmi $(docker images -f "dangling=true" -q)
# 删掉所有容器
docker stop $(docker ps -qa)
docker kill $(docker ps -qa)
docker rm $(docker ps -qa)
# 删除所有镜像
docker rmi --force $(docker images -q)

########################      mysql        ########################

# 查配置
show variables like '%';
# 放开用户的远程操作权限
GRANT ALL PRIVILEGES ON *.* TO '<user>'@'%' IDENTIFIED BY '<password>' WITH GRANT OPTION;
# 刷新权限规则生效
flush privileges;
# 在线改配置
set <global|session>

########################      openssl        ########################

# 查看证书过期时间
openssl x509 -noout -enddate -in <crt_path>
# 获取端口证书过期时间
echo 'Q' | timeout 5 openssl s_client -connect <host:port> 2>/dev/null | openssl x509 -noout -enddate
# 自签根证书
openssl genrsa -aes256 -out <ca私钥位置> 2048
openssl req -new -key <ca私钥位置> -out <ca签发流程位置> -subj "/C=/ST=/L=/O=/OU=/CN=/emailAddress="
openssl x509 -req -sha256 -days <过期天数> -in <ca签发流程位置> -out <ca证书位置> -signkey <ca私钥位置> -CAcreateserial
# 根证书签发子证书
openssl genrsa -aes256 -out <私钥位置> 2048
openssl req -new -key <私钥位置> -out <签发流程位置> -subj "/C=/ST=/L=/O=/OU=/CN=/emailAddress=" 
openssl x509 -req -sha256 -days <过期天数> -in <签发流程位置> -out <证书位置> -signkey <私钥位置> -CAkey <ca私钥> -CA <ca证书位置> -CAcreateserial
openssl pkcs12 -export -clcerts -in <证书位置> -inkey <私钥位置> -out <p12证书位置> -name <别名>

########################      keytool        ########################

# 查看keystore
${JAVA_HOME}/bin/keytool -v -list -storepass <password> -keystore <keystore_path>
# 导入trust keystore
${JAVA_HOME}/bin/keytool -import -trustcacerts -noprompt -alias <别名> -file <证书位置> -keystore <Keystore位置>
# 导入keystore
${JAVA_HOME}/bin/keytool -importkeystore -trustcacerts -noprompt -alias <别名> -deststoretype pkcs12 -srcstoretype pkcs12 -srckeystore <p12证书位置> -destkeystore <Keystore位置>

```
