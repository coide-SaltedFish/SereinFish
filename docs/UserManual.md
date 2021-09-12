# 关于

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

YuQ.bot.name = [Bot名字] [#这是可选的，如果需要编写，请使用uncode编码]

YuQ.Mirai.user.qq = [这里写QQ号]

YuQ.Mirai.user.pwd = [这里写QQ密码]

### 4.获取设备标识文件 device.json
推荐方法：使用[mcl](https://github.com/iTXTech/mcl-installer/releases)按照提示进行登录

然后在mcl目录找到：\bots\QQ号\device.json 并复制到本程序包目录

### 5.编写start.bat文件(可选)
新建start.bat文件，写入以下命令：

[java.exe路径] [本程序包名称(注：请带上后缀名:.jar)]

## 启动
双击start.bat文件即可一键启动Bot，等待程序初始化完成即可

