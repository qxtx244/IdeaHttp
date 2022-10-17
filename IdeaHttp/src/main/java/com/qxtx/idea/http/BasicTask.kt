package com.qxtx.idea.http

import androidx.annotation.CallSuper
import com.qxtx.idea.http.call.CallWrapper
import com.qxtx.idea.http.callback.IHttpCallback
import com.qxtx.idea.http.response.Response
import com.qxtx.idea.http.tools.forEach
import com.qxtx.idea.http.call.ExecutorCall
import com.qxtx.idea.http.call.ICall
import com.qxtx.idea.http.task.ITask
import com.qxtx.idea.http.tools.HttpLog
import okhttp3.*
import okhttp3.internal.http.HttpMethod
import okhttp3.internal.toImmutableList
import java.io.IOException
import java.lang.Exception
import java.lang.RuntimeException
import java.net.HttpCookie
import kotlin.collections.ArrayList

/**
 * @author QXTX-WIN
 *
 * **Create Date** 2022/5/12 14:10
 *
 * **Description**
 *
 * http请求任务的执行实现
 * @property requestMethod 请求方法，取值见[RequestMethod]
 * @property basicRequest [BasicRequest]对象
 * @property client 请求处理对象
 */
abstract class BasicTask(
    protected val requestMethod: String,
    protected val basicRequest: BasicRequest,
    protected val client: OkHttpClient
): ITask {

    companion object {
        const val CHECK_PASS = 0
        const val ERROR_MUST_NOT_BODY = -1
        const val ERROR_REQUIRE_BODY = -2
        const val ERROR_ = -3
        const val ERROR_REQUEST_WITH_PAYLOAD = -4
    }

    /** 请求数据的数据集，可存在多种不同的请求数据 */
    protected val requestBodys: MutableList<RequestBody> = ArrayList()

    @CallSuper
    override fun execute(tag: Any): Response {
        try {
            val response: Response? = try {
                newCall(tag)?.execute()
            } catch (e: Exception) {
                Response.errorResponse(cause = e)
            }

            checkCookie(response)

            return response?: Response.errorResponse()
        } finally {
            synchronized(HttpBase.callMap) {
                HttpBase.callMap -= tag
            }
        }
    }

    @CallSuper
    override fun enqueue(tag: Any, callback: IHttpCallback) {
        val call = newCall(tag)
        if (call == null) {
            callback.onFailure(null, null)
        } else {
            call.enqueue(object: IHttpCallback {
                override fun onFailure(call: Call?, e: IOException?) {
                    synchronized(HttpBase.callMap) {
                        HttpBase.callMap -= tag
                    }

                    callback.onFailure(call, e)
                }

                override fun onResponse(call: Call, response: Response) {
                    synchronized(HttpBase.callMap) {
                        HttpBase.callMap -= tag
                    }

                    checkCookie(response)
                    callback.onResponse(call, response)
                }
            })
        }
    }

    private fun newCall(tag: Any?): ICall? {
        val realUrl = appendUrlAndParams()

        //2022/6/7 14:24 这里可以像retrofit那样，自行组装Request对象，
        //  可以避开okhttp对method携带payload的检测

        val requestBuilder = Request.Builder().apply {
            val code = checkRequestValid()
            if (code < CHECK_PASS) {
                HttpLog.e("异常的请求，立即终止行为。错误码：$code")
                return null
            }

            if (tag != null && HttpBase.callMap.containsKey(tag)) {
                HttpLog.e("任务的tag已经存在，拒绝本次请求。tag=$tag")
                return null
            }

            tag(tag ?: Any())

            url(realUrl)

            //2022/8/5 16:02 检查是否存在cookie缓存，如果匹配到，则添加。
            // 注意，用户可能会显式设置“Cookie”请求头字段，这类数据的优先级最高，因此缓存的cookie数据必须在此之前完成
            val domain = HttpBase.parseDomain(realUrl)
            val cookies = basicRequest.http.getCookie(domain)

            var cookie = ""
            cookies?.toImmutableList()?.forEach {
                if (it.discard || it.hasExpired() || (it.secure && !realUrl.startsWith("https://"))) {
                    cookies.remove(it)
                } else {
                    if (cookie.isNotEmpty()) cookie += ";"

                    //拼装缓存的cookie
                    cookie += "${it.name}=${it.value}"
                }
            }
            if (cookie.isNotEmpty()) {
                HttpLog.d("自动添加匹配的cookie缓存：Cookie=$cookie")
                addHeader("Cookie", cookie)
            }

            val headers = basicRequest.headers.build()
            headers.forEach {
                HttpLog.d("header数据对：${it.first}:${it.second}")
                addHeader(it.first, it.second)
            }

            var requestBody: RequestBody? = null
            if (requestBodys.isNotEmpty()) {
                requestBody = if (requestBodys.size == 1) {
                    requestBodys[0]
                } else {
                    MultipartBody.Builder().apply {
                        requestBodys.forEach { addPart(it) }
                    }.build()
                }
            }

            when (requestMethod) {
                RequestMethod.GET -> get()
                RequestMethod.POST -> {
                    if (requestBody == null) {
                        HttpLog.w("post请求缺少请求数据！")
                        throw RuntimeException("不允许无请求数据的post请求！")
                    }
                    post(requestBody)
                }
                else -> method(requestMethod, requestBody)
            }
        }

        val rawCall = client.newCall(requestBuilder.build())

        //外部未指定tag时，不记录此次请求
        tag?.apply {
            synchronized(HttpBase.callMap) {
                HttpBase.callMap[this] = rawCall
            }
        }

        val converter = basicRequest.responseConverterFactory?.responseBodyConverter()
        val executorCall = ExecutorCall(
            call = CallWrapper(converter = converter, call = rawCall),
            executor = basicRequest.callbackExecutor)

        return executorCall
    }

    /**
     * 对请求的参数进行检查
     * OkHttp对一些请求方法会强制要求携带/不携带payload，因此在OkHttp抛出异常之前进行检查，
     * 以及时终止请求
     * @return 检查结果，只有为[CHECK_PASS]时表示检查通过
     */
    private fun checkRequestValid(): Int {
        var ret = CHECK_PASS
        if (HttpMethod.requiresRequestBody(requestMethod) && requestBodys.isEmpty()) {
            ret = ERROR_REQUEST_WITH_PAYLOAD
        } else if (!HttpMethod.permitsRequestBody(requestMethod) && requestBodys.isNotEmpty()) {
            ret = ERROR_MUST_NOT_BODY
        }
        return ret
    }

    private fun appendUrl(): String {
        basicRequest.apply {
            if (subUrl == null) return baseUrl

            var result = baseUrl
            //根据需要自动添加“/”分隔符
            if (!baseUrl.endsWith("/") && !subUrl!!.startsWith("/")) {
                result += "/"
            }

            return result + subUrl
        }
    }

    private fun appendUrlAndParams(): String {
        var url = appendUrl()
        basicRequest.apply {
            if (urlParams.isEmpty()) return url
            url += "?"
            urlParams.forEach { (k, v) ->
                url += if (url.endsWith("?")) "" else "&"
                url += "$k=$v"
            }
        }
        return url
    }

    /**
     * 检查服务端返回的cookie数据
     * @param response http请求结果
     */
    private fun checkCookie(response: Response?) {
        //检查服务端是否要缓存cookie
        if (response == null || !response.isSuccessful) {
            return
        }

        //忽略与请求地址的域不一致的set-cookie字段
        response.headers.forEach { pair ->
            //发现apache的HttpServer，会使用“Set-cookie”这种仅首字母大写的格式，需要兼容
            //“Set-Cookie2”字段可能会存在包含多个cookie数据的情况，目前不考虑这种字段
            if (pair.first == "Set-Cookie" || pair.first == "Set-cookie") {
                val requestDomain = HttpBase.parseDomain(response.request.url.toString())
                val newCookie = (HttpCookie.parse(pair.second)[0] as HttpCookie).apply {
                    if (domain == null) domain = requestDomain
                    if (path == null) path = "/"
                }

                if (newCookie.domain == requestDomain) {
                    basicRequest.http.setCookie(newCookie)
                }
            }
        }
    }
}