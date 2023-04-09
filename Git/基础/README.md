# Git 基础

![](https://github.com/xirong/my-git/blob/master/command-handbook/git-cheat-sheet.png?raw=true)

![](https://raw.githubusercontent.com/xirong/my-git/master/command-handbook/git%E5%B8%B8%E7%94%A8%E5%91%BD%E4%BB%A4.png)

The name "git" was given by Linus Torvalds when he wrote the very first version. He described the tool as "the stupid content tracker" and the name as (depending on your way):

- random three-letter combination that is pronounceable, and not actually used by any common UNIX command. The fact that it is a mispronunciation of "get" may or may not be relevant.

- stupid. contemptible and despicable. simple. Take your pick from the dictionary of slang.

- "global information tracker": you're in a good mood, and it actually works for you. Angels sing, and a light suddenly fills the room.

- "g*dd*mn idiotic truckload of sh\*t": when it breaks

The major difference between Git and any other VCS (Subversion and friends included) is the way Git thinks about its data. Conceptually, most other systems store information as a list of file-based changes. These other systems (CVS, Subversion, Perforce, Bazaar, and so on) think of the information they store as a set of files and the changes made to each file over time (this is commonly described as delta-based version control).

![](https://cdn-images-1.medium.com/max/1600/1*6ywHRvYfgRVCSL_4xh1Mfw.png)

Git doesn’t think of or store its data this way. Instead, Git thinks of its data more like a series of snapshots of a miniature filesystem. With Git, every time you commit, or save the state of your project, Git basically takes a picture of what all your files look like at that moment and stores a reference to that snapshot. To be efficient, if files have not changed, Git doesn’t store the file again, just a link to the previous identical file it has already stored. Git thinks about its data more like a stream of snapshots.

![](https://cdn-images-1.medium.com/max/1600/0*V1iIPrfbxJFLOQEU.png)

- Snapshot: It records all your files at a given point of time so that you can look up at them anytime later. It is basically a way how GIT tracks your code history.

- Commit: The act of creating a snapshot is called a commit. One is advised to commit his code whenever there are significant changes made.

- Repository: The location or digital storage where all your files are stored.

- Head: The reference to the most recent commit is called Head.

- Branches: GIT follows a sort of tree like analogy for keeping track of code, when several people are collaborating on a project, the general procedure is to make a branch from the master branch, do the changes there and make a request to the master branch to merge the code. Basically, All commits live on some branch and there can be many branches in a single project repository.

![](https://cdn-images-1.medium.com/max/1600/0*-8t0j0AN8GL2OP9y.png)
