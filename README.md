
![version](https://img.shields.io/badge/version-1.0.1-brightgreen)
![license](https://img.shields.io/badge/license-MIT-brightgreen)
![androidx](https://img.shields.io/badge/support-androidx-brightgreen) 

# 功能列表

### 应用测速

可以准确统计应用`Applicaiton`创建耗时、应用冷启动耗时和页面inflate耗时&完全渲染耗时

### FPS分析

可以实时显示FPS并支持分页面分析FPS

### 代码扫描

编译时扫描出一些敏感函数(比如耗时函数)

### 慢函数检测

编译时插桩，准确的统计每一个函数的耗时，并显示运行时调用堆栈。

### 网络请求监控

监控App网络请求,可以很方便的查看返回的`json`数据

### 内存分析

实时显示应用的内存并支持分页面分析内存状态

### 应用crash捕获

支持捕获java层异常

### apk分析与优化

可以准确的分析出apk中的大图、重复文件、apk包大小与内容组成

### 自定义UI

可以方便的接入应用的“后门”

### 数据上报

支持上报所有监控数据, 也支持自定义数据上报逻辑

### 极高的可配置性

提供了众多配置项,可以灵活的检测应用的性能问题

# [使用文档](./documents/README.md)

# 实现原理

实现原理相关文章见:[Rabbit实现原理剖析](https://github.com/SusionSuc/AdvancedAndroid/blob/master/Rabbit%E5%AE%9E%E7%8E%B0%E5%8E%9F%E7%90%86%E5%89%96%E6%9E%90/README.md)

# 贡献代码

如果你对`rabbit`比较感兴趣，或者发现`rabbit`还有一些功能不够完善，欢迎提`pr`参与到rabbit的开发中!

>个人微信:SusionSuc

# License

Rabbit is under the MIT license. See the [LICENSE](./LICENSE) file for details
