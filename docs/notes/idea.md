## IDEA Usage

### 常用插件

- Lombok
- GsonFormat
- Maven Helper
- GenerateAllSetter
- Rainbow Brackets
  - D3D3D3
  - 7577A8
  - A89248
  - 5C98BB
  - 5B9392
- CodeGlance
- BashSupport
  - 和默认shell script插件冲突，要disable一下
- Grep Console
- Nyan Progress Bar
- Kubernetes



### 右侧Maven标签栏的Dependences报红

检查没有冲突，并且包导入成功，把pom中的对应的报红依赖注释，import一遍，然后放开注释再import一遍



### 复制代码到IDEA有行号

CTRL+R  ^[ 0-9] relace all狂点直到没有红
CTRL+R  ^[^\s*\n] relace all
CTRL+ALT+L 规范代码



### 怎样取消默认打开工程 

settings 里搜索 reopen



### 作者姓名

VM 配置（改内存大小的文件） 里面加上

-Duser.name 作者名字