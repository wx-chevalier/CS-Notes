# Cgo

要使用 Cgo 特性，需要安装 C/C++构建工具链，在 macOS 和 Linux 下是要安装 GCC，在 windows 下是需要安装 MinGW 工具。同时需要保证环境变量 CGO_ENABLED 被设置为 1，这表示 Cgo 是被启用的状态。在本地构建时 CGO_ENABLED 默认是启用的，当交叉构建时 Cgo 默认是禁止的。比如要交叉构建 ARM 环境运行的 Go 程序，需要手工设置好 C/C++ 交叉构建的工具链，同时开启 CGO_ENABLED 环境变量。然后通过 import "C"语句启用 Cgo 特性。
