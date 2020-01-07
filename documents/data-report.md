# 数据上报

目前`rabbit`支持上报的数据类型有:

1. 页面测速信息
2. 应用冷启动信息
3. 卡顿信息
4. fps
5. 异常信息
6. 慢函数

# 自定义上报

`rabbit`中提供了数据上报回调，如果你不想使用`rabbit`的数据上报系统，可以使用这个回调完成自定义的上报逻辑:

```
rabbitConfig.reportConfig.enable = false  //禁用掉rabbit的上报逻辑
rabbitConfig.reportConfig.dataReportListener = object :RabbitReportConfig.DataReportListener{
    override fun onPrepareReportData(data: Any, currentUseTime: Long) {
        //接入自己的上报逻辑
    }
}
```

# rabbit数据上报逻辑

## 上报的数据格式

以`json`格式上报任何数据。

### 基本格式

```
{
    "deviceInfoStr": "{....}",
    "infoStr": "{...}",
    "time": 1577775888933,
    "type": "fps_info",
    "useTime": 19
}
```

- type : 指定上报的数据类型
- deviceInfoStr : 设备信息, 格式json字符串
- infoStr: 该类型的数据的具体内容, 格式为json字符串
- useTime: 应用当前使用时长

### 数据类型

目前`type`与`infoStr`的对应关系如下:

```
fun getDataType(info: Any): String {
    return when (info) {
        is RabbitPageSpeedInfo -> "page_speed"
        is RabbitAppStartSpeedInfo -> "app_start"
        is RabbitBlockFrameInfo -> "block_info"
        is RabbitFPSInfo -> "fps_info"
        is RabbitExceptionInfo -> "exception_info"
        else -> "undefine"
    }
}
```

**对应每种`infoStr`包含哪些信息可以参考具体的对象**

**`deviceInfoStr`对应于`RabbitDeviceInfo`**

## 向服务端发送的上报数据

`rabbit`支持一次发送一个数据上报点或者一次发送多个上报点。

- 一次上报一个数据

会把数据base64后直接发出

- 一次上报多个数据

base64每一个上报数据之后，然后用"&"拼接起来发送。

# 自定义配置

## 配置上报的地址

```
rabbitConfig.reportConfig.reportPath = "http://127.0.0.1:8000/apmdb/xxxxx"
```

## 是否上报

```
rabbitConfig.reportConfig.enable = true
```

>这个也可以通过UI来动态控制

## 不上报哪些数据

```
rabbitConfig.reportConfig.notReportDataFormat.addAll(hashSetOf(RabbitExceptionInfo::class.java))
```
>不上报异常数据

## 请求发送相关

```
rabbitConfig.reportConfig.emitterSleepCount = 3  
rabbitConfig.reportConfig.batchReportPointCount = 5
rabbitConfig.reportConfig.emitterFailedRetryCount = 2
```

batchReportPointCount : 每次向服务器发送几个点

emitterSleepCount: 一次发送多个点时，点的数据不够时等待的次数。 每次等待5秒

emitterFailedRetryCount : 发送失败时请求重试的次数

### 自定义fps上报的频率

```
rabbitConfig.reportConfig.fpsReportPeriodS = 2  // 2秒上报一次
```