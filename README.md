# SmartIM
一个简单的IM API封装，支持SmartQQ、微信

![screenshot](https://raw.githubusercontent.com/Jamling/SmartIM/master/example/example.png)

## 实现

- SmartQQ: 腾讯WebQQ，官网：http://w.qq.com/
- Wechat: 微信网页版，官网：https://wx.qq.com/

## 模块

本项目为maven工程，有以下4个模块

- core: SmartIM中间层及相关IM接口。
- smartqq：SmartQQ协议层，基于[ScienJus/smartqq](https://github.com/ScienJus/smartqq)修改
- wechat: 微信协议层，基于[biezhi/wechat-bot-api](https://github.com/biezhi/wechat-bot-api)修改
- example: 示例模块，也是使用Swing编写的可运行程序

## 设计

- 协议层： IM协议的API封装，网络框架使用okhttp，接口解析使用gson，原则上，一个API对应一个方法。
- 中间层： 对协议层的封装，面向业务逻辑设计，负责向应用层提供数据，分发事件，比如消息接收事件，联系人变更事件。
- 应用层： 基于协议层和中间层的应用，比如自动回复机器人，参考[应用](#应用)。

## 应用

- [SmartIM example](https://jamling.github.com/jws): SmartIM的Swing客户端，支持SmartQQ、微信聊天
- [SmartQQ4Eclipse](https://github.com/Jamling/SmartQQ4Eclipse): SmartIM的eclipse插件，可以在eclipse中使用QQ、微信进行聊天并且相互交流代码问题
- [SmartQQ4IntelliJ](https://github.com/Jamling/SmartQQ4IntelliJ): SmartIM的Idea插件，功能上要比eclipse少

## Example运行

三种运行方式，提前是都需要安装Java SDK或JRE。

1. 下载SmartIM-App.jar，双击执行
2. 通过`java -jar SmartIM-App.jar`来执行
3. 找到example下的Application.java类，在IDE中Run As Main

### Windows

双击`SmartIM-App.jar`即可

### Ubuntu (桌面版)
先安装jre（已有jre的跳过）
 打开终端输入`java -version`，如果java未安装，则会提示找不到java指令，然后建议你安装列出的软件包，建议选择安装`openjdk`的jre。
 安装完成后再输入`java -version`，测试一下jre是否安装成功

创建一个**启动器**，类型为**应用程序**，名称随便填，命令写`java -jar /home/jamling/SmartIM-App.jar`。注意jar的路径要是绝对路径哦。
然后就能像在windows下面一样双击运行了，是不是很方便？

如有更多问题，建议先参考[常见问题]

## 即时讨论
请加入![QQ群: 307490913](group.png)

[常见问题]:https://github.com/Jamling/SmartIM/wiki/%E5%B8%B8%E8%A7%81%E9%97%AE%E9%A2%98
