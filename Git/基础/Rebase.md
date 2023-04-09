# Git Rebase 详解

本文主要讲解下 Git Rebase 的基本概念用法、其内部原理以及我们在真实项目中使用 Git Rebase 应该遵循的原则以及为啥需要遵循这些原则。

# Rebase 基础

相信对于 rebase 肯定不会陌生，就好像上图描述的过程一样，当你使用 rebase 命令的时候，即好像将你需要去 rebase 的分支拔下来然后重新插到另一个分支上。官方对于 rebase 的描述为：

```
“git-rebase: Forward-port local commits to the updated upstream head”— git doc
```

翻译一下，就是讲你在某个分支上的所有提交记录移花接木到另一个分支上。这边需要强调一个概念：reapply，使用 rebase 并不是简单地好像你用 ctrl-x/ctrl-v 进行剪切复制一样，rebase 会依次地将你所要操作的分支的所有提交应用到目标分支上。也就是说，实际上在执行 rebase 的时候，有两个隐含的注意点：

- 在重放之前的提交的时候，Git 会创建新的提交，也就是说即使你重放的提交与之前的一模一样 Git 也会将之当做新的独立的提交进行处理。

- Git rebase 并不会删除老的提交，也就是说你在对某个分支执行了 rebase 操作之后，老的提交仍然会存放在.git 文件夹的 objects 目录下。如果你对于 Git 是如何存放你的提交不太了解的话可以参考这篇文章：[Understanding git for real by exploring the .git directory](https://medium.freecodecamp.com/understanding-git-for-real-by-exploring-the-git-directory-1e079c15b807#.6ylqa5e2w)

基于以上表述，我们可以得出以下相对更准确的 Git rebase 的工作流程：

从上图可以看出，在对特征分支进行 rebase 之后，其等效于创建了新的提交。并且老的提交也没有被销毁，只是简单地不能再被访问或者使用。在对于分支的章节我们曾经提及，一个分支只是一个执行提交的指针。因此如果既没有分支或者 Tag 指向某个提交，该提交将无法再被访问使用，但是该提交会一直存在于你的文件系统中，占用着你的磁盘存储。

# 使用 Rebase 修改本地历史

顾名思义，Rebase(变基)有移花接木之效果，能将特性分支移接到主分支之上，常用于优化提交历史，或者修改本地的提交信息。首先查看本地的 Commit 历史：

```sh
$ git log --pretty=oneline

a931ac7c808e2471b22b5bd20f0cad046b1c5d0d c
b76d157d507e819d7511132bdb5a80dd421d854f b
df239176e1a2ffac927d8b496ea00d5488481db5 a
```

运行 Git Rebase:

```sh
# 使用 Git Rebase，对最后两个提交进行操作
git rebase --interactive HEAD~2
```

```s
pick b76d157 b
squash a931ac7 c //change pick to squash

# Rebase df23917..a931ac7 onto df23917
#
# Commands:
#  p, pick = use commit
#  r, reword = use commit, but edit the commit message
#  e, edit = use commit, but stop for amending
#  s, squash = use commit, but meld into previous commit
#  f, fixup = like "squash", but discard this commit's log message
#
# If you remove a line here THAT COMMIT WILL BE LOST.
# However, if you remove everything, the rebase will be aborted.
#
```

然后保存并且关闭编辑器：

```s
# This is a combination of 2 commits.
# The first commit's message is:

b

# This is the 2nd commit message:

c
```

这样的话就已经完成了合并

```sh
$ git log --pretty=oneline
18fd73d3ce748f2a58d1b566c03dd9dafe0b6b4f b and c
df239176e1a2ffac927d8b496ea00d5488481db5 a
```

# Golden Rule of Rebase

> “No one shall rebase a shared branch” — Everyone about rebase

估计你也肯定看过这个原则，不过可能表述不一样罢了。本章节就是用实例的角度来探讨下，为啥不能再一个共享的分支上进行 Git rebase 操作。所谓共享的分支，即是指那些存在于远端并且允许团队中的其他人进行 Pull 操作的分支。假设现在 Bob 和 Anna 在同一个项目组中工作，项目所属的仓库和分支大概是下图这样：

现在 Bob 为了图一时方便打破了原则，正巧这时 Anna 在特征分支上进行了新的提交，此时的结构图大概是这样的：

当 Bob 打算推送自己的分支到远端的时候，它收到了如下的警告：

Git 尝试着使用 fast-forward 来合并你的分支，具体的细节我们会在其他博客中进行讨论，这边只需要明白远端的 Git Server 被 Bob 搞得一头雾水，不知道应该如何去合并。此时 Bob 为了推送他的本地的提交，只能选择强行合并，即告诉远端：不要再尝试着合并我推送给你的和你已经有点提交，一切按照我推送过去的来。那么 Git 会进行如下操作：

然后呢，当 Anna 也进行推送的时候，她会得到如下的提醒：

这个消息很正常，没啥特殊的，只是 Git 提醒 Anna 她本地的版本与远程分支并不一致，在 Anna 提交之前，分支中的 Commit 序列是如下这样的：

```
A--B--C--D'   origin/feature // GitHub
A--B--D--E    feature        // Anna
```

在进行 Pull 操作之后，Git 会进行自动地合并操作，结果大概是这样的：

这个第 M 个提交即代表着合并的提交，也就是 Anna 本地的分支与 Github 上的特征分支最终合并的点，现在 Anna 解决了所有的合并冲突并且可以 Push 她的代码，在 Bob 进行 Pull 之后，每个人的 Git Commit 结构为：

到这里，看到上面这个混乱的流线图，相信你对于 Rebase 和所谓的黄金准则也有了更形象深入的理解。这还只是仅有两个人，一个特征分支的项目因为误用 rebase 产生的后果。如果你团队中的每个人都对公共分支进行 rebase 操作，那还不得一团乱麻。另外，相信你也注意到，在远端的仓库中存有大量的重复的 Commit 信息，这会大大浪费我们的存储空间。如果你还觉得这么什么，那我们来假设下还有一哥们 Emma，第三个开发人员，在他进行了本地 Commit 并且 Push 到远端之后，仓库变为了：
