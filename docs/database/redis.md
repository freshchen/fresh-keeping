# Redis

## 简介

Redis是一个开源(BSD许可)的基于内存的数据存储，可用作数据库、缓存和消息代理。它支持诸如字符串、散列、列表、集、带范围查询的排序集、位图、hyperloglog、带半径查询和流的地理空间索引等数据结构。Redis具有内置的复制、Lua脚本、LRU清除、事务和不同级别的磁盘持久性，并通过Redis Sentinel和Redis集群的自动分区提供高可用性。

## 场景

- 点赞
- 投票
- 排行
- 会话管理（分布式session，cookie存储）
- 浏览记录
- 购物车
- 网页缓存



## 基础数据结构常用命令

[Redis数据类型官网](<https://redis.io/topics/data-types-intro>)

### key

| Key相关命令 | 示例           | 描述                                        |
| --------- | -------------- | ------------------------------------------- |
| SET       | SET name lingc | 给key赋予string类型值，key重复则覆盖        |
| GET       | GET name       | 得到key的值，如果得到的不是string类型值报错 |
| KEYS      | KEYS *         | 查寻key，支持正则                           |
| RANDOMKEY | RANDOMKEY      | 随机显示一个key                             |
| EXISTS    | EXISTS name    | 判断key是否存在                             |
|TYPE|TYPE name|看key类型|
|DEL|DEL name|删除key|
|RENAME|RENAME name myname|更改key，如果要变为的key存在，覆盖原值|
|RENAMENX|RENAMENX name myname|更改key，如果要变为的key存在，则放弃操作|
|SELECT|SELECT 0|切换数据库，默认有16个库（0-15）|
|MOVE|MOVE name 1|把key移到别的库|
|EXPIRE|EXPIRE name 10|设置key的生命周期，单位秒|
|PERSIST|PERSIST name|设置key的生命周期为永久|
|TTL|TTL name|查看key的生命周期|

### String


| String相关命令 | 示例           | 描述                                        |
| --------- | -------------- | ------------------------------------------- |
|set key value [ex 秒数] / [px 毫秒数]  [nx] /[xx]|set a 1 ex 10 nx|ex存在秒数，px存在毫秒，nx如果不存在执行，xx存在依然执行|
|mset  multi set|mset key1 v1 key2 v2|一次设置多个key|
|mget key1 key2 ..keyn|mget key1 key2|一次查询多个key|
|setrange key offset value|setrange name  2 x|把字符串的offset偏移字节,改成value，注意: 如果偏移量大于字符长度, 该字符自动补0x00|
|append key value|append name chen|把value追加到key的原值上|
|getrange key start stop|getrange name 0 3|是获取字符串中 [start, stop]范围的值,对于字符串的下标,左数从0开始,右数从-1开始|
|getset key newvalue|getset name ling|原子操作，先输出原来的值，再写入新值|
|incr key <num可选>|incr age|指定的key的值加1,并返回加1后的值|
|decr key <num可选>|decr age|指定的key的值减1,并返回加1减的值|
|getbit key offset|set char A, getbit char 1|获取值的二进制表示,对应位上的值(从左,从0编号)|
|setbit  key offset value|setbit char 0 1|设置offset对应二进制位上的值,返回: 该位上的旧值|
|bitop operation destkey key1 [key2 ...]|bitop or char char lower|对key1,key2..keyN作operation,并将结果保存到 destkey 上。注意: 对于NOT操作, key不能多个|

### Linked List

| Link相关命令 | 示例           | 描述                                        |
| --------- | -------------- | ------------------------------------------- |
|lpush key value|lpush link1 a|把值插入到链表左边|
|rpush key value|rpush link1 a|把值插入到链表右边|
|lpop key|lpop link1|弹出链表左边第一个值|
|rpop key|rpop link1|弹出链表右边第一个值|
|lrange key start  stop|lrange link1 0 -1|返回链表中[start,stop]中的元素|
|lrem key count value|lrem link1 3 a|作用: 从key链表中删除 value值，注: 删除count的绝对值个value后结束|
|ltrim key start stop|ltrim link1 1 3|剪切key对应的链接,切[start,stop]一段,并把该段重新赋给key|
|lindex key index|lindex link1 3|返回index索引上的值,|
|llen key|llen link1|计算链接表的元素个数|
|linsert  key [after.before] search value|linsert link1 after  c aaa|作用: 在key链表中寻找’search’,并在search值之前之后,插入value，注: 一旦找到一个search后,命令就结束了,因此不会插入多个value|
|rpoplpush source dest|rpoplpushlink1 link2|把source的尾部拿出,放在dest的头部,并返回 该单元值|
|brpop ,blpop  key timeout|brpop link1 100|等待弹出key的尾/头元素, Timeout为等待超时时间，如果timeout为0,则一直等待|

### Set

| Set相关命令 | 示例           | 描述                                        |
| --------- | -------------- | ------------------------------------------- |
|sadd key  value1 value2|sadd set1 a b c d e f g|往集合key中增加元素|
|srem  key value1 value2|srem set1 c|删除集合中集为value1 value2的元素返回值: 忽略不存在的元素后,真正删除掉的元素的个数|
|spop key|spop set1|返回并删除集合中key中1个随机元素|
|srandmember key|srandmember set1|返回集合key中,随机的1个元素.|
|sismember key  value|sismember set1 b|判断value是否在key集合中是返回1,否返回0|
|smembers key|smembers set1| 返回集中中所有的元素 |
|scard key|scard set1|返回集合中元素的个数|
|smove key1 key2 value|smove set1 set2 a|把key1中的value删除,并添加到key2集合中|
|sinter  key1 key2 key3|sadd s1 0 2 4 6|求出key1 key2 key3 三个集合中的交集,并返回|
|sinterstore dest key1 key2 key3|sinterstoreset1 set2 set3|求出key1 key2 key3 三个集合中的交集,并赋给dest|
|suion key1 key2.. Keyn|suionset1 set2 set3|求出key1 key2 keyn的并集,并返回|
|sdiff key1 key2 key3|sdiff set1 set2 set3|求出key1与key2 key3的差集|

### Order Set


| order set 相关命令 | 示例           | 描述                                        |
| --------- | -------------- | ------------------------------------------- |
|zadd key score1 value1 score2 value2|zadd stu 18 lily 19 hmm 20 lilei 21 lilei|定义一个有序集合并且添加元素|
|zrem key value1 value2|zrem|删除集合中的元素|
|zremrangebyscore key min max|zremrangebyscore stu 4 10|按照socre来删除元素,删除score在[min,max]之间的|
|zremrangebyrank key start end|zremrangebyrank stu 0 1|按排名删除元素,删除名次在[start,end]之间的|
|zrank key member|zrank stu lilei|查询member的排名(升续 0名开始)|
|zrevrank key memeber|zrevrank  stu hmm|查询 member的排名(降续 0名开始)|
|ZRANGE key start stop [WITHSCORES]|zrange stu 0 2|把集合排序后,返回名次[start,stop]的元素，默认是升续排列|
|zrevrange key start stop|zrevrange stu 0 2|把集合降序排列,取名字[start,stop]之间的元素|
|zrangebyscore  key min max [withscores] limit offset N|zadd stu 1 a 3 b 4 c 9 e 12 f 15 g，，zrangebyscore stu 3 12 limit 1 2 withscores|集合(升续)排序后,取score在[min,max]内的元素,并跳过 offset个, 取出N个|
|zcard key|zcard stu|返回元素个数|
|zcount key min max|zcount stu 1 3|返回[min,max] 区间内元素的数量|

### Hash


| hash相关命令 | 示例           | 描述                                        |
| --------- | -------------- | ------------------------------------------- |
|hset key field value|hset map1 name ling|把key中 filed域的值设为value|
|hmset key field1 value1 [field2 value2 field3 value3 ......fieldn valuen]|hmget map1 age name|设置field1->N 个域, 对应的值是value1->N|
|hget key field|hget map1 age|返回key中field域的值|
|hmget key field1 field2 fieldN|hmget map1 age name|返回key中field1 field2 fieldN域的值|
|hgetall key|hgetall map1|返回key中,所有域与其值|
|hdel key field|hdel map1 sex|删除key中 field域|
|hlen key|hlen map1|返回key中元素的数量|
|hexists key field|hexists map1 name|判断key中有没有field域|
|hincrby key field value|HINCRBY map1 age 1|是把key中的field域的值整型值增加|
|hkeys key|hkeys  map1|返回key中所有的field|
|hvals key|hvals map1|返回key中所有的value|



## Redis事务

[Redis的事务功能详解](<https://www.cnblogs.com/kyrin/p/5967620.html>)

### MULTI

用于标记事务块的开始。Redis会将后续的命令逐个放入队列中，然后才能使用EXEC命令原子化地执行这个命令序列。

这个命令的运行格式如下所示：

MULTI

这个命令的返回值是一个简单的字符串，总是OK。

### EXEC

在一个事务中执行所有先前放入队列的命令，然后恢复正常的连接状态。

当使用WATCH命令时，只有当受监控的键没有被修改时，EXEC命令才会执行事务中的命令，这种方式利用了检查再设置（CAS）的机制。

这个命令的运行格式如下所示：

EXEC

这个命令的返回值是一个数组，其中的每个元素分别是原子化事务中的每个命令的返回值。 当使用WATCH命令时，如果事务执行中止，那么EXEC命令就会返回一个Null值。

### DISCARD

清除所有先前在一个事务中放入队列的命令，然后恢复正常的连接状态。

如果使用了WATCH命令，那么DISCARD命令就会将当前连接监控的所有键取消监控。

这个命令的运行格式如下所示：

```
DISCARD
```

这个命令的返回值是一个简单的字符串，总是OK。

### WATCH

当某个事务需要按条件执行时，就要使用这个命令将给定的键设置为受监控的。

这个命令的运行格式如下所示：

```
WATCH key [key ...]
```

这个命令的返回值是一个简单的字符串，总是OK。

对于每个键来说，时间复杂度总是O(1)。

### UNWATCH

清除所有先前为一个事务监控的键。

如果你调用了EXEC或DISCARD命令，那么就不需要手动调用UNWATCH命令。

这个命令的运行格式如下所示：

```
UNWATCH
```

这个命令的返回值是一个简单的字符串，总是OK。

时间复杂度总是O(1)。

**Redis与 mysql事务的对比**

|      | Mysql             | Redis        |
| ---- | ----------------- | ------------ |
| 开启 | start transaction | muitl        |
| 语句 | 普通sql           | 普通命令     |
| 失败 | rollback 回滚     | discard 取消 |
| 成功 | commit            | exec         |

### CAS示例

```bash
127.0.0.1:6379> SET name ling
OK
127.0.0.1:6379> SET age 20
OK
127.0.0.1:6379> WATCH name
OK
127.0.0.1:6379> SET name chen
OK
127.0.0.1:6379> MULTI
OK
127.0.0.1:6379> SET age 30
QUEUED
127.0.0.1:6379> EXEC
(nil)
127.0.0.1:6379> get name
"chen"
127.0.0.1:6379> get age
"20"

```

注: rollback与discard 的区别

如果已经成功执行了2条语句, 第3条语句出错.

Rollback后,前2条的语句影响消失.

Discard只是结束本次事务,前2条语句造成的影响仍然还在

注:

在mutil后面的语句中, 语句出错可能有2种情况

1: 语法就有问题, 

这种,exec时,报错, 所有语句得不到执行

2: 语法本身没错,但适用对象有问题. 比如 zadd 操作list对象

Exec之后,会执行正确的语句,并跳过有不适当的语句.



## 消息发布与订阅

订阅端: Subscribe 频道名称

发布端: publish 频道名称 发布内容

发布端示例

```bash
127.0.0.1:6379> PUBLISH news1 hello
(integer) 2
127.0.0.1:6379> PUBLISH news2 halo
(integer) 2
127.0.0.1:6379> PUBLISH news3 nihao
(integer) 1

```

订阅端1示例

```bash
127.0.0.1:6379> SUBSCRIBE news1 news2
Reading messages... (press Ctrl-C to quit)
1) "subscribe"
2) "news1"
3) (integer) 1
1) "subscribe"
2) "news2"
3) (integer) 2
1) "message"
2) "news1"
3) "hello"
1) "message"
2) "news2"
3) "halo"
```

订阅端2示例

```bash
127.0.0.1:6379> PSUBSCRIBE news*
Reading messages... (press Ctrl-C to quit)
1) "psubscribe"
2) "news*"
3) (integer) 1
1) "pmessage"
2) "news*"
3) "news1"
4) "hello"
1) "pmessage"
2) "news*"
3) "news2"
4) "halo"
1) "pmessage"
2) "news*"
3) "news3"
4) "nihao"
```



## 持久化

| 方案    | 描述                                                         | 优点                   | 缺点                                         |
| ------- | ------------------------------------------------------------ | ---------------------- | -------------------------------------------- |
| **rdb** | 在配置文件中定义了rdb备份的触发条件，条件达到就开始备份redis内存快照。 | 恢复数据很快，磁盘io少 | 没有达到触发条件期间发生故障，未备份数据丢失 |
| **aof** | 将每次操作都记录到一个日志中，通过日志恢复数据。             | 丢失数据风险小         | 还原数据速度慢，磁盘io频繁。                 |

**问: 在dump rdb过程中,aof如果停止同步,会不会丢失?**

答: 不会,所有的操作缓存在内存的队列里, dump完成后,统一操作.

**问: aof重写是指什么?**

答: aof重写是指把内存中的数据,逆化成命令,写入到.aof日志里.

以解决 aof日志过大的问题.

**问: 如果rdb文件,和aof文件都存在,优先用谁来恢复数据?**

答: aof

**问: 2种是否可以同时用?**

答: 可以,而且推荐这么做

**问: 恢复时rdb和aof哪个恢复的快**

答: rdb快,因为其是数据的内存映射,直接载入到内存,而aof是命令,需要逐条执行





## redis.conf配置文件详解

```bash
#  基本配置
daemonize no #是否以后台进程启动
databases 16 #创建database的数量(默认选中的是database 0)


save 900 1    #刷新快照到硬盘中，必须满足两者要求才会触发，即900秒之后至少1个关键字发生变化。
save 300 10  #必须是300秒之后至少10个关键字发生变化。
save 60 10000 #必须是60秒之后至少10000个关键字发生变化。
stop-writes-on-bgsave-error yes    #后台存储错误停止写。
rdbcompression yes    #使用LZF压缩rdb文件。
rdbchecksum yes    #存储和加载rdb文件时校验。
dbfilename dump.rdb    #设置rdb文件名。
dir ./    #设置工作目录，rdb文件会写入该目录。


# 主从配置
slaveof <masterip> <masterport> #设为某台机器的从服务器
masterauth <master-password>   #连接主服务器的密码
slave-serve-stale-data yes  # 当主从断开或正在复制中,从服务器是否应答
slave-read-only yes #从服务器只读
repl-ping-slave-period 10 #从ping主的时间间隔,秒为单位
repl-timeout 60 #主从超时时间(超时认为断线了),要比period大
slave-priority 100    #如果master不能再正常工作，那么会在多个slave中，选择优先值最小的一个slave提升为master，优先值为0表示不能提升为master。

repl-disable-tcp-nodelay no #主端是否合并数据,大块发送给slave
slave-priority 100 从服务器的优先级,当主服挂了,会自动挑slave priority最小的为主服


# 安全
requirepass foobared # 需要密码
rename-command CONFIG b840fc02d524045429941cc15f59e41cb7be6c52 #如果公共环境,可以重命名部分敏感命令 如config



# 限制
maxclients 10000 #最大连接数
maxmemory <bytes> #最大使用内存

maxmemory-policy volatile-lru #内存到极限后的处理
volatile-lru -> LRU算法删除过期key
allkeys-lru -> LRU算法删除key(不区分过不过期)
volatile-random -> 随机删除过期key
allkeys-random -> 随机删除key(不区分过不过期)
volatile-ttl -> 删除快过期的key
noeviction -> 不删除,返回错误信息

# 解释 LRU ttl都是近似算法,可以选N个,再比较最适宜T踢出的数据
maxmemory-samples 3

# aof日志模式
appendonly no #是否仅要日志
appendfsync no # 系统缓冲,统一写,速度快
appendfsync always # 系统不缓冲,直接写,慢,丢失数据少
appendfsync everysec #折衷,每秒写1次

no-appendfsync-on-rewrite no #为yes,则其他线程的数据放内存里,合并写入(速度快,容易丢失的多)
auto-AOF-rewrite-percentage 100 #当前aof文件是上次重写是大N%时重写
auto-AOF-rewrite-min-size 64mb #aof重写至少要达到的大小

# 慢查询
slowlog-log-slower-than 10000 #记录响应时间大于10000微秒的慢查询
slowlog-max-len 128   # 最多记录128条


# 服务端命令
time  返回时间戳+微秒
dbsize 返回key的数量
bgrewriteaof 重写aof
bgsave 后台开启子进程dump数据
save 阻塞进程dump数据
lastsave 

slaveof host port 做host port的从服务器(数据清空,复制新主内容)
slaveof no one 变成主服务器(原数据不丢失,一般用于主服失败后)

flushdb  清空当前数据库的所有数据
flushall 清空所有数据库的所有数据(误用了怎么办?)

shutdown [save/nosave] 关闭服务器,保存数据,修改AOF(如果设置)

slowlog get 获取慢查询日志
slowlog len 获取慢查询日志条数
slowlog reset 清空慢查询


info []

config get 选项(支持*通配)
config set 选项 值
config rewrite 把值写到配置文件
config restart 更新info命令的信息

debug object key #调试选项,看一个key的情况
debug segfault #模拟段错误,让服务器崩溃
object key (refcount|encoding|idletime)
monitor #打开控制台,观察命令(调试用)
client list #列出所有连接
client kill #杀死某个连接  CLIENT KILL 127.0.0.1:43501
client getname #获取连接的名称 默认nil
client setname "名称" #设置连接名称,便于调试


```



## 删除bigkey

下面操作可以使用pipeline加速。redis 4.0已经支持key的异步删除

1、Hash删除: hscan + hdel

```java
public void delBigHash(String host, int port, String password, String bigHashKey) {
 Jedis jedis = new Jedis(host, port);
 if (password != null && !"".equals(password)) {
        jedis.auth(password);
    }
 ScanParams scanParams = new ScanParams().count(100);
 String cursor = "0";
 do {
 ScanResult<Entry<String, String>> scanResult = jedis.hscan(bigHashKey, cursor, scanParams);
 List<Entry<String, String>> entryList = scanResult.getResult();
 if (entryList != null && !entryList.isEmpty()) {
 for (Entry<String, String> entry : entryList) {
                jedis.hdel(bigHashKey, entry.getKey());
            }
        }
        cursor = scanResult.getStringCursor();
    } while (!"0".equals(cursor));

 //删除bigkey
    jedis.del(bigHashKey);
}
```

2、List删除: ltrim

```java
public void delBigList(String host, int port, String password, String bigListKey) {
 Jedis jedis = new Jedis(host, port);
 if (password != null && !"".equals(password)) {
        jedis.auth(password);
    }
 long llen = jedis.llen(bigListKey);
 int counter = 0;
 int left = 100;
 while (counter < llen) {
 //每次从左侧截掉100个
        jedis.ltrim(bigListKey, left, llen);
        counter += left;
    }
 //最终删除key
    jedis.del(bigListKey);
}
```

3、Set删除: sscan + srem

```java
public void delBigSet(String host, int port, String password, String bigSetKey) {
 Jedis jedis = new Jedis(host, port);
 if (password != null && !"".equals(password)) {
        jedis.auth(password);
    }
 ScanParams scanParams = new ScanParams().count(100);
 String cursor = "0";
 do {
 ScanResult<String> scanResult = jedis.sscan(bigSetKey, cursor, scanParams);
 List<String> memberList = scanResult.getResult();
 if (memberList != null && !memberList.isEmpty()) {
 for (String member : memberList) {
                jedis.srem(bigSetKey, member);
            }
        }
        cursor = scanResult.getStringCursor();
    } while (!"0".equals(cursor));

 //删除bigkey
    jedis.del(bigSetKey);
}
```

4、SortedSet删除: zscan + zrem

```java
public void delBigZset(String host, int port, String password, String bigZsetKey) { 
 Jedis jedis = new Jedis(host, port);
 if (password != null && !"".equals(password)) {
        jedis.auth(password);
    }
 ScanParams scanParams = new ScanParams().count(100);
 String cursor = "0";
 do {
 ScanResult<Tuple> scanResult = jedis.zscan(bigZsetKey, cursor, scanParams);
 List<Tuple> tupleList = scanResult.getResult();
 if (tupleList != null && !tupleList.isEmpty()) {
 for (Tuple tuple : tupleList) {
                jedis.zrem(bigZsetKey, tuple.getElement());
            }
        }
        cursor = scanResult.getStringCursor();
    } while (!"0".equals(cursor));

 //删除bigkey
    jedis.del(bigZsetKey);
}
```

