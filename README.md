# SmartIM
一个简单的IM API封装，支持SmartQQ、微信

## 实现

- SmartQQ: 腾讯WebQQ
- Wechat: 微信网页版

## 设计

- 协议层： IM协议的API封装，网络框架使用okhttp，接口解析使用gson，原则上，一个API对应一个方法。
- 中间层： 对协议层的封装，面向业务逻辑设计，负责向应用层提供数据，分发事件，比如消息接收事件，联系人变更事件。
- 应用层： 基于协议层和中间层的应用，比如自动回复机器人。

## 应用

- SmartQQ4Eclipse: SmartIM的eclipse插件，可以在eclipse中使用QQ、微信进行聊天并且相互交流代码问题
- SmartQQ4IntelliJ: SmartIM的Idea插件，功能上要比eclipse少
