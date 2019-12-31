# 异常捕获与内存泄漏

## 异常捕获

`rabbit`目前只支持捕获`java层`异常,主要实现原理如下:

```
Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
    saveCrash(throwable, thread, defaultExceptionHandler)
}
```

## 内存泄漏捕获

`rabbit`目前通过`leakcanary`来捕获应用的内存泄漏。


