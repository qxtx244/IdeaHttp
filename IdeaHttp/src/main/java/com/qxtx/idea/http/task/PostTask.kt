package com.qxtx.idea.http.task

import com.qxtx.idea.http.RequestMethod
import com.qxtx.idea.http.BasicRequest
import okhttp3.OkHttpClient

/**
 * @author QXTX-WIN
 *
 * **Create Date** 2022/6/6 14:45
 *
 * **Description**
 *
 * http post请求任务
 */
class PostTask(
    request: BasicRequest,
    client: OkHttpClient
): BodyTask(RequestMethod.POST, request, client)