# Rabbit使用文档

>version : 1.0.3

**目前仅支持迁移到androidx的项目**

## 文档索引

- [应用测速](./speed-monitor.md)
- [慢函数检测](./slow-method-monitor.md)
- [代码扫描](./block-code-monitor.md)
- [网络日志监控](./net-log-monitor.md)
- [卡顿日志监控](./block-log-monitor.md)
- [FPS和内存监控](./memory-fps-monitor.md)
- [异常与内存泄漏捕获](./exception-leak.md)
- [apk包分析](./app-analyzer.md)
- [接入自定义业务面板](./custom-page.md)
- [数据存储](./storage.md)
- [数据上报](./data-report.md)
- [noop包接入](./noop-document.md)
- [rabbit配置相关](./rabbit-config.md)
- [release文档](./release/release-1.0.md)
- [其他](./others.md)

## 快速使用

`rabbit`的功能没有经过线上验证, 因此目前只能在`debug`下使用, 可以通过下面的方式来安全引入`rabbit` : [noop包接入](./noop-document.md)

### 初始化与配置

#### 初始化

```
Rabbit.init(application, config)
```

相关支持配置见:[RabbitConfig](https://github.com/SusionSuc/rabbit-client/blob/master/rabbit-base/src/main/java/com/susion/rabbit/base/config/RabbitConfig.kt)。各项配置具体含义会在每个功能的文档中做详细的介绍。

#### 插件配置

rabbit的`gradle`插件目前主要涉及到代码插桩，可以通过下面对插桩做配置:

```
rabbitConfig {
    enable = true  // 是否启动字节码插桩. 状态切换应clean工程
    printLog = true // 编译时打印插桩log
}
```

**更多配置选项见各个功能的具体使用文档**

### 打开rabbit

`rabbit`使用悬浮窗来展示各种监控数据，因此需要申请悬浮窗权限, 不过内部自带了权限申请逻辑, 调用下面方法来打开`rabbit`:

>Rabbit.kt
```
fun open(requestPermission: Boolean = true, activity: Activity)
```

即`Rabbit.open(true, this)`, 这个方法会主动申请悬浮窗权限。如果有权限的话会打开`rabbit`浮标:

![](./pic/fps.jpg)

点击`rabbit`浮标即可打开`rabbit`主面板:

![](./pic/entry.jpg)

**可以通过再次点击`rabbit`浮标来关闭`rabbit`浮窗。**








