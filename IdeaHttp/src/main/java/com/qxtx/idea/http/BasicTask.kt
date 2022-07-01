package com.qxtx.idea.http

import com.qxtx.idea.http.callback.IHttpCallback
import com.qxtx.idea.http.response.Response
import com.qxtx.idea.http.tools.forEach
import com.qxtx.idea.http.call.ConverterCall
import com.qxtx.idea.http.call.ExecutorCall
import com.qxtx.idea.http.call.ICall
import com.qxtx.idea.http.task.ITask
import com.qxtx.idea.http.tools.HttpLog
import okhttp3.*
import okhttp3.internal.http.HttpMethod
import java.io.IOException
import java.lang.Exception

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

    override fun execute(tag: Any): Response {
        try {
            val response: Response? = try {
                newCall(tag)?.execute()
            } catch (e: Exception) {
                Response.errorResponse(cause = e)
            }
            return response?: Response.errorResponse()
        } finally {
            synchronized(HttpBase.callMap) {
                HttpBase.callMap -= tag
            }
        }
    }

    override fun enqueue(tag: Any, callback: IHttpCallback<Response>) {
        val call = newCall(tag)
        if (call == null) {
            callback.onFailure(null, null)
        } else {
            call.enqueue(object: IHttpCallback<Response> {
                override fun onFailure(call: Call?, e: IOException?) {
                    callback.onFailure(call, e)
                }

                override fun onResponse(call: Call, response: Response) {
                    callback.onResponse(call, response)
                }
            })
        }
    }

    private fun newCall(tag: Any?): ICall<Response>? {
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

            basicRequest.headers.forEach { (k, v) -> addHeader(k, v) }

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
                RequestMethod.POST -> post(requestBody!!)
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
        val convertCall = ConverterCall(rawCall, converter)
        val executorCall = ExecutorCall(convertCall, basicRequest.callbackExecutor)

        //装饰功能
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
            if (baseUrl.endsWith("/") && !subUrl!!.startsWith("/")) {
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
}