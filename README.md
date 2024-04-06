# SmartIM
一个使用微信PC版协议、~~QQ网页版协议~~实现的聊天工具，支持所有平台，无需安装

![screenshot](https://raw.githubusercontent.com/Jamling/SmartIM/master/example/example.png)

## 实现的协议

- SmartQQ: 腾讯WebQQ，官网：http://w.qq.com/, 2019年已停止服务，暂无替代协议。
- Wechat: 微信PC/网页版，官网：https://wx.qq.com/

## 工程简介

本项目为gradle工程，有以下4个模块

- core: SmartIM中间层及相关IM接口。
- smartqq：SmartQQ协议层，基于[ScienJus/smartqq](https://github.com/ScienJus/smartqq)修改
- wechat: 微信协议层，基于[biezhi/wechat-bot-api](https://github.com/biezhi/wechat-bot-api)修改
- example: 示例应用模块，使用Swing界面技术

## 设计

- 协议层： IM协议的API封装，网络框架使用okhttp，接口解析使用gson，原则上，一个API对应一个方法。
- 中间层： 对协议层的封装，面向业务逻辑设计，负责向应用层提供数据，分发事件，比如消息接收事件，联系人变更事件。
- 应用层： 基于协议层和中间层的应用，比如自动回复机器人，参考[应用](#应用)。

## 基于本项目的应用

- [SmartIM example](https://jamling.github.com/jws): SmartIM的Swing客户端，支持SmartQQ、微信聊天
- [SmartIM4Eclipse](https://github.com/Jamling/SmartIM4Eclipse): SmartIM的eclipse插件，除了聊天，还能进行代码评审
- [SmartIM4IntelliJ](https://github.com/Jamling/SmartIM4IntelliJ): SmartIM的IDEA插件，除了聊天，还能进行代码评审

## Example运行

三种运行方式，前提是都需要安装Java SDK或JRE。

1. 下载SmartIM-App.jar ([下载站点：国外](https://jamling.github.com/jws/SmartIM-App.jar),[下载站点：国内](http://dl.ieclipse.cn/jws/SmartIM-App.jar))后，双击或通过`java -jar SmartIM-App.jar`来运行
2. 使用Java Web Start运行，点击[https://dl.ieclipse.cn/jws/SmartIM-App.jnlp](https://dl.ieclipse.cn/jws/SmartIM-App.jnlp)或通过命令`javaws https://dl.ieclipse.cn/jws/SmartIM-App.jnlp` 运行
3. 将项目导入Eclipse等IDE中，找到example下的Application.java类，在IDE中Run As Main

### Windows

双击`SmartIM-App.jar`即可

### Ubuntu (桌面版)
先安装jre（已有jre的跳过）
 打开终端输入`java -version`，如果java未安装，则会提示找不到java指令，然后建议你安装列出的软件包，建议选择安装`openjdk`的jre。
 安装完成后再输入`java -version`，测试一下jre是否安装成功

创建一个**启动器**，类型为**应用程序**，名称随便填，命令写`java -jar /home/jamling/SmartIM-App.jar`。注意jar的路径要是绝对路径哦。
然后就能像在windows下面一样双击运行了，是不是很方便？

如有更多问题，建议先参考[常见问题]

### MacOS
Times字体不存在
点击[https://www.freebestfonts.com/download?fn=1911](https://www.freebestfonts.com/download?fn=1911)下载字体后安装即可

## 即时讨论
请加入![QQ群: 307490913](group.png)

[常见问题]:https://github.com/Jamling/SmartIM/wiki/%E5%B8%B8%E8%A7%81%E9%97%AE%E9%A2%98
