package com.qxtx.idea.http.callback

import okhttp3.Call
import java.io.IOException

/**
 * @author QXTX-WIN
 *
 * **Create Date** 2022/5/17 11:15
 *
 * **Description**
 *
 * http结果回调接口
 * @param T 请求结果类型
 */
interface IHttpCallback<T> {

    /**
     * 请求失败回调
     * @param call 请求对象
     * @param e 请求过程中抛出的异常
     */
    fun onFailure(call: Call?, e: IOException?)

    /**
     * 当请求得到结果时，回调此方法，但得到的数据不一定是有效的
     * @param call 请求对象
     * @param response 请求结果对象
     */
    fun onResponse(call: Call, response: T)
}