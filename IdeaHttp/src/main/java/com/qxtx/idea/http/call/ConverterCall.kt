package com.qxtx.idea.http.call

import com.qxtx.idea.http.callback.IHttpCallback
import com.qxtx.idea.http.converter.Converter
import com.qxtx.idea.http.response.Response
import com.qxtx.idea.http.response.ResponseCode
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.ResponseBody
import okio.Timeout
import java.io.IOException
import java.lang.Exception

/**
 * @author QXTX-WIN
 *
 * **Create Date** 2022/5/17 15:55
 *
 * **Description**
 *
 * okhttp3.Call对象的装饰者，用于请求结果的反序列化实现。
 * 当未配置反序列化策略时，默认返回String，即直接将请求数据以String格式返回
 *
 * @property call 原始的call对象，即okhttp3.Call对象
 * @property converter 反序列化器
 * @constructor 通过Call对象和反序列化对象两个参数，构建装饰对象实例
 */
class ConverterCall(
    private val call: Call,
    private val converter: Converter<ResponseBody, Any>?
): ICall<Response> {

    @Throws(Exception::class)
    override fun execute(): Response {
        val response = call.execute()

        val body = response.body
        if (!response.isSuccessful) {
            return Response(response, body?.string())
        }

        if (converter == null) {
            //2022/5/18 11:19 这可能存在大量数据时内存占用较大的问题
            return Response(response, body?.string())
        }

        val convertData = getConvertBody(body)
        return if (convertData is Exception) {
            Response.errorResponse(ResponseCode.CONVERTER_ERROR, convertData)
        } else {
            Response(response, convertData)
        }
    }

    override fun enqueue(responseCallback: IHttpCallback<Response>) {
        call.enqueue(object: Callback {
            override fun onResponse(call: Call, response: okhttp3.Response) {
                if (!response.isSuccessful) {
                    responseCallback.onResponse(call,
                        Response(response, null)
                    )
                    return
                }

                //2022/5/18 10:03 如果无法解析，则默认返回string类型的body
                val body = response.body
                val result = if (converter == null) {
                    Response(response, body?.string())
                } else {
                    val convertData = getConvertBody(body)
                    if (convertData is Exception) {
                        Response.errorResponse(ResponseCode.CONVERTER_ERROR, convertData)
                    } else {
                        Response(response, convertData)
                    }
                }
                responseCallback.onResponse(call, result)
            }

            override fun onFailure(call: Call, e: IOException) {
                responseCallback.onFailure(call, e)
            }
        })
    }

    /**
     * 获取反序列化后的body数据
     * @param body 原始body数据
     * @return 如果返回Exception对象，则说明反序列化过程中发生了异常。
     */
    private fun getConvertBody(body: ResponseBody?): Any? {
        return body?.let {
            try {
                converter!!.convert(it)
            } catch (e: Exception) {
                e.printStackTrace()
                e
            }
        }
    }

    override fun isExecuted(): Boolean {
        return call.isExecuted()
    }

    override fun cancel() {
        return call.cancel()
    }

    override fun isCanceled(): Boolean {
        return call.isCanceled()
    }

    override fun request(): Request {
        return call.request()
    }

    override fun timeout(): Timeout {
        return call.timeout()
    }

    override fun clone(): Call {
        return call.clone()
    }
}