# 引入NOOP包

由于目前`rabbit`中的功能没有在线上验证过, 因此只推荐在`debug`包中使用。下面是一种引入思路:

>根目录`build.gradle`

```
dependencies {
    classpath 'com.susion:rabbit-gradle-transform:${latest-version}'
}
```

>新建`rabbit-plugin.gradle`

```
def taskName = getGradle().getStartParameter().taskNames.toString().toLowerCase()
def inDebug = taskName.contains("debug") //这里要改成针对当前打包系统的判断方式
def rabbitVersion = {latest-version}
def rabbitDepen = "com.susion:rabbit-noop:$rabbitVersion"

if(inDebug){
    print("apply rabbit transform ! ---->")
    apply plugin: 'rabbit-tracer'

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

>主应用`build.gradle`

```
apply from: 'rabbit-plugin.gradle'

dependencies {
    implementation rootProject.ext.rabbitDependence
}

```

# Rabbit UI功能

在`noop`包中是包含`ui`功能的，因此你仍然可以在`rabbit`中自定义你的UI。

> 详见: [自定业务面板](./custom-page.md)
