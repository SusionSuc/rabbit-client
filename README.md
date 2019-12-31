
![pic1](documents/picture/rabbit-header.png)

**Rabbit的目标是成为一个全面、简单易用的`APM`系统**

**[使用文档](./documents/README.md)**

# 功能简介

目前主要包括下面功能:

1. 网络日志监控
2. App Crash 捕获
3. App 流畅度(FPS) 监控
4. App 卡顿监控
5. App 测速 (应用启动测速&页面测速)
6. 网络请求耗时监控
7. 内存泄漏检测(LeakCanary)
8. apk内容分析与优化
9. 应用内存分析

**rabbit支持上报所有性能监控数据,详情见使用文档**

# 项目架构

![](./documents/picture/rabbit-client-arc.png)

>实现原理相关文章见:[Rabbit实现原理剖析](https://github.com/SusionSuc/AdvancedAndroid/blob/master/Rabbit%E5%AE%9E%E7%8E%B0%E5%8E%9F%E7%90%86%E5%89%96%E6%9E%90/README.md)

# 开发计划

目前服务端功能正在开发中，详见:

[rabbit-server](https://github.com/SusionSuc/rabbit-server) : 存储客户端上报的数据(`python/django`)

[rabbit-admin](https://github.com/SusionSuc/rabbit-admin) : web端管理后台(vue)


# License

Rabbit is under the MIT license. See the [LICENSE] file for details