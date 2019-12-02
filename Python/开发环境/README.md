# Python 环境配置与简单应用

# 全局配置

## pyenv

# 局部开发环境配置

## pipenv

建议使用 [pipenv](https://github.com/pypa/pipenv) 作为项目环境管理：

```sh
# 安装 pyenv
$ brew install pipenv

# 创建 Python 2/3 版本的项目
$ pipenv --two/--three

# 安装项目依赖，会在当前目录下生成 .venv 目录，包含 python 解释器
$ pipenv install
$ pipenv install --dev

# 弹出 Virtual Env 对应的脚本环境
$ pipenv shell

# 执行文件
$ pipenv run python

# 定位项目路径
$ pipenv --where
/Users/kennethreitz/Library/Mobile Documents/com~apple~CloudDocs/repos/kr/pipenv/test

# 定位虚拟环境路径
$ pipenv --venv
/Users/kennethreitz/.local/share/virtualenvs/test-Skyy4vre

# 定位 Python 解释器路径
$ pipenv --py
/Users/kennethreitz/.local/share/virtualenvs/test-Skyy4vre/bin/python
```

如果遇到编码问题，可以设置如下环境变量：

```sh
export LC_ALL=zh_CN.UTF-8
export LANG=zh_CN.UTF-8
```

如果遇到网络问题，可以尝试使用国内的镜像源：

```Pipfile
[[source]]
url = "https://mirrors.ustc.edu.cn/pypi/web/simple"
verify_ssl = true
name = "pypi"
```
