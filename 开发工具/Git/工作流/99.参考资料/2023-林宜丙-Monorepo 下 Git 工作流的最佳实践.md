# Monorepo 下 Git 工作流的最佳实践

我们最熟悉的 Git 工作流莫过于 Git flow, Gitlab flow, Github flow，而对于 feature branch 和 trunk-based 比较陌生，那么以上几种 flow 有什么关系呢？

- Feature branch 和 Trunk-based 工作流是比较新晋的概念，二者是相对的、互斥的，它们组成一个全集；
- Git flow, Gitlab flow, Github flow 都属于 feature branch development，它们有一个共同点：都采用『功能驱动式开发』，即：需求是开发的起点，先有需求再有功能分支（feature branch）或者补丁分支（hotfix branch）；

![Feature branch vs Git flow](https://assets.ng-tech.icu/item/20230302200730.png)

在 Monorepo 工程中，使用 feature branch development 开发模式时，随着代码库复杂性和团队规模的增长，并行的『长期分支』也会越来越多，这些分支在合入主干时，将会频繁遇到冲突或者不兼容的情况，而手动解决代码冲突往往会引入 Bug。而 trunk-based development 鼓励开发者可以通过一些小的提交创建『短期分支』，从而大大缓解冲突问题，有助于保持生产版本的流畅。总的来说，选择一个工作流不仅仅是一系列操作工具的流程，我们往往还需要对它背后的思想买单；下面的表格是两种工作流模式在各个维度的适用情况：

![工作流模式](https://assets.ng-tech.icu/item/20230302200920.png)

目前大部分业务场景使用的都是 feature branch 的开发模式，如果你的业务是多人开发一个巨型应用（如抖音主站、飞书文档等），应该尝试使用 Trunk based 开发模式，这会提高仓库整体工程质量和管理水平。

# Feature branch development

『功能分支开发模式』的核心思想是所有特性开发都应该在专用的分支，而不是主分支中进行。这种封装使多个开发人员，可以轻松地在不干扰主代码库的情况下处理特定功能。这也意味着主分支永远不会包含损坏的代码，这对于持续集成环境来说是一个巨大的优势。-- Git Feature Branch Workflow | Atlassian Git Tutorial

![Feature branch](https://assets.ng-tech.icu/item/20230302201024.png)

1、从 master 分支创建一个功能分支（Feature Branch）
2、开发者们在功能分支中完成开发工作
3、构建功能分支，并通知 QA 进行验证
4、如果发现任何问题

- 开发者创建一个基于功能分支的修复 MR
- 经过代码审阅和合并过程将修复 MR 合入功能分支
- 再重新构建部署，并通知 QA 进行验证

5、QA 验证通过后，将功能分支发布至线上，然后将其合并入主干后删除

## 为什么使用 feature branch development？

- 多功能并行开发：使多个开发人员可以轻松地在不干扰主代码库的情况下处理特定功能。

- 保持主分支稳定：主分支永远不会包含损坏的代码，这对于持续集成环境来说是一个巨大的优势。

- 心智负担低：仅需了解简单的操作即可实践，无需了解 cherry-pick, feature flag 等概念。

# Trunk-based development

『基于主干的开发模式』是一种版本控制管理实践，开发者将小而频繁的更新合并到核心『主干』（通常是 master 分支）。这是 DevOps 团队中的一种常见做法，也是 DevOps 生命周期的一部分，因为它简化了合并和集成阶段。事实上，它也是 CI/CD 的必备实践。与其它存在『长期分支』的功能分支策略相比，开发者可以通过一些小的提交创建『短期分支』。随着代码库复杂性和团队规模的增长，『基于主干的开发模式』有助于保持生产版本的流畅。-- Trunk-based Development | Atlassian

![Trunk-based Development](https://assets.ng-tech.icu/item/20230302201509.png)

## 从部署分支上线

半自动化流程，适用于低频率部署，以及自动化测试不全面的项目：

![半自动化流程](https://assets.ng-tech.icu/item/20230302201546.png)

A dot represents an MR merged into master. Green dots means good commits that passed e2e tests, and red dot means a buggy commit which should be avoided when deploying/rollback.

1、从 master 分支创建一个部署分支（RC）
2、构建部署分支（RC），并通知 QA 进行验证
3、如果发现任何问题

- 开发者创建一个基于 master 分支的修复 MR
- 经过代码审阅和合并过程将修复 MR 合入 master
- 将 commits cherrypick 到部署分支（RC），再重新构建部署，并通知 QA 进行验证

4、QA 验证通过后，将部署分支（RC）发布至线上，然后删除该分支（RC）

## 从主干分支上线

全自动化流程，适用于需要高频率部署，以及自动化测试较为全面的项目：

![从主干分支上线](https://assets.ng-tech.icu/item/20230302201723.png)

A dot represents an MR merged into master. Green dots means good commits that passed e2e tests, and red dot means a buggy commit which should be avoided when deploying/rollback.

1、定时部署：每天或者每小时到了特定时间，部署机器人自动找到当前最新通过全部端到端测试的代码 (特定的 commit hash)，然后将之部署上线。
2、持续部署：每当有新代码合并进主干分支时，部署机器人自动验证新代码是否通过所有端到端测试，以及是否与该项目相关，如果是则自动部署上线

## 为什么使用 trunk-based development？

- 允许持续的代码集成（CI）：在『基于主干的开发模式』中，源源不断的提交合入主干分支。为每个提交添加自动化测试套件和代码覆盖率监控可以实现持续集成。当新代码合并到主干中时，会运行自动集成和代码覆盖测试以验证代码质量。
- 确保持续的代码审查（CR）：『基于主干的开发模式』的快速、小型提交使代码审查成为一个更有效的过程。借助小型分支，开发人员可以快速查看和审查小的更改。与评审者阅读大面积代码变更的长期功能分支相比，这要容易得多。
- 支持连续的生产代码发布（CD）：团队应该每天频繁地合并到主分支。『基于主干的开发模式』努力使主干分支保持 “绿色”，这意味着它可以在每次提交合并后进行部署。自动化测试、代码收敛和代码审查，保证了基于主干的项目可以随时部署到生产环境中。
- 更适用于大型 Monorepo 下的多人协作场景（scalable）：大型 Monorepo 下的多人协作场景更易出现代码冲突，不仅消耗的大量的人力解决冲突，还增加了『长期分支』合入『主干分支』引入 bug 的可能性。与其它存在『长期分支』的功能分支策略相比，开发者可以通过一些小提交创建『短期分支』进行快速迭代。因此，随着代码库复杂性和团队规模的增长，『基于主干的开发模式』也能保证顺畅的多人协作。
- 线性的提交历史（Linear history）：Trunk-based development 更容易做到线性的 commit 历史，它有如下几个好处：
- 方便查看和跟踪历史记录
- 方便回溯变更，比如：Feature A 是在 Bugfix B 之前或者之后引入的？
- 方便排查 bug，比如：使用 Git bisect 二分排查，而非线性历史则难以操作
- 撤销变更，比如：当你发现一个有问题的 commit，简单的 revert 对应的 commit 即可，而非线性的历史会有很多跨分支的合并，使 revert 变得困难

![Linear history & Non-linear history](https://assets.ng-tech.icu/item/20230302201925.png)

## 有效的两个前提

- 持续集成和测试：在每次代码合并前后，开发者都需要知道自己的代码对主干带来了什么影响，因此持续集成和测试的能力必不可少。

- 功能开关：因为在基于主干开发时，大的功能被分解为小改动，因此对于还未完成而之后部分合并进主干的功能，我们需要功能开关来不让他们过早地暴露给用户。

功能开关通常是一套独立的控制系统，线上的代码有两套逻辑，然后通过实时读取功能开关的取值来决定是否隐藏或暴露某个功能。通常，我们在部署完一个功能相关的所有代码之后打开某个功能开关。然后当此功能已经稳定并且被永久加入产品后，会把功能开关和相关的逻辑代码删除掉。

# Links

- https://mp.weixin.qq.com/s?__biz=MjM5MTA1MjAxMQ==&mid=2651260181&idx=1&sn=aa8055a7b6a253fe95e638b314932d8d&chksm=bd48dc918a3f5587cba4b8905834eae7ab1228441c0d24b50616e581ef077432fc465f2cab2d&mpshare=1&scene=1&srcid=0302oEojgsR0DveQxFMS50A9&sharer_sharetime=1677724863503&sharer_shareid=b12a66c3f9db4c759a84aa32051be4ab&version=4.1.0.99228&platform=mac#rd
