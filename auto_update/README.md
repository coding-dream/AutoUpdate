## Android自动更新库

服务器url返回的的内容类似如下

```
{
 "versionName":"v1.0",
 "versionCode":"2",
 "downUrl":"https://git.oschina.net/need88.com/TempFile/raw/master/test.apk",   //apk的下载地址
 "updateInfo":"1.更新xxx\n2.update bugs\n3.update fixs"
}

```

## 方式一

```
 UpdateAgent.init(Constants.SERVER_URL);
 UpdateAgent.update(this); //默认
 //UpdateAgent.forceUpdate(this);//强制
 //UpdateAgent.silentUpdate(this);//静默

```


## 方式二

```
String jsonStr = getStrFromServer();
UpdateAgent.updateByStr(this,jsonStr, UpdateAgent.Type.defaultType); // UpdateAgent.Type.silentType,  UpdateAgent.Type.forceType

```

