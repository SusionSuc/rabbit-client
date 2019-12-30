# 网络日志监控

该功能目前只支持`okhttp`框架，需要在初始化`OkHttpClient`时加入自定义的拦截器`Rabbit.getNetInterceptor()`:

```
val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(Rabbit.getNetInterceptor())
    ...
    .build()
```

目前网络日志监控的功能目前主要包括下面这些点:

1. 记录每一个请求的成功与失败
2. 可以方便的查看后台返回的json数据

监控示例:

![pic1](picture/rabbit_net1.png)

![pic1](picture/rabbit_net2.png)