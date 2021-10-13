---
begin: 2021-10-13
status: ongoing
rating: 1
---

# PlantUML使用

[官网](https://plantuml.com/)

## 时序图

```plantuml
@startuml
participant participant as Foo
actor       actor       as Foo1
boundary    boundary    as Foo2
control     control     as Foo3
entity      entity      as Foo4
database    database    as Foo5
collections collections as Foo6
queue       queue       as Foo7
Foo -> Foo1 : To actor 
Foo -> Foo2 : To boundary
Foo -> Foo3 : To control
Foo -> Foo4 : To entity
Foo -> Foo5 : To database
Foo -> Foo6 : To collections
Foo -> Foo7 : To queue
@enduml


```


```plantuml
@startuml
Alice -> Bob: Authentication Request
Bob --> Alice: Authentication Response

Alice -> Bob: Another authentication Request
Alice <-- Bob: Another authentication Response
@enduml

```



```plantuml
@startuml
Alice->Alice: This is a signal to self.\nIt also demonstrates\nmultiline \ntext
@enduml
```


```plantuml
@startuml
Bob ->x Alice
Bob -> Alice
Bob ->> Alice
Bob -\ Alice
Bob \\- Alice
Bob //-- Alice

Bob ->o Alice
Bob o\\-- Alice

Bob <-> Alice
Bob <->o Alice
@enduml

```


```plantuml
@startuml
autonumber
Bob -> Alice : Authentication Request
Bob <- Alice : Authentication Response

autonumber 15
Bob -> Alice : Another authentication Request
Bob <- Alice : Another authentication Response

autonumber 40 10
Bob -> Alice : Yet another authentication Request
Bob <- Alice : Yet another authentication Response

@enduml

```

## 用例图

```plantuml

@startuml

:First Actor:
:Another\nactor: as Man2
actor Woman3
actor :Last actor: as Person1

@enduml

@startuml
:User: --> (Use)
"Main Admin" as Admin
"Use the application" as (Use)
Admin --> (Admin the application)
@enduml

@startuml
left to right direction
actor Guest as g
package Professional {
  actor Chef as c
  actor "Food Critic" as fc
}
package Restaurant {
  usecase "Eat Food" as UC1
  usecase "Pay for Food" as UC2
  usecase "Drink" as UC3
  usecase "Review" as UC4
}
fc --> UC4
g --> UC1
g --> UC2
g --> UC3
@enduml

@startuml
left to right direction
actor "Food Critic" as fc
rectangle Restaurant {
  usecase "Eat Food" as UC1
  usecase "Pay for Food" as UC2
  usecase "Drink" as UC3
}
fc --> UC1
fc --> UC2
fc --> UC3
@enduml

@startuml
left to right direction
skinparam packageStyle rectangle
actor customer
actor clerk
rectangle checkout {
  customer -- (checkout)
  (checkout) .> (payment) : include
  (help) .> (checkout) : extends
  (checkout) -- clerk
}
@enduml



```


## 类图

![](image/Pasted%20image%2020211014003732.png)

![](image/Pasted%20image%2020211014003914.png)

```plantuml

@startuml
abstract        抽象
abstract class  抽象(等同abstract)
annotation      注解
circle          圆
()              圆缩写形式
class           类
diamond         菱形
<>              菱形写形式
entity          实例
enum            枚举
interface       接口
@enduml

```

```plantuml
@startuml
Class01 <|-- Class02
Class03 *-- Class04
Class05 o-- Class06
Class07 .. Class08
Class09 -- Class10
@enduml
```

```plantuml
@startuml
Class11 <|.. Class12
Class13 --> Class14
Class15 ..> Class16
Class17 ..|> Class18
Class19 <--* Class20
@enduml

```

```plantuml
@startuml

类01 "1" *-- "many" 类02 : 包含

类03 o-- 类04 : 聚合

类05 --> "1" 类06

@enduml

```

```plantuml
@startuml
class 汽车

发动机 - 汽车 : 驱动 >
汽车 *- 轮子 : 拥有 4 >
汽车 -- 人 : < 所属

@enduml

```

```plantuml
@startuml
Object <|-- ArrayList

Object : equals()
ArrayList : Object[] elementData
ArrayList : size()

@enduml

```


```plantuml
@startuml

class Dummy {
 -field1
 #field2
 ~method1()
 +method2()
}

@enduml
```

```plantuml
@startuml

class Foo
note left: On last defined class

note top of Object
  In java, <size:18>every</size> <u>class</u>
  <b>extends</b>
  <i>this</i> one.
end note

note as N1
  This note is <u>also</u>
  <b><color:royalBlue>on several</color>
  <s>words</s> lines
  And this is hosted by <img:sourceforge.jpg>
end note

@enduml

```

```plantuml
@startuml
class A {
{static} int counter
+void {abstract} start(int timeout)
}
note left of A::counter
  该成员已注释
end note
note right of A::start
  在 UML 注释了此方法
end note
@enduml
```

```plantuml
@startuml

abstract class AbstractList
abstract AbstractCollection
interface List
interface Collection

List <|-- AbstractList
Collection <|-- AbstractCollection

Collection <|- List
AbstractCollection <|- AbstractList
AbstractList <|-- ArrayList

class ArrayList {
  Object[] elementData
  size()
}

enum TimeUnit {
  DAYS
  HOURS
  MINUTES
}

annotation SuppressWarnings

@enduml
```

```plantuml
@startuml

class Foo<? extends Element> {
  int size()
}
Foo *- Element

@enduml

@startuml
class Student {
  Name
}
Student "0..*" - "1..*" Course
(Student, Course) .. Enrollment

class Enrollment {
  drop()
  cancel()
}
@enduml

```

## 活动图

```plantuml
@startuml
start
:Hello world;
:This is on defined on
several **lines**;
stop
@enduml

```


```plantuml
@startuml

start

if (Graphviz 已安装?) then (yes)
  :处理所有\n绘制任务;
else (no)
  :仅处理
  __时序图__ 和 __活动__ 图;
endif

stop

@enduml

```


```plantuml
@startuml
if (计数器?) equals (5) then
:打印 5;
else 
:打印非 5;
@enduml

```


```plantuml
@startuml
start
if (条件 A) then (yes)
  :文本 1;
elseif (条件 B) then (yes)
  :文本 2;
  stop
elseif (条件 C) then (yes)
  :文本 3;
elseif (条件 D) then (yes)
  :文本 4;
else (nothing)
  :文本 else;
endif
stop
@enduml

```


```plantuml
@startuml
!pragma useVerticalIf on
start
if (条件 A) then (yes)
  :文本 1;
elseif (条件 B) then (yes)
  :文本 2;
  stop
elseif (条件 C) then (yes)
  :文本 3;
elseif (条件 D) then (yes)
  :文本 4;
else (nothing)
  :文本 else;
endif
stop
@enduml

```


```plantuml
@startuml
start
switch (测试?)
case ( 条件 A )
  :Text 1;
case ( 条件 B ) 
  :Text 2;
case ( 条件 C )
  :Text 3;
case ( 条件 D )
  :Text 4;
case ( 条件 E )
  :Text 5;
endswitch
stop
@enduml

```


```plantuml
@startuml
if (条件?) then
  :错误;
  stop
endif
#palegreen:行为;
@enduml

```


```plantuml
@startuml

start

repeat :foo作为开始标注;
  :读取数据;
  :生成图片;
backward:这是一个后撤行为;
repeat while (更多数据?)

stop

@enduml

```


```plantuml

```


## 参考链接


##### 标签
#tools #design 
