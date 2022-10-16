package com.qxtx.idea.http.call

import com.qxtx.idea.http.R
import com.qxtx.idea.http.callback.IHttpCallback
import com.qxtx.idea.http.response.Response
import okhttp3.Call
import okhttp3.Request
import okio.Timeout

/**
 * @author QXTX-WIN
 *
 * **Create Date** 2022/5/17 16:55
 *
 * **Description**
 */
interface ICall {

    /**
     * 取消请求。属于okhttp3.Call的装饰方法。
     */
    fun cancel()

    /**
     * 克隆请求对象。属于okhttp3.Call的装饰方法。
     * @return okhttp3.Call对象
     */
    fun clone(): Call

    /**
     * 异步请求
     * @param responseCallback 请求结果的回调对象
     */
    fun enqueue(responseCallback: IHttpCallback)

    /**
     * 同步请求。属于okhttp3.Call的装饰方法。
     * @return 返回的数据类型根据反序列化配置决定。如果未配置反序列化方案，则根据具体实现返回其默认类型
     */
    fun execute(): Response

    /**
     * 属于okhttp3.Call的装饰方法。
     * @return 请求是否已经处于关闭状态
     */
    fun isCanceled(): Boolean

    /**
     * 属于okhttp3.Call的装饰方法。
     * @return
     */
    fun isExecuted(): Boolean

    /**
     * 属于okhttp3.Call的装饰方法。
     * @return
     */
    fun request(): Request

    /**
     * 属于okhttp3.Call的装饰方法。
     * @return
     */
    fun timeout(): Timeout
}