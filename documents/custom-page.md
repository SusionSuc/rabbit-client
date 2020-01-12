# 自定义页面

`rabbit`依托于浮窗来与用户交互。考虑到很多app都会有一些debug页面，`rabbit`提供了一套API可以把一些你自定义的页面接入到`rabbit`浮窗中。


## 自定义rabbit页面

能够被`rabbit`展示的页面需要继承自`RabbitBasePage`:

```
class CustomBusinessPage(context: Context) : RabbitBasePage(context) {

    override fun getLayoutResId() = INVALID_RES_ID

    override fun setEntryParams(params: Any) {
        super.setEntryParams(params)
    }
    
    init {
        background = getDrawable(context, R.color.rabbit_white)
        addView(TextView(context).apply {
            text = "业务自定义页面"
            textSize = 20f
        })

        setTitle("自定义业务面板")
    }
}
```

- getLayoutResId() : 页面布局, 如果没有可以传`INVALID_RES_ID`
- init代码块 : 初始化页面UI
- setEntryParams : 页面跳转的参数

## 获取Activity Context

**由于`rabbit`展示在window上，它所能提供的上下文并不是`Activity`,如果你需要`Activity`的话，可以调用`Rabbit.getCurrentActivity()`来获取当前应用的页面上下文。**


## 接入到rabbit中

自定义好页面后，可以配置到`rabbit`中:

```
val rabbitConfig = RabbitConfig()
val page1 = RabbitMainFeatureInfo("业务面板",R.mipmap.ic_launcher,CustomBusinessPage::class.java)
val customPages = arrayListOf(page1)
rabbitConfig.uiConfig.entryFeatures = customPages
Rabbit.config(rabbitConfig)
```

这样你就可以在`rabbit`中看到你自己的debug页面了:

![](./pic/entry.jpg)


## 在rabbit中定义更多页面

你可能需要从自定义的`rabbit`页面跳转至另一个自定义的`rabbit`页面，可以通过下面方法跳转:

```
Rabbit.openPage(RabbitExceptionDetailPage::class.java, logInfo)
```

上面打开了`RabbitExceptionDetailPage`页面，并携带了`logInfo`对象作为参数。

在`RabbitExceptionDetailPage`你可以通过`setEntryParams`来接收页面入参:

```
class RabbitExceptionDetailPage(context: Context): RabbitBasePage(context) {

    override fun setEntryParams(exceptionInfo: Any) {
        if (exceptionInfo !is RabbitExceptionInfo) return

        ...use exceptionInfo 对象
    }
}
```