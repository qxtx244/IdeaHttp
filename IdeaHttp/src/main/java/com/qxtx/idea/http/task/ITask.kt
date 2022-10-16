package com.qxtx.idea.http.task

import com.qxtx.idea.http.R
import com.qxtx.idea.http.callback.IHttpCallback
import com.qxtx.idea.http.response.Response
import com.qxtx.idea.http.response.ResponseCode
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import java.io.IOException

/**
 * @author QXTX-WIN
 *
 * **Create Date** 2022/5/12 9:24
 *
 * **Description**
 *
 * 请求任务接口
 */
interface ITask {

    /**
     * 异步执行
     * @param tag 任务的标识对象。在需要时可以通过这个对象取消任务
     * @param callback [Response]对象
     */
    fun enqueue(tag: Any, callback: IHttpCallback)

    /**
     * 同步执行
     * @param tag 任务标识对象。在需要时可以通过这个对象取消任务
     * @return [Response]对象
     */
    fun execute(tag: Any): Response
}