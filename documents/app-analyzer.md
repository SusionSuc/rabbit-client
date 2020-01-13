# Apk包分析

`rabbit`提供了一个**apk-analyzer.jar**文件，可以通过运行这个jar包来分析`apk`的内容，目前分析的点包括:

1. app基本信息
2. 大图
3. apk组成
4. 重复文件
5. apk方法数

可用的分析jar:[apk-analyzer.jar](https://github.com/SusionSuc/rabbit-client/releases/download/v1.0-beta/rabbit-apk-analyzer.jar)

## 使用配置

**apk-analyzer.jar**在运行时需要一个配置文件:

```
java -jar apk-analyzer.jar apk-analyzer-config.json
```

上面`apk-analyzer-config.json`中约定的格式如下:

```
{
    "apkPath":"xxxx/app-Release.apk",
    "methodGroup":[{
        "name":"xxx",
        "package":"com.xxx.xx"
    }],
    "classMappingFilePath":"/Users/susionwang/Desktop/apk-analyzer/apk-analyzer-config.json",
    "maxImageSizeKB":30,
    "uploadPath":"xxx/upload"
}
```

- apkPath : 待分析的apk的路径
- methodGroup : apk方法数统计归组
- classMappingFilePath : mapping文件的路径
- maxImageSizeKB : 大于多少KB的图片会当做大图被检测出来。
- uploadPath : 分析结果上传的地址, 数据格式与基本上报格式一致:[数据上报](./data-report.md)

## 分析结果示例

分析结果会输出在`apk-analyzer-result.json`文件中:

```
{
    "AppInfo": {
        "versionCode": "1004000",
        "versionName": "1.4.0",
        "appSize": "21.51 MB"
    },
    "BigImageRes": [
        {
            "name": "assets/flutter_assets/images/icons/xxLogo.png",
            "size": "76.73 KB"
        }
        {
            "name": "res/drawable-xhdpi-v4/ixxxxiao.png",
            "size": "47.90 KB"
        }
        ...
    ],
    "ApkCompose": [
        {
            "type": "so",
            "totalSizeStr": "8.05 MB"
        },
        {
            "type": "dex",
            "totalSizeStr": "6.54 MB"
        },
        {
            "type": "png",
            "totalSizeStr": "3.08 MB"
        },
        {
            "type": "gif",
            "totalSizeStr": "1.37 MB"
        },
        {
            "type": "arsc",
            "totalSizeStr": "638.21 KB"
        }
        ...
    ],
    "DuplicatedFile": [
        {
            "files": [
                "res/drawable-xxhdpi-v4/bg_home_water_ripple.webp",
                "res/drawable-xxhdpi-v4/bg_keyboard_shadow_line.webp"
            ],
            "fileSize": "",
            "md5Value": "d41d8cd98f00b204e9800998ecf8427e"
        }
        ....
    ],
    "MethodCount": {
        "total-count": 127732,
        "com.xxx.xx": 14800,
        "other-pkg": 112932
    }
}
```