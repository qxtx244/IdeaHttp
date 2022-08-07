IdeaHttp
========

## **概述**
+ 基于OkHttp3封装
+ 借鉴于Retrofit2，支持请求返回数据的反序列化和请求返回的线程切换
+ 链式调用在使用上更加简洁
+ 请求过程拆分，各个阶段创建的对象可复用
+ 默认支持https请求
+ 支持请求拦截器的动态添加/移除
+ 支持自动处理服务端下发的cookie
+ 使用的三方库  
  - [OkHttp-4.9.3](https://github.com/square/okhttp/tree/parent-4.9.3)
  - [Fastjson-1.2.32](https://github.com/alibaba/fastjson/tree/1.2.32)

## **使用**

### **添加到项目**
1. 添加mavenCentral仓库  
   `Gradle6.x或更低版本`  
   在工程根build.gradle中
   ```
   buildscript {
     repositories {
         mavenCentral() //添加这行代码
     }
   }
   ```
   `Gradle7.x及更高版本`
   ```
   dependencyResolutionManagement {
        repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
        repositories {              
            mavenCentral()  //添加这行代码
        }
    }
   ```
   注意，如果这里是**RepositoriesMode.PREFER_PROJECT**，则应改为使用Gradle6.x版本的配置方式
2. 在目标module的build.gradle中
   ```
   dependencies {
     implementation('io.github.qxtx244.http:IdeaHttp:1.0.0')  //添加这行代码
   }
   ```

### **使用功能**
* **创建实例**
  ```
  val client = HttpBase()
  ```
* **初始化**  
  根据需要，选择合适的初始化方法进行初始化
  ```
  client.init(...)
  ```
* **设置Cookie**  
  支持手动设置cookie，在请求地址匹配成功时，自动为请求添加Cookie。
  ```
  client.setCookie("cookie的有效域", "cookie索引名称", "cookie值")
  ```
  或
  ```
  client.setCookie(HttpCookie("cookie索引名称", "cookie值"))
  ```
  例：".abc.d"可成功匹配"www.abc.d"，"http://www.abc.dd"等。
* **开始请求**  
  · 创建请求入口对象。
  ```
  val request = client
        .newRequest(baseUrl)                         //创建请求对象，传入基础url，可在后续继续拼接url，以可以形成不同的请求地址       
        .addHeader(...)                               //支持添加多个请求头
        .addUrlParam(...)                             //支持添加多个url参数，将会以“&key=value”的形式拼接到url上
        .setResponseConverter(Converter.Factory)  //设置反序列化方案，对请求返回的数据主动进行反序列化操作。
                                                         //预置基于FastJson的反序列化方案FastjsonConverterFactory
        .setExecutor(Executor)                       //设置线程切换方案，请求返回后，在指定的线程中执行事件回调方法
  ```
  `request`对象可以复用。  
  在每一次具体请求前，可以对baseUrl进行补全，以形成不同的请求地址，满足多种请求的需要。每次调用都会覆盖上一次的设置。  
  如果需要，将为拼接的url自动补充url分隔符“/”。
  ```
  request.setSubUrl(subUrl)
  ```
  · get请求
  ```
  val response = request.get().sync(tag1) //开始同步请求
  ```
  或
  ```
  request.get().async(tag2, IHttpCallback\<Response\>)  //开始异步请求
  ```
  · post请求
  ```
  val response = request.post()
        //.addBody(...)     //可以选择添加不限项请求数据，HttpBase将会在请求时整合body数据，自动选择Content-Type
        .setBody(...)       //设置请求数据，和addBody(...)同时使用时，会覆盖addBody(...)设置过的body数据
        .sync(tag3)
  ```
  或
  ```
  request.post()
        //.addBody(...)
        .setBody(...)
        .async(tag4, IHttpCallback\<Response\>)
  ```
  · 其它请求方式可自行了解
4. **添加拦截器**
+ 一般拦截器，在处理请求之前会被触发，可添加多个
  ```
  client.addInterceptor(HttpInterceptor)
  ```
+ 网络请求拦截器，在检查请求缓存之后，并且开始网络请求之前被触发，可添加多个
  ```
  client.addNetworkInterceptor(HttpInterceptor)
  ```
5. **取消请求**  
取消请求可以取消包括正在进行的和还未开始的请求
+ 取消指定tag的请求任务
  ```
  client.cancel(tag)
  ```
+ 取消全部请求
  ```
  client.cancelAll()
  ```

### **其它说明**
+ **FastJson的版本选择**  
  FastJson对kotlin的支持并不完美，目前验证1.2.32版本是可用的。如希望自行更换其它版本，请先确认目标版本是否支持kotlin类的序列化/反序列化。
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

## **Demo**
demo中演示了如何使用IdeaHttp去执行一些同步/异步请求，并包含拦截器配置，反序列化器配置、线程切换和取消请求等部分操作。  
在demo/Doc目录中，提供了多个配套的本地Http服务端的实现代码，用于演示IdeaHttp对响应数据的自动反序列化能力。  
Http服务端代码在IntelliJ Idea(jdk使用corretto-11.0.14.1)上可用。