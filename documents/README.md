# Rabbit使用文档

## 文档索引

- [应用测速](./speed-monitor.md)
- [慢函数检测](./slow-method-monitor.md)
- [网络日志监控](./net-log-monitor.md)
- [卡顿日志监控](./block-log-monitor.md)
- [FPS和内存监控](./memory-fps-monitor.md)
- [异常与内存泄漏捕获](./others-monitor.md)
- [apk包分析](./app-analyzer.md)
- [接入自定义业务面板](./cutom-page.md)
- [数据上报](./data-report.md)


## 快速使用

>根目录`build.gradle`
```
dependencies {
    classpath 'com.susion:rabbit-gradle-transform:0.0.8'
}
```

>应用build.gradle
```
apply plugin: 'rabbit-tracer-transform' //引入插件

dependencies {
    debugImplementation "com.susion:rabbit:0.0.8"  
    releaseImplementation "com.susion:rabbit-noop:0.0.8" // release 下不做任何操作
} 
```

### 引入示例

>由于功能目前没有在线上环境验证过，因此目前建议只在测试环境下使用。下面是一个引入`rabbit`的思路:

1. 新建`rabbit-plugin.gradle`:

```
def taskName = getGradle().getStartParameter().taskNames.toString().toLowerCase()
def inDebug = taskName.contains("debug") //这里要改成针对当前打包系统的判断方式
def rabbitVersion = "0.0.7.1"
def rabbitDepen = "com.susion:rabbit-noop:$rabbitVersion"

if(inDebug){
    print("apply rabbit transform ! ---->")
    apply plugin: 'rabbit-tracer-transform'

    rabbitConfig {
        methodMonitorPkgs = ['com.susion.rabbit.demo']
        pageSpeedMonitorPkgs = ['com.susion.rabbit.demo']
    }

    rabbitDepen = "com.susion:rabbit:$rabbitVersion"
}

rootProject.ext {
    rabbitDependence = rabbitDepen
}
```

2. 主项目`build.gradle`文件

```
apply from: 'rabbit-plugin.gradle'
```

### 配置

```
Rabbit.config(config)
```

相关支持配置见:[RabbitConfig](https://github.com/SusionSuc/rabbit-client/blob/master/rabbit-base/src/main/java/com/susion/rabbit/base/config/RabbitConfig.kt),各项配置具体含义会在每个功能的文档中做详细的介绍。

### 打开rabbit

`rabbit`使用悬浮窗来展示各种监控数据，因此需要申请悬浮窗权限, 不过内部自带了权限申请逻辑, 调用下面方法来打开`rabbit`:

>Rabbit.kt
```
fun open(requestPermission: Boolean = true, activity: Activity)
```

即`Rabbit.open(true, this)`, 这个方法会主动申请悬浮窗权限。如果有权限的话会打开`rabbit`浮标:

![pic1](./picture/rabbit-float.png)

点击`rabbit`浮标即可打开`rabbit`主面板:

![pic2](./picture/rabbit-entry.png)

**可以通过再次点击`rabbit`浮标来关闭`rabbit`浮窗。**

### 监控功能开关配置

#### 通过代码配置

可以通过代码来配置`rabbit`各项监控的打开与否:

```
val autoOpenMonitors = hashSetOf(RabbitMonitorProtocol.NET.name, RabbitMonitorProtocol.EXCEPTION.name)

rabbitConfig.monitorConfig.autoOpenMonitors.addAll(autoOpenMonitors)

Rabbit.config(rabbitConfig)
```
>上面配置会自动打开**网络日志监控**和**异常日志监控**功能

#### UI配置

当然也可以在UI浮窗中配置各项功能打开与否。点击**监控配置**即可看到具体功能配置页面:

![pic3](./picture/rabbit-config.png)







