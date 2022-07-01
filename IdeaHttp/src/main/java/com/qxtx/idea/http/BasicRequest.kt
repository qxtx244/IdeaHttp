package com.qxtx.idea.http

import android.os.Handler
import android.os.Looper
import com.qxtx.idea.http.converter.Converter
import com.qxtx.idea.http.tools.MultiPair
import com.qxtx.idea.http.tools.urlDecode
import com.qxtx.idea.http.tools.urlEncode
import com.qxtx.idea.http.task.*
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import java.util.concurrent.Executor

/**
 * @author QXTX-WIN
 *
 * **Create Date** 2022/5/12 13:25
 *
 * **Description**
 *
 * 公共请求对象
 *
 * @param baseUrl 基础url，可以在后续流程中继续拼接
 * @param client 核心的http请求实现对象
 */
class BasicRequest(
    val baseUrl: String,
    val client: OkHttpClient
): IRequest {

    internal var responseConverterFactory: Converter.Factory<ResponseBody, Any>? = null

    /** 请求结果回调的executor，如果调用时存在主线程，则默认为执行在主线程 */
    internal var callbackExecutor: Executor = Executor { command ->
        Looper.getMainLooper()?.apply {
            Handler(this).post {
                command?.run()
            }
        }
    }

    /** url组成部分，如果非空，则和[baseUrl]拼接成完整的url */
    internal var subUrl: String? = null
    /** 请求头数据集（键值对） */
    internal val headers = MultiPair<String, String>()
    /** url参数的拼接数据集 */
    internal val urlParams = MultiPair<String, String>()

    override fun get(): ITask {
        return GetTask(this, client)
    }

    override fun head(): ITask {
        return HeadTask(this, client)
    }

    override fun post(): IBodyTask {
        return PostTask(this, client)
    }

    override fun put(): IBodyTask {
        return PutTask(this, client)
    }

    override fun patch(): IBodyTask {
        return PatchTask(this, client)
    }

    override fun requestMethod(method: String): IBodyTask {
        return GenericTask(method, this, client)
    }

    override fun setExecutor(executor: Executor): IRequest {
        this.callbackExecutor = executor
        return this
    }

    override fun setResponseConverter(factory: Converter.Factory<ResponseBody, Any>?): IRequest {
        responseConverterFactory = factory
        return this
    }

    override fun setSubUrl(uri: String): IRequest {
        subUrl = uri
        return this
    }

    override fun addHeader(key: String, value: String): IRequest {
        headers[key] = value
        return this
    }

    override fun addHeader(headers: MultiPair<String, String>): IRequest {
        this.headers += headers
        return this
    }

    override fun removeHeader(key: String) {
        headers -= key
    }

    override fun clearHeader() = headers.clear()

    override fun addUrlParam(key: String, value: String): IRequest {
        urlParams[key.urlEncode()] = value.urlDecode()
        return this
    }

    override fun addUrlParam(params: MultiPair<String, String>): IRequest {
        urlParams += params
        return this
    }

    override fun removeUrlParam(key: String) {
        urlParams -= key
    }

    override fun clearUrlParam() = urlParams.clear()
}