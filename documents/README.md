# Rabbit使用文档

## 文档索引

- [应用测速功能](./speed-monitor.md)
- [网络日志监控](./net-log-monitor.md)
- [卡顿日志监控](./block-log-monitor.md)
- [数据上报](./data-report.md)
- [其他监控功能](./others-monitor.md)

## 引入方法

>根目录`build.gradle`
```
dependencies {
    classpath 'com.susion:rabbit-gradle-transform:0.0.3'
}
```

>应用build.gradle
```
apply plugin: 'rabbit-tracer-transform' //引入插件

dependencies {
    debugImplementation "com.susion:rabbit:0.0.7.1"  
    releaseImplementation "com.susion:rabbit-noop:0.0.7.1" // release 下不做任何操作
} 
```

### 引入示例

>下面是一个引入`rabbit`的思路


1. 新建`rabbit-plugin.gradle`:

```
def taskName = getGradle().getStartParameter().taskNames.toString().toLowerCase()
def inDebug = taskName.contains("debug")
def rabbitVersion = "0.0.7.1"
def rabbitDepen = "com.susion:rabbit-noop:$rabbitVersion"

if(inDebug){
    print("apply rabbit transform ! ---->")
    apply plugin: 'rabbit-tracer-transform'

    rabbitConfig {
        monitorPkgNamePrefixList = ['com.susion.rabbit.demo']
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

相关支持配置见:[RabbitConfig](https://github.com/SusionSuc/Rabbit/blob/master/rabbit/src/main/java/com/susion/rabbit/RabbitConfig.kt),各项配置具体含义会在每个功能的文档中做详细的介绍。



