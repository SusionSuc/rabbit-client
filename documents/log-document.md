# Rabbit的日志逻辑

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