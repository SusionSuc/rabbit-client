## Rabbit的日志逻辑

`rabbit`中的Log主要分为下面这些类型:

```
val TAG_STORAGE = "rabbit-storage-log"
val TAG_REPORT = "rabbit-report-log"
val TAG_MONITOR = "rabbit-monitor-log"
val TAG_MONITOR_UI = "rabbit-monitor-ui-log"
val TAG_UI = "rabbit-ui-log"
val COMMON_TAG = "rabbit-log"
```

logcat中过滤相应的TAG即可查看相应模块的日志


## rabbit数据存储相关

可以通过配置让`rabbit`相关监控数据只在一次应用session中有效:

```
rabbitConfig.storageConfig.storageInOnSessionData.storageInOnSessionData.addAll(
    ArrayList<Class<Any>>().apply {
        add(RabbitHttpLogInfo::class.java as Class<Any>)
    })
```

设置网络数据只在一个应用`sesiion`中有效。Rabbit默认`网络日志`和`内存日志`只在一个应用session中有效

