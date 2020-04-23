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

# 关于rabbit

## 后续开发计划

### 客户端后续开发计划

1. 完善现有功能
2. 实现敏感代码检测功能(IO操作、敏感权限、隐藏API等)
3. 全局性能检测模式
4. ...

### 服务端管理后台

服务端功能正在开发中，详见:

[rabbit-server](https://github.com/SusionSuc/rabbit-server) : 存储客户端上报的数据(`python/django`)

[rabbit-admin](https://github.com/SusionSuc/rabbit-admin) : web端管理后台(vue)


## 参考链接

[booster](https://github.com/didi/booster)

[matrix](https://github.com/Tencent/matrix)

[DoraemonKit](https://github.com/didi/DoraemonKit)

[ArgusAPM](https://github.com/Qihoo360/ArgusAPM)

[json view](https://github.com/smuyyh/JsonViewer)




