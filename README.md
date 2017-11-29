# SmartIM
一个简单的IM API封装，支持SmartQQ、微信

![screenshot](https://raw.githubusercontent.com/Jamling/SmartIM/master/example/example.png)

## 实现

- SmartQQ: 腾讯WebQQ
- Wechat: 微信网页版

## 设计

- 协议层： IM协议的API封装，网络框架使用okhttp，接口解析使用gson，原则上，一个API对应一个方法。
- 中间层： 对协议层的封装，面向业务逻辑设计，负责向应用层提供数据，分发事件，比如消息接收事件，联系人变更事件。
- 应用层： 基于协议层和中间层的应用，比如自动回复机器人。

## 应用

- [SmartIM example](https://jamling.github.com/jws): SmartIM的Swing客户端，支持SmartQQ、微信聊天
- [SmartQQ4Eclipse](https://github.com/Jamling/SmartQQ4Eclipse): SmartIM的eclipse插件，可以在eclipse中使用QQ、微信进行聊天并且相互交流代码问题
- [SmartQQ4IntelliJ](https://github.com/Jamling/SmartQQ4Eclipse): SmartIM的Idea插件，功能上要比eclipse少

## Example运行

三种运行方式，提前是都需要安装Java SDK或JRE。

1. 下载SmartIM-App.jar，双击执行
2. 通过`java -jar SmartIM-App.jar`来执行
3. 找到example下的Application类，在IDE中Run As Main
