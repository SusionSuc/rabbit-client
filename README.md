# Rabbit

**Rabbit目标是成为一个全面、简单易用的`APM`系统, 目前处于开发阶段,各个功能都在不断完善中,有任何意见欢迎提`issue`或者`mr`**

## 客户端

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

>目前所有功能只能在debug下使用，没有经过线上验证！

### 架构

>Rabbit客户端架构:

![](picture/rabbit-client-arc.png)

## 性能数据上报

支持上报所有的检测数据。

## Rabbit服务端

[开发中][RabbitServer](https://github.com/SusionSuc/RabbitServer)

**具体使用文档见Wiki**

实现原理相关文章见:[Rabbit实现原理剖析](https://github.com/SusionSuc/AdvancedAndroid/blob/master/Rabbit%E5%AE%9E%E7%8E%B0%E5%8E%9F%E7%90%86%E5%89%96%E6%9E%90/README.md)


