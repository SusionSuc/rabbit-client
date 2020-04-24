# 数据存储

>`rabbit`目前使用`greendao`做数据存储组件。

## 支持配置

- 配置让`rabbit`相关监控数据只在一次应用session中有效:

``` 
rabbitConfig.storageConfig.storageInOneSessionData.add(RabbitMonitorProtocol.FPS.name)
rabbitConfig.storageConfig.storageInOneSessionData.add(RabbitMonitorProtocol.NET.name)
```

- 配置每种数据的最大存储数量

```
rabbitConfig.storageConfig.dataMaxSaveCountLimit[RabbitMonitorProtocol.NET.name] = 100
rabbitConfig.storageConfig.dataMaxSaveCountLimit[RabbitMonitorProtocol.EXCEPTION.name] = 100
```

- 向Rabbit中存储你自定义的数据

>你需要提供你的数据对应的`Greendao Dao`:

```
rabbitConfig.storageConfig.daoProvider.addAll(ArrayList<RabbitDaoProviderConfig>().apply {
    add(
        RabbitDaoProviderConfig(
            DebugTrackInfo::class.java as Class<Any>,
            debugTrackInfoDao as AbstractDao<Any, Long>
        )
    )
})
```

## 数据操作API

`rabbit`数据操作的API位于`RabbitStorage`中,具体功能可以参考这个类。