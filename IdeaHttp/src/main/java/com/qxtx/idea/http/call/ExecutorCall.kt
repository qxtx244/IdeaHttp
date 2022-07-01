package com.qxtx.idea.http.call

import com.qxtx.idea.http.callback.IHttpCallback
import com.qxtx.idea.http.response.Response
import okhttp3.Call
import okhttp3.Request
import okio.Timeout
import java.io.IOException
import java.util.concurrent.Executor


/**
 * @author QXTX-WIN
 *
 * **Create Date** 2022/5/17 9:46
 *
 * **Description**
 *
 * 线程切换的实现，okhttp3.Call的装饰者
 * @property call [ICall]对象
 * @property executor [Executor]对象，用于切换线程
 * @constructor 构造包装对象实例
 */
class ExecutorCall(
    private val call: ICall<Response>,
    private val executor: Executor
    ): ICall<Response> {

    @Throws(Exception::class)
    override fun execute(): Response {
        return call.execute()
    }

    override fun enqueue(responseCallback: IHttpCallback<Response>) {
        call.enqueue(object: IHttpCallback<Response> {
            override fun onResponse(call: Call, response: Response) {
                executor.execute {
                    responseCallback.onResponse(call, response)
                }
            }
            override fun onFailure(call: Call?, e: IOException?) {
                executor.execute {
                    responseCallback.onFailure(call, e)
                }
            }
        })
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