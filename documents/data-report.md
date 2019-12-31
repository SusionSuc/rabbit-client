# 数据上报

目前`rabbit`支持上报的数据类型有:

1. 页面测速信息
2. 应用冷启动信息
3. 卡顿信息
4. fps
5. 异常信息

## 配置

## 配置上报的地址

```
rabbitConfig.reportConfig.reportPath = "http://127.0.0.1:8000/apmdb/xxxxx"
```

## 是否上报

```
rabbitConfig.reportConfig.reportMonitorData = true
```

### 自定义fps上报的频率

```
rabbitConfig.reportConfig.fpsReportPeriodS = 2  // 2秒上报一次
```

## 不上报哪些数据

```
rabbitConfig.reportConfig.notReportDataFormat.addAll(hashSetOf(RabbitExceptionInfo::class.java))
```
>不上报异常数据

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

