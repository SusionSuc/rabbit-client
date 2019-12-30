# 应用测速功能

应用测速可以分为下面4个功能:

1. Application创建耗时
2. 页面测速
3. 应用冷启动测速
4. 页面网络请求耗时监控

测速功能主要是通过编译期字节码插桩来完成的，需要引入自定义的gradle插件:

>根build.gradle
```
classpath 'com.susion:rabbit-gradle-transform:0.0.3'
```

>应用build.gradle
```
apply plugin: 'rabbit-tracer-transform'
```

## Application创建耗时

`rabbit`会统计应用的`Applicaiton.onCreate()`方法耗时。

## 页面测速

对于页面测速的统计主要分为3段:

1. `Activity.onCreate`耗时
2. `Activity`首次inflate耗时
3. `Activity`网络请求结束首次渲染耗时

对于**Activity网络请求结束首次渲染耗时**,需要应用在`assets`目录下提供`rabbit_speed_monitor.json`文件:

>比如:
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

对于**MainActivity网络请求结束首次渲染耗时**定义就是: `api/xxx1`和`api/xxx2`请求结束后引发的页面渲染事件。上面对于`ForumContainerActivity`的统计结果如下图:

![pic1](./picture/page_render_speed.png)

## 应用冷启动测速

如果你通过`rabbit_speed_monitor.json`配置`home_activity`和`home_activity`的完全渲染事件，那么从`Application`创建到`home_activity`的**MainActivity的网络请求结束首次渲染**耗时就是应用的冷启动耗时:

![pic2](./picture/app_speed.png)


## 网络请求耗时

可以在`rabbit_speed_monitor.json`文件中配置每个页面需要监控的网络请求，最终会生成如下统计结果:

![pic3](picture/page_request_speed.png)


## 参考文章

[rabbit应用测速实现原理](https://github.com/SusionSuc/AdvancedAndroid/blob/master/Rabbit%E5%AE%9E%E7%8E%B0%E5%8E%9F%E7%90%86%E5%89%96%E6%9E%90/%E5%BA%94%E7%94%A8%E6%B5%8B%E9%80%9F%E7%BB%84%E4%BB%B6.md)

[Android自动化页面测速在美团的实践](https://tech.meituan.com/2018/07/12/autospeed.html)