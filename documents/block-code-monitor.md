# 阻塞代码检测

`rabbit`默认可以检测出应用中的阻塞代码,比如下面这段代码:

```
//涉及IO操作的代码
getSharedPreferences("test", Context.MODE_PRIVATE).edit().putBoolean("111", true).commit()
```

## 定义扫描范围

可以通过`rabbit gradle dsl`定义阻塞代码扫描范围:

>xxx.gradle
```
rabbitConfig {
    
    enableBlockCodeCheck = true //默认是关闭的

    //阻塞代码扫描范围
    blockCodePkgs = ['com.susion.rabbit.demo']
}
```

`rabbit`会在编译时扫描出这些代码，然后你在`rabbit`面板中就可以查看它们是被谁调用:

![](./pic/block-call.jpg)

**查看之前不要忘记在配置中打开相关开关!!**

>点击可以查看更详细的调用情况。

## 导出扫描结果

点击面板右上角的导出按钮，这些阻塞代码就会被导出到SD卡上，路径为:

```
/{SDcard}/Rabbit/blockCall.txt
```

## 自定义API扫描列表

`rabbit`默认的阻塞代码列表位于:[DEFAULT_BLOCK_APIS](https://github.com/SusionSuc/rabbit-client/blob/master/rabbit-gradle-transform/src/main/java/com/susion/rabbit/gradle/utils/IoScanApiList.kt)

也可以自定义这个API列表:

>xxx.gradle
```
rabbitConfig {
    //自定义阻塞代码点
    customBlockCodeCheckList = ['com/susion/rabbit/demo/MainActivity.fakeBlockCode()V']
}
```
