package com.qxtx.idea.http.call

import com.qxtx.idea.http.callback.IHttpCallback
import com.qxtx.idea.http.converter.Converter
import com.qxtx.idea.http.response.Response
import com.qxtx.idea.http.response.ResponseCode
import okhttp3.Call
import okhttp3.Callback
import okhttp3.ResponseBody
import java.io.IOException

/**
 * @author QXTX-WIN
 * Create Date 2022/10/15 15:46
 * Description 包装okhttp3.Call对象
 */
class CallWrapper(
    private val converter: Converter<ResponseBody, Any?>? = null,
    private val call: Call
): ICall {

    override fun cancel() = call.cancel()

    override fun clone() = call.clone()

    override fun enqueue(responseCallback: IHttpCallback) {
        call.enqueue(object: Callback {
            override fun onResponse(call: Call, response: okhttp3.Response) {
                responseCallback.onResponse(
                    call,
                    Response(rawResponse = response, body = response.body, converter = converter))
            }

            override fun onFailure(call: Call, e: IOException) {
                responseCallback.onFailure(call, e)
            }
        })
    }

    override fun execute(): Response {
        return try {
            call.execute().let {
                Response(rawResponse = it, body = it.body, converter = converter)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Response.errorResponse(ResponseCode.UNKNOWN, e)
        }
    }

    override fun isCanceled() = call.isCanceled()

    override fun isExecuted() = call.isExecuted()

    override fun request() = call.request()

    override fun timeout() = call.timeout()
}