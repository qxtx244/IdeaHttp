package com.qxtx.idea.http

import com.qxtx.idea.http.callback.HttpInterceptor
import okhttp3.OkHttpClient
import java.net.HttpCookie

/**
 * @author QXTX-WIN
 *
 * **Create Date** 2022/6/10 10:46
 *
 * **Description**
 *
 * http请求接口
 */
interface IHttp {

    /**
     * 获取指定url的Cookie
     *
     * @param url 地址或地址起始片段
     *
     * @return 返回对应的Cookie缓存，否则返回null
     */
    fun getCookie(url: String): MutableList<HttpCookie>?

    /**
     * 设置候选的cookie，可设置多个。当请求地址匹配成功到cookie的域，自动为请求添加匹配到的Cookie。
     * 总是会覆盖已存在相同域的cookie。当value为null时，表示移除此cookie。
     *
     * @param domain 匹配域。一般地，cookie根据域来进行匹配，而不是完成的url。cookie支持不同端口共享，只要domain和path一致。
     *                  例1：https://www.abc.com/s/xxx，其域为www.abc.com
     *                  例2：https://192.168.1.2:123456/x/sss，其域为192.168.1.2
     *
     * @param name cookie名称
     * @param value cookie值。当为null时，表示移除此cookie
     */
    fun setCookie(domain: String, name: String, value: String?)

    /**
     * 设置候选的cookie，可设置多个。当请求地址匹配成功到cookie的域，自动为请求添加匹配到的Cookie。
     * 总是会覆盖已存在相同域的cookie。
     *
     * @param cookie cookie对象。
     */
    fun setCookie(cookie: HttpCookie)

    /**
     * 初始化方法。
     * @param callTimeoutMs 整个请求过程的超时时长，单位为毫秒。0为永不超时
     */
    fun init(callTimeoutMs: Long)

    /**
     * 初始化方法。
     * @param connectTimeoutMs 请求过程中连接服务端的超时时长，单位为毫秒
     * @param readTimeoutMs 请求过程中读取数据的超时时长，单位为毫秒
     * @param writeTimeoutMs 请求过程中写入数据的超时时长，单位为毫秒
     */
    fun init(connectTimeoutMs: Long, readTimeoutMs: Long, writeTimeoutMs: Long)

    /**
     * 初始化方法
     * @param client 用于全局的OkHttpClient对象。当为null时，使用默认构建的OkHttpClient对象
     */
    fun init(client: OkHttpClient?)

    /**
     * 添加普通拦截器
     * 这种拦截器在责任链头部被回调，此时并未开始进行缓存分析和网络请求
     * @param interceptor 待添加的拦截器对象
     */
    fun addInterceptor(interceptor: HttpInterceptor)

    /**
     * 移除普通拦截器
     * @param interceptor 待移除的拦截器对象
     */
    fun removeInterceptor(interceptor: HttpInterceptor)

    /**
     * 添加网络请求拦截器
     * 这种拦截器在网络请求之前被回调
     * @param interceptor 待添加的拦截器对象
     */
    fun addNetworkInterceptor(interceptor: HttpInterceptor)

    /**
     * 移除网络请求拦截器
     * @param interceptor 待移除的拦截器对象
     */
    fun removeNetworkInterceptor(interceptor: HttpInterceptor)

    /**
     * 全局配置。配置后，对所有请求生效
     * @param builder http客户端对象的建造者
     */
    fun globalConfig(builder: OkHttpClient.Builder)

    /**
     * 拷贝一份全局配置builder对象。
     * 通常作为[.newClient]的参数使用
     * @return 当前全局配置对象的拷贝
     */
    fun copyGlobalConfig(): OkHttpClient.Builder?

    /**
     * 新建一个[IHttp]对象
     * 可通过[copyGlobalConfig]获得全局配置对象，以在全局配置的基础上二次配置
     * @return [IHttp]对象
     */
    fun newClient(builder: OkHttpClient.Builder): IHttp

    /**
     * 创建请求对象
     * @param baseUrl 基础的请求地址，可以在后续拼接
     * @return [IRequest]对象
     */
    fun newRequest(baseUrl: String): IRequest

    /**
     * 取消指定的http请求，包括
     * @param tag 请求任务对应的tag
     */
    fun cancel(tag: Any)

    /**
     * 取消所有http请求
     */
    fun cancelAll()
}