# Rabbit [Developing]

**Rabbit是一个可以帮助Android开发者提高开发效率和App质量的开发者工具。Rabbit目前处于开发阶段,各个功能都处于不断完善中。**

目前主要包括下面功能:

1. 网络日志监控
2. App Crash 捕获
3. App 流畅度(FPS) 监控
4. App 卡顿监控
5. App 页面启动时间与平均帧率监控 (开发中)

# 使用文档

**Rabbit目前并没有提供快捷引入其他App的方法**

## 初始化

>在`Application`中添加:
```
Rabbit.init(this)
```

## 网络日志监控

目前`Rabbit`只支持监控`OkHttp`的网络日志，可以在`OkHttpClient`构建时添加下面代码来开启**网络日志监控功能**:

```
val okHttpClient = OkHttpClient.Builder().addInterceptor(Rabbit.getHttpLogInterceptor())
```

添加后可以在`Rabbit`中查看网络日志:

![net1](picture/rabbit_net1.png)

![net1](picture/rabbit_net2.png)

## 异常日志捕获

`Rabbit`默认使用`Theread.setDefaultUncaughtExceptionHandler`来捕获线程crash并保存到数据库中:

![net1](picture/rabbit_exception1.png)

## App 流畅度(FPS) 监控  && App 卡顿监控

可以在Rabbit功能配置页打开这两个功能。当监控到卡顿时会保存到数据库，可以在Rabbit中查看页面卡顿原因。

# 实现原理

Rabbit中各个功能的实现会逐渐更新在wiki中,欢迎关注我的微信公众号或者掘金快速获得相关信息:

- [掘金](https://juejin.im/user/57b1173f165abd0054298059)

![](picture/微信公众号.jpeg)
