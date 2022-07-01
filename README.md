IdeaHttp
========

## **概述**
+ 完全使用Kotlin开发
+ 基于OkHttp3封装，遵循RFC7231，RFC2616等协议标准。
+ 借鉴于Retrofit2，支持请求返回数据的反序列化和请求返回的线程切换
+ 链式调用在使用上更加简洁
+ 请求过程拆分，各个阶段创建的对象可复用
+ 默认支持https请求
+ 支持请求拦截器的动态添加/移除
+ 已集成三方库
  - okhttp-4.9.3（已集成）
  - okio-2.8.0（已集成）
  - fastjson-1.2.32（已集成）

## **使用**

### **使用说明**
+ **代码混淆**  
  模块本身不做混淆，如果宿主项目开启了混淆，请加入以下代码到混淆规则：
  ```
  -keep class com.qxtx.idea.http.** {*;}
  -keep class okhttp3.** {*;}
  ```
+ **安卓高版本对http的支持**  
  注意：高版本安卓系统默认不支持http请求。需要手动添加http的支持，否则无法使用http请求。处理方法如下：  
  · 添加资源目录：名称为xml，在此目录下创建任意名称（如http_config）的xml文件，输入以下内容：
  ```
  <?xml version="1.0" encoding="utf-8"?>
  <network-security-config>
      <base-config cleartextTrafficPermitted="true" />  //允许使用http
  </network-security-config>
  ```
  在AndroidManifest.xml中，为<application节点添加两个属性：
  ```
  <manifest
      <application
          ...
          android:usesCleartextTraffic="true"
          android:networkSecurityConfig="@xml/http_config">
  </manifest>
  ```
### **功能使用**
* **创建实例**
  ```
  val http = HttpBase()
  ```
* **初始化**  
  根据需要，选择合适的初始化方法进行初始化
  ```
  http.init(...)
  ```
* **开始请求**  
  · 创建请求入口对象。
  ```
  val request = http
        .newRequest(baseUrl)                         //创建请求对象，传入基础url，可在后续继续拼接url，以可以形成不同的请求地址       
        .addHeader(...)                               //支持添加多个请求头
        .addUrlParam(...)                             //支持添加多个url参数，将会以“&key=value”的形式拼接到url上
        .setResponseConverter(Converter.Factory)  //设置反序列化方案，对请求返回的数据主动进行反序列化操作。
                                                         //预置基于FastJson的反序列化方案FastjsonConverterFactory
        .setExecutor(Executor)                       //设置线程切换方案，请求返回后，在指定的线程中执行事件回调方法
  ```
  `request`对象可以复用。  
  在每一次具体请求前，可以对baseUrl进行补全，以形成不同的请求地址，满足多种请求的需要。每次调用都会覆盖上一次的设置
  ```
  request.subUrl(subUrl)
  ```
  · get请求
  ```
  val response = request.get().sync(tag1) //开始同步请求
  
  request.get().async(tag2, IHttpCallback\<Response\>)  //开始异步请求
  ```
  · post请求
  ```
  val response = request.post()
        //.addBody(...)     //可以选择添加不限项请求数据，HttpBase将会在请求时整合body数据，自动选择Content-Type
        .setBody(...)       //设置请求数据，和addBody(...)同时使用时，会覆盖addBody(...)设置过的body数据
        .sync(tag3)
        
  request.post()
        //.addBody(...)
        .setBody(...)
        .async(tag4, IHttpCallback\<Response\>)
  ```
  · 其它请求方式可自行查看
4. **添加拦截器**
+ 一般拦截器，在处理请求之前会被触发，可添加多个
  ```
  http.addInterceptor(HttpInterceptor)
  ```
+ 网络请求拦截器，在检查请求缓存之后，并且开始网络请求之前被触发，可添加多个
  ```
  http.addNetworkInterceptor(HttpInterceptor)
  ```
5. **取消请求**  
取消请求可以取消包括正在进行的和还未开始的请求
+ 取消指定tag的请求任务
  ```
  http.cancel(tag)
  ```
+ 取消全部请求
  ```
  http.cancelAll()
  ```

## **Demo**
demo中演示了如何使用IdeaHttp去执行一些同步/异步请求，并包含配置拦截器，取消请求等部分操作。
在demo/Doc目录中，提供了一个httpServer