package com.qxtx.idea.http.task

import com.qxtx.idea.http.RequestMethod
import com.qxtx.idea.http.BasicRequest
import okhttp3.OkHttpClient

/**
 * @author QXTX-WIN
 *
 * **Create Date** 2022/6/6 14:46
 *
 * **Description**
 *
 * http put请求任务
 */
class PutTask(
    request: BasicRequest,
    client: OkHttpClient
): BodyTask(RequestMethod.PUT, request, client)