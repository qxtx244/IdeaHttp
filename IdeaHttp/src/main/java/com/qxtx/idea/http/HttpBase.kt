package com.qxtx.idea.http

import com.qxtx.idea.http.callback.HttpInterceptor
import com.qxtx.idea.http.verifier.SslSocketHelper
import okhttp3.Call
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import kotlin.math.max

/**
 * @author QXTX-WIN
 *
 * **Create Date** 2022/6/10 10:49
 *
 * **Description**
 *
 * 基于OkHttp的http请求库。默认支持http/https
 * 使用步骤：
 * 1. 创建实例： val http = HttpBase()
 * 2. 实例初始化：http.init(...)
 * 3. 开始http请求，注意同时请求的任务tag不能重复
 * 4. 取消请求任务：http.cancel(tag) 或 http.cancelAll()
 *
 * * 支持线程切换。通过[IRequest.setExecutor]方法设置目标线程对象，当请求返回结果时，
 *     结果将在目标线程中被回调。默认在主线程中回调结果；
 * * 支持请求结果反序列化。通过[IRequest.setResponseConverter]设置反序列化方案（目前只内置Fastjson反序列化实现），
 *     请求返回的结果将自动被反序列化；
 * * 如果为配置反序列化方案，请求返回的对象[com.qxtx.idea.http.response.Response]中body字段为String类型。
 * * 支持动态添加和移除拦截器
 * * 内部捕获同步请求异常，并记录到[com.qxtx.idea.http.response.Response.cause]中
 */
open class HttpBase: IHttp {

    companion object {
        internal val callMap = LinkedHashMap<Any, Call>()
    }

    private val interceptorList = ArrayList<HttpInterceptor>()
    private val networkInterceptorList = ArrayList<HttpInterceptor>()

    private var client: OkHttpClient? = null

    override fun init(callTimeoutMs: Long) {
        val timeoutMs = max(0, callTimeoutMs)
        val builder: OkHttpClient.Builder = newBuilder()
        if (timeoutMs > 0) {
            client = builder.callTimeout(timeoutMs, TimeUnit.MILLISECONDS).build()
        }
    }

    override fun init(connectTimeoutMs: Long, readTimeoutMs: Long, writeTimeoutMs: Long) {
        client = newBuilder()
            .connectTimeout(max(0, connectTimeoutMs).toLong(), TimeUnit.MILLISECONDS)
            .readTimeout(max(0, readTimeoutMs).toLong(), TimeUnit.MILLISECONDS)
            .writeTimeout(max(0, writeTimeoutMs).toLong(), TimeUnit.MILLISECONDS)
            .build()
    }

    override fun init(client: OkHttpClient?) {
        if (client != null) {
            this.client = client
        } else {
            this.client = newBuilder().build()
        }
    }

    override fun addInterceptor(interceptor: HttpInterceptor) {
        if (!interceptorList.contains(interceptor)) {
            interceptorList.add(interceptor)
        }
    }

    override fun removeInterceptor(interceptor: HttpInterceptor) {
        interceptorList.remove(interceptor)
    }

    override fun addNetworkInterceptor(interceptor: HttpInterceptor) {
        if (!networkInterceptorList.contains(interceptor)) {
            networkInterceptorList.add(interceptor)
        }
    }

    override fun removeNetworkInterceptor(interceptor: HttpInterceptor) {
        networkInterceptorList.remove(interceptor)
    }

    override fun globalConfig(builder: OkHttpClient.Builder) {
        client = builder.build()
    }

    override fun copyGlobalConfig(): OkHttpClient.Builder? {
        return client?.newBuilder()
    }

    override fun newClient(builder: OkHttpClient.Builder): IHttp {
        val http = HttpBase()
        http.client = builder.build()
        return http
    }

    override fun newRequest(baseUrl: String): IRequest {
        if (client == null) {
            TODO("使用前必须初始化！")
        }
        return BasicRequest(baseUrl, client!!)
    }

    override fun cancel(tag: Any) {
        synchronized(callMap) {
            callMap[tag]?.apply {
                cancel()
                callMap -= this
            }
        }
    }

    override fun cancelAll() {
        synchronized(callMap) {
            for (call in callMap.values) {
                call.cancel()
            }
            callMap.clear()
        }
    }

    private fun newBuilder(): OkHttpClient.Builder {
        return OkHttpClient.Builder()
            .hostnameVerifier(SslSocketHelper.hostnameVerifier)
            .sslSocketFactory(SslSocketHelper.sslContext!!.socketFactory, SslSocketHelper.trustManager!!)
            .addInterceptor(Interceptor { chain: Interceptor.Chain ->
                if (interceptorList.isEmpty()) {
                    chain.proceed(chain.request())
                } else {
                    var isIntercept: Boolean
                    val tempList = interceptorList.clone() as java.util.ArrayList<HttpInterceptor>
                    for (interceptor in tempList) {
                        isIntercept = interceptor.intercept(chain)
                        //当某个拦截器决定取消请求，则不再触发之后的拦截器回调
                        if (chain.call().isCanceled()) {
                            break
                        } else {
                            if (isIntercept) {
                                //如果外部决定拦截，则取消请求，并且不再往后传递
                                chain.call().cancel()
                                break
                            }
                        }
                    }
                    chain.proceed(chain.request())
                }
            })
            .addNetworkInterceptor(Interceptor { chain: Interceptor.Chain ->
                if (networkInterceptorList.isEmpty()) {
                    chain.proceed(chain.request())
                } else {
                    var isIntercept: Boolean
                    val tempList = networkInterceptorList.clone() as java.util.ArrayList<HttpInterceptor>
                    for (interceptor in tempList) {
                        isIntercept = interceptor.intercept(chain)
                        if (chain.call().isCanceled()) {
                            break
                        } else {
                            if (isIntercept) {
                                chain.call().cancel()
                                break
                            }
                        }
                    }
                    chain.proceed(chain.request())
                }
            })
    }
}