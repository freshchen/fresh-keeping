---
begin: 2021-11-16
status: done
rating: 1
---

# Python 命令行参数解析工具 argparse

## 为什么需要argparse

开门见山，举一个简易计算器代码的例子，其中sys.argv用来读取脚本执行时后面传入的参数。

```python
def calculator(x, y, operation):
    if "add" == operation:
        return x + y
    elif "mod" == operation:
        return x % y
    elif "sub" == operation:
        return x - y
    elif "div" == operation:
        return x / y
    elif "mul" == operation:
        return x * y


def main():
    print(calculator(sys.argv[1], sys.argv[2], sys.argv[3]))


if __name__ == '__main__':
    main()
```

我们定义了一个calculator方法来完成一些简单的计算工作，这看来相当平凡，但对于用户来说，在没有良好的文档支持的前提下，传入不同参数有不同的行为，如果只有少量参数还可以接受，随着参数的增加，方法会变得越来越不易使用。这时候便需要参数解析，argparse模块便官方推荐的在optparse基础上更进一步的改良版标准库。让我们在了解一下她的庐山真面目之前先通过一个例子来了解argparse相关特性。

相信小伙伴们都或多或少用过Linux系统，让我们通过下面这个例子直观的了解argparse能做什么。

```bash
# 只输入ls，默认显示当前目录下内容
[root@host workarea]# ls
pythondemo  scripts
# 当我们给ls命令加一个参数，便会去找这个参数对应目录下的内容
[root@host workarea]# ls pythondemo/
arg1.py  argparsedemo1.py  fangzhen.py  numpyapi.py  tools
# 我们也可以使用ls -[cmd]来改变行为，获得更详细的信息
[root@host workarea]# ls -l
total 8
drwxr-xr-x 3 root root 4096 Dec 14 14:05 pythondemo
drwxr-xr-x 2 root root 4096 Dec 14 14:29 scripts
# 如果我们想知道ls命令的其他用法和相关信息可以使用ls --help
[root@host workarea]# ls --help
Usage: ls [OPTION]... [FILE]...
List information about the FILEs (the current directory by default).
Sort entries alphabetically if none of -cftuvSUX nor --sort is specified.

Mandatory arguments to long options are mandatory for short options too.
  -a, --all                  do not ignore entries starting with .
  -A, --almost-all           do not list implied . and ..
...
```

脚本传入后的参数显示的绑定，让用户更清楚自己在执行什么操作，并且给用户一些提示信息在他忘记如何使用我们的脚本时，这便是我们要做的，当然如果参数数量不多，或者行为不复杂可以不使用。

## argparse初体验

```python
#总体使用流程如下
import argparse
# 模板创建一个解析参数对象
parser = argparse.ArgumentParser()
# 用来指定程序需要接受的命令参数
parser.add_argument()
# 通过分析指定的参数返回一些数据
args = parser.parse_args()
```

我们直接试着用argparse对上面的例子进行改造，直观感受下区别

```python
def calculator(args):
    operation = args.operation
    x = args.x
    y = args.y
    if "add" == operation:
        return x + y
    elif "mod" == operation:
        return x % y
    elif "sub" == operation:
        return x - y
    elif "div" == operation:
        return x / y
    elif "mul" == operation:
        return x * y


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--x", type=float, default=1.0, help="What is the first number")
    parser.add_argument("--y", type=float, default=1.0, help="What is the second number")
    parser.add_argument("--operation", type=str, help="What operation? [add,mod,sub,div,mul]")
    args = parser.parse_args()
    print(args)
    print(calculator(args))


if __name__ == '__main__':
    main()
```

经过简单的改造，调用calculator方法时，我们可以更清楚自己在做什么操作，并且可以根据帮助信息使用而不需要去阅读源码了，这真的节省了很多不必要的时间。

```bash
# 直接调用不加参数，会提示None，命名空间那行为print(args)
[root@host pythondemo]# python arg2.py 
Namespace(operation=None, x=1.0, y=1.0)
None
# 加上参数-h 或 --help我们就能看到帮助信息，感觉像模像样
[root@host pythondemo]# python arg2.py -h
usage: arg2.py [-h] [--x X] [--y Y] [--operation OPERATION]

optional arguments:
  -h, --help            show this help message and exit
  --x X                 What is the first number
  --y Y                 What is the second number
  --operation OPERATION
                        What operation? [add,mod,sub,div,mul]
# 参数传入方法，这里的等于号可以省略
[root@host pythondemo]# python arg2.py --x=2 --y=3 --operation=mul
6.0
# 当我们少输入或者没有输入要求的参数
[root@host pythondemo]# python arg2.py 2
usage: arg2.py [-h] [--x X] [--y Y] [--operation OPERATION]
arg2.py: error: unrecognized arguments: 2
```

当然这并没有完成我们的全部需求，我们还可以做的更好，那么我们就得深入了解下今天得主角argparse。

## argparse详解

### 模板创建argparse.ArgumentParser()

我们可以给解析模板来个简单得标题，会在--help中显示

```python
argparse.ArgumentParser(description='sample demo')
```

如果我们不想用Linux风格的"-"或"--"来作为命令前缀，我们自定义如下

```python
parser = argparse.ArgumentParser(prefix_chars='-+/')
```

模板创建时是默认添加-h和--help帮助信息的，如果我们不想要，写可以去除掉

```python
argparse.ArgumentParser(add_help=False)
```

我们也可以添加版本信息，会自动加入获取版本信息的-v和--version命令

```python
argparse.ArgumentParser(version='1.0')
```

### 重头戏parser.add_argument()

直接在方法里加一个字符串"-a"或"--a"当在脚本后面调用传入值后，都可以使用arg.a获取传入值，传入值默认从sys.argv【1:】中获得

```python
parser.add_argument("-a")
args = parser.parse_args()
print(args)
print(args.a)

# 结果
λ python exam2.py --a=1
Namespace(a='1')
1
```

如果我们想改变访问方式可以用dest参数

```python
parser.add_argument("--a", dest="c")
args = parser.parse_args()
print(args)
print(args.c)

# 结果
λ python exam2.py --a=1    

Namespace(c='1')           
1
```

我们也可以不用变量显示赋值的方式,不加“-”或“--”,这样传入值和参数定义的顺序一样，这种情况desk不奏效

```python
parser.add_argument("a")
parser.add_argument("b")
args = parser.parse_args()
print(args)

# 结果
λ python exam2.py 1 2
Namespace(a='1', b='2')
```

当然我们也可以给传入两种接受方式，这种情况"-"开头的为命令简写，获取传入参数用"--"后的属性

```python
parser.add_argument('-n', '--name', help="What's ur name")
args = parser.parse_args()
print(args)
print(args.name)

# 结果
λ python exam3.py -n=ling
Namespace(name='ling')
ling
λ python exam3.py --name=wang
Namespace(name='wang')
wang
```

默认我们传入的值会当作字符串来处理，如果我们需要指定类型可以使用type参数，如上面计算器中定义所示，如果不指定type为int，便会报错。默认支持的type类型有 int，float，open

```python
parser.add_argument(type="")
```

如果确定动作需要传入参数的个数，我们也可以加nargs做强制限制。["n":参数的绝对个数，"？"：0或1个,"*":0或所以,"+":所有并且至少一个]

```python
parser.add_argument(nargs="")
```

### 参数动作

argparse内置6种动作可以在解析到一个参数时进行触发：

`store`保存参数值，可能会先将参数值转换成另一个数据类型。若没有显式指定动作，则默认为该动作。

`store_const`保存的常量，如果触发此动作，值是参数规格中提前被定义的值，而不能命令行传入值。

`store_ture`/`store_false`保存相应的布尔值。这两个动作被用于实现布尔开关。

`append`将值保存到一个列表中。若参数重复出现，则保存多个值。

`append_const`将一个定义在参数规格中的值保存到一个列表中。

`version`打印关于程序的版本信息，然后退出

用法如下所示：

```python
parser.add_argument('-s', action='store', dest='simple_value', help='Store a simple value')
parser.add_argument('-c', action='store_const', dest='constant_value',
        const='value-to-store',
        help='Store a constant value')

parser.add_argument('-t', action='store_true', default=False,
        dest='boolean_switch',
        help='Set a switch to true')
parser.add_argument('-f', action='store_false', default=False,
        dest='boolean_switch',
        help='Set a switch to false')

parser.add_argument('-a', action='append', dest='collection',
        default=[],
        help='Add repeated values to a list')

parser.add_argument('-A', action='append_const', dest='const_collection',
        const='value-1-to-append',
        default=[],
        help='Add different values to list')
parser.add_argument('-B', action='append_const', dest='const_collection',
        const='value-2-to-append',
        help='Add different values to list')

parser.add_argument('--version', action='version', version='%(prog)s 1.0')
results = parser.parse_args()
print 'simple_value     =', results.simple_value
print 'constant_value   =', results.constant_value
print 'boolean_switch   =', results.boolean_switch
print 'collection       =', results.collection
print 'const_collection =', results.const_collection

# 结果
λ python argparse_action.py -s value

simple_value     = value
constant_value   = None
boolean_switch   = False
collection       = []
const_collection = []

λ python argparse_action.py -c

simple_value     = None
constant_value   = value-to-store
boolean_switch   = False
collection       = []
const_collection = []

λ python argparse_action.py -t

simple_value     = None
constant_value   = None
boolean_switch   = True
collection       = []
const_collection = []

λ python argparse_action.py -f

simple_value     = None
constant_value   = None
boolean_switch   = False
collection       = []
const_collection = []

λ python argparse_action.py -a one -a two -a three

simple_value     = None
constant_value   = None
boolean_switch   = False
collection       = ['one', 'two', 'three']
const_collection = []

λ python argparse_action.py -B -A

simple_value     = None
constant_value   = None
boolean_switch   = False
collection       = []
const_collection = ['value-2-to-append', 'value-1-to-append']
λ python argparse_action.py --version
argparse_action.py 1.0
```

### 解析器组

我们经常会遇到很多解析器都会需要相同的参数，例如都需要输入用户名密码，这样我们可以定义一个父解析器定义common的规则，子解析器可以集成，并且扩展。如果定义了相同的参数便会产生冲突。argparse有两个内置的冲突处理器`error`（默认）和`resolve`，`resolve`会基于冲突选项的添加顺序来选择一个参数处理器，后添加覆盖老规则。我们应该尽量避免冲突，所以父解析器一般不定义帮助信息。下面举个简单的例子。

首先定义父解析器如下：

```python
parser = argparse.ArgumentParser(description='parent 2', add_help=False)
parser.add_argument('-p', '--password', help='What is your passwrd')
parser.add_argument('-user', '--username', help='What is your username')
parser.add_argument('-m', '--female', help='What is your female')
args = parser.parse_args()
print(args)

# 结果
λ python arg_parent.py -h
usage: arg_parent.py [-p PASSWORD] [-user USERNAME] [-m FEMALE]
arg_parent.py: error: unrecognized arguments: -h

λ python arg_parent.py -p 123 -user ling -m man
Namespace(female='man', password='123', username='ling')
```

子解析器：集成并且重写

```python
import arg_parent

parser = argparse.ArgumentParser(description='son 1', parents=[arg_parent.parser], conflict_handler='resolve')
parser.add_argument('-w', '--weather', help="What's the weather")
parser.add_argument('-m', '--female', action='store_const', const='TRUE', help='What is your female')

args = parser.parse_args()
print(args)

# 结果
λ python arg_son.py -m -w cold -p 123
Namespace(female='TRUE', password='123', username=None, weather='cold')

λ python arg_son.py -m man
usage: arg_son.py [-h] [-p PASSWORD] [-user USERNAME] [-w WEATHER] [-m]
arg_son.py: error: unrecognized arguments: man
```

目前只用到了以上特性，还有一些特性就不一一介绍了，后续用到再补充。

## 参考链接


##### 标签
#tools #python