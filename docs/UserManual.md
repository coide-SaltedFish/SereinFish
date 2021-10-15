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
### 4.获取设备标识文件 device.json
推荐方法：使用[mcl](https://github.com/iTXTech/mcl-installer/releases)按照提示进行登录

然后在mcl目录找到：\bots\QQ号\device.json 并复制到本程序包目录

### 5.编写start.bat文件(可选)
新建start.bat文件，写入以下命令：

```text

[java.exe路径] [本程序包名称(注：请带上后缀名:.jar)]

```

####示例：

当程序包命名为[sereinfish_bot.jar]时

```text
java sereinfish_bot.jar
```

## 启动
双击start.bat文件一键启动Bot

等待程序初始化完成即可

