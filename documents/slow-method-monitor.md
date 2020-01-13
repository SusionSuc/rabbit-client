# 慢函数检测(函数耗时)

>慢函数: 卡顿主线程超过一定时间的函数。

该功能主要是通过编译时字节码插桩来完成的,支持对慢函数进行分包统计,需要在`build.gradle`文件中定义不同的包:

```
apply plugin: 'rabbit-tracer-transform'

rabbitConfig {

    enableMethodCostCheck = true //默认是关闭的

    methodMonitorPkgs = ['com.susion.rabbit.demo', 'com.susion.rabbit.demo.page']  //指定检测范围
}
```

`rabbit`默认卡顿主线程15ms以上的函数都是慢函数，当然也可以配置慢函数的检测阈值:

```
rabbitConfig.monitorConfig.slowMethodPeriodMs = 20
Rabbit.config(rabbitConfig)
```

>打开慢函数检测开关后可以看到如下图的统计结果:

![](./pic/slow-method-pkg.jpg)

点击卡片可以看到每一个包下的慢函数:

![](./pic/slow-method-list.jpg)

点击卡片可以看到该函数相关调用堆栈:

![](./pic/slow-method-stack.jpg)