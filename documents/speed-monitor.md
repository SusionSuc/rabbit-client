# 应用测速功能

目前测速点主要有:

1. Application创建耗时
2. 页面测速
3. 应用冷启动测速
4. 页面网络请求耗时监控

>具体耗时统计的含义如下图:

![pic](./pic/rabbit-speed-time.png)

## 使用

测速功能主要是通过编译期字节码插桩来完成的，因此需要引入`rabbit`插件:

>应用build.gradle
```
apply plugin: 'rabbit-tracer-transform'

rabbitConfig {
    pageSpeedMonitorPkgs = ['com.susion.rabbit.demo']  // 指定测速范围
}
```

### 配置

可以在`rabbit`初始化时提供[RabbitAppSpeedMonitorConfig](https://github.com/SusionSuc/rabbit-client/blob/master/rabbit-base/src/main/java/com/susion/rabbit/base/entities/RabbitAppSpeedMonitorConfig.kt)对象来对要监控的内容进行配置:

```
rabbitConfig.monitorConfig.monitorSpeedList = loadMonitorSpeedConfig()
Rabbit.config(rabbitConfig)
```

这里`loadMonitorSpeedConfig()`是从`assert`文件夹下读取了配置:

```
 private fun loadMonitorSpeedConfig(): RabbitAppSpeedMonitorConfig {
        try {
            val jsonStr =
                FileUtils.getAssetString(RabbitMonitor.application!!, "rabbit_speed_monitor.json")
            if (jsonStr.isEmpty()) return RabbitAppSpeedMonitorConfig()
            return Gson().fromJson(jsonStr, RabbitAppSpeedMonitorConfig::class.java)
        } catch (e: Exception) {

        }
        return RabbitAppSpeedMonitorConfig()
    }

```

>`rabbit_speed_monitor.json`:

```
{
  "home_activity": "MainActivity",
  "page_list": [
    {
      "page": "ForumContainerActivity",
      "api": [
        "api/xxx1",
        "api/xxx2"
      ]
    }
    ...
  ]
}
```

`home_activity`:它指定了应用的入口`Activity`, 主要和应用冷启动逻辑相关联，如果配置的此项，那么冷启动耗时可以测量到上图的中**T6**。

`page_list`: 用来配置每一个测速页面。当配置的`api`中的接口都请求完成后就会触发这个页面的**T6**点。


>当然你也可以从网络动态下发配置,对于这种情况你可能需要使用这个API:

```
Rabbit.reConfig(configWithSpeedList)
```

**在开始测试前不要忘记打开测速功能**

## 统计示例

最终生成的测速结果如下图:

>页面渲染 & 网络耗时

![](./pic/page-start.jpg)


>应用冷启动

![](./pic/app-start.jpg)


## 参考文章

[rabbit应用测速实现原理](https://github.com/SusionSuc/AdvancedAndroid/blob/master/Rabbit%E5%AE%9E%E7%8E%B0%E5%8E%9F%E7%90%86%E5%89%96%E6%9E%90/%E5%BA%94%E7%94%A8%E6%B5%8B%E9%80%9F%E7%BB%84%E4%BB%B6.md)

[Android自动化页面测速在美团的实践](https://tech.meituan.com/2018/07/12/autospeed.html)