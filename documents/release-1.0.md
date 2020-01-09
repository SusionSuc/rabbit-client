
**rabbit**是一个可以让Android开发者很方便的在本地对App做性能监控的工具。

>项目地址 : https://github.com/SusionSuc/rabbit-client

# [接入文档]((https://github.com/SusionSuc/rabbit-client/blob/master/documents/README.md))

# 功能介绍

**本文简单介绍一下rabbit所提供的功能,更多详细内容见[rabbit使用文档](https://github.com/SusionSuc/rabbit-client/blob/master/documents/README.md)**

## 应用测速

基于编译时代码插桩, 可以非常方便的测量应用的冷启动时间与页面渲染时间:



## 慢函数检测

基于编译时代码插桩, rabbit可以准确的统计每一个函数的耗时，并筛选出**慢函数**:

>慢函数这里定义为在主线程消耗时间超过一定阈值的函数,rabbit也支持配置检测其他线程的慢函数。



## 代码扫描

rabbit可以扫描出app的阻塞代码，比如`io`操作代码:

```
SharePreferences$Editor.commit()
```

## 网络日志监控

rabbit可以记录网络请求日志并方便的查看后台返回的`json`数据。

## 卡顿日志监控

可以实时的检测出应用的卡顿并记录下卡顿时的堆栈。

## FPS与内存监控

可以实时的显示出当前应用的帧率和内存占用信息。

## 异常捕获

可以捕获应用的`java`层异常并停供了方便的查看方式

## 内存泄漏捕获

接入了最新的`leakcanary`。

## apk包分析

可以准确的分析出apk中的大图、重复文件、apk包大小与内容组成。

## 自定义面板

很多app都会有一些”后门”,rabbit提供了简便的API使你可以很方便的把它们集成到rabbit中。

## 数据上报

提供了完善的数据上报系统,不仅可以上报所有检测的数据, 也可以很方便的接入自定义的上报逻辑。

## noop包

rabbit目前没有经过线上环境的验证，为了方便接入，提供了[noop包](https://github.com/SusionSuc/rabbit-client/blob/master/documents/noop-document.md)

# 参考资料

[booster](https://github.com/didi/booster)

[matrix](https://github.com/Tencent/matrix)

[DoraemonKit](https://github.com/didi/DoraemonKit)

[ArgusAPM](https://github.com/Qihoo360/ArgusAPM)

[fpsviewer](https://github.com/SilenceDut/fpsviewer)

[JsonViewer](https://github.com/smuyyh/JsonViewer)

[AndroidPerformanceMonitor](https://github.com/markzhai/AndroidPerformanceMonitor)