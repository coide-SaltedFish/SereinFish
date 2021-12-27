# 关于

SereinFish Bot

# 如何部署

注意：当前版本Bot仅支持Windows环境部署

## 运行环境
首先请确保已经正确安装Java程序运行环境

推荐使用[Java 1.8 SDK](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html)

## 配置启动文件
### 1.下载程序包
### 2.在程序包目录下新建一个conf文件夹
### 3.在conf文件夹中新建 YuQ.properties 文件并编写其内容
#### YuQ.properties 文件编写

```properties
#Bot名字 
#这是可选的，如果需要编写，请使用unicode编码
#YuQ.bot.name =

#这里写QQ号
YuQ.Mirai.user.qq =

#这里写QQ密码
YuQ.Mirai.user.pwd =

```

[更多配置项](https://yuqworks.github.io/YuQ-Doc/guide/basic-configuration.html)
### 4.运行程序并按照提示使用`滑块助手`进行登录，此操作只需进行一次

### 5.编写`start.bat`文件(可选)
新建`start.bat`文件，写入以下命令：

```text

[java.exe路径] -jar [本程序包名称(注：请带上后缀名:.jar)]

```

#### 示例：

当程序包命名为`sereinfish_bot.jar`时

```text
java -jar sereinfish_bot.jar
```

## 启动
双击`start.bat`文件一键启动Bot

等待程序初始化完成即可

## 设置权限

Bot运行并初始化完成后

找到并打开`\SereinFish\conf\authority.json`文件，使用你的`QQ号码`替换下面标注部分，即可将自己设置为`MASTER`权限

`MASTER`以下权限可通过Bot指令设置

```json
{
  "opList": {},
  "masterList": {
    "你的QQ": 你的QQ
  },
  "adminList": {
  },
  "groupBotAdmin": {
  }
}
```

## 获取bot运行状态

权限设置后，在控制面板中打开群聊`启用`开关，也可在群聊中使用`SereinFish Bot 开`指令

并在在群聊中使用`@Bot 运行状态`命令获取运行状态

如果正常返回信息，说明指令路由模块运行状态正常

再次发送`@Bot`指令，如果Bot正常应答，说明事件模块运行正常

## 其他

使用`@Bot help`指令获取指令菜单列表



