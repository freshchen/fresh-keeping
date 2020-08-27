
# Git常用命令总结

```
//时刻掌握仓库当前的状态
git status
```

```
//看看具体修改了什么内容
git diff
```

```
//将更改放入暂存区
git add .
```

```
//查看历史版本记录
git log
//查看所有提交过的版本，如果不小心回退可以再回去
git reflog
```

```
//回到上个版本
git reset --hard HEAD^
//回到指定版本
git reset --hard versionNum
//回到指定版本但是不改变修改
git reset --mixed versionNum
```

```
/**丢弃工作区中的修改
命令git checkout -- readme.txt意思就是，把readme.txt⽂文件在⼯工作区的修改全部撤销，这
⾥里有两种情况：
⼀一种是readme.txt⾃自修改后还没有被放到暂存区，现在，撤销修改就回到和版本库⼀一模⼀一
样的状态；
⼀一种是readme.txt已经添加到暂存区后，⼜又作了修改，现在，撤销修改就回到添加到暂存
区后的状态.
场景1：当你改乱了⼯工作区某个⽂文件的内容，想直接丢弃⼯工作区的修改时，⽤用命令git
checkout -- file。
场景2：当你不但改乱了⼯工作区某个⽂文件的内容，还添加到了暂存区时，想丢弃修改，分两
步，第⼀一步⽤用命令git reset HEAD file，就回到了场景1，第⼆二步按场景1操作。
*/
git checkout -- file
```

```
//删除文件
git rm file
```

```
//把本地库内容推入远程库
git push -u origin master
```

```
//创建并且切换分支
git checkout -b branchName
//相当于下面两条命令
git branch branchName
git checkout branchName
//合并到主分支
git merge branchName
//删除分支
git branch -d branchName
//查看分支合并图
git log --graph
```

```
//储存当前工作现场
git stash
//查看刚才的工作现场
git stash list
//恢复
git stash apply
//删除
git stash drop
//恢复并且删除
git stash pop
```

```
//查看远程仓库
git remote
//在本地创建和远程分⽀支对应的分⽀支：
git checkout -b branch-name origin/branchname
//建⽴立本地分⽀支和远程分⽀支的关联
git branch --set-upstream branch-name origin/branch-name
//抓取最近的版本
git pull
```

```
//创建标签
git tag v1.0
//查看标签
git tag
//查看标签信息
git show v0.9
```

官方文档笔记

```
1.查看所有git配置
git config --list
2.检查某一项配置
git config <key>
3.获取帮助（不借助互联网查看相关指令）有三种方式
$ git help <verb>
$ git <verb> --help
$ man git-<verb>
4.忽略文件不接受GIT管理
$ cat .gitignore
*.[oa]
*~
5.跳过暂存区提交
$ git commit -a -m 'added new benchmarks'
6.删除所有～结尾的
$ git rm \*~
```