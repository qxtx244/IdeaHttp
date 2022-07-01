package com.qxtx.idea.http.task

import com.qxtx.idea.http.RequestMethod
import com.qxtx.idea.http.BasicRequest
import okhttp3.OkHttpClient

/**
 * @author QXTX-WIN
 *
 * **Create Date** 2022/6/6 14:47
 *
 * **Description**
 *
 * http patch请求任务
 */
class PatchTask(
    request: BasicRequest,
    client: OkHttpClient
): BodyTask(RequestMethod.PATCH, request, client)