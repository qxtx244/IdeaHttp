package com.qxtx.idea.http.task

import com.qxtx.idea.http.RequestMethod
import com.qxtx.idea.http.BasicRequest
import com.qxtx.idea.http.BasicTask
import okhttp3.OkHttpClient

/**
 * @author QXTX-WIN
 *
 * **Create Date** 2022/6/2 9:25
 *
 * **Description**
 *
 * http head请求的实现
 */
class HeadTask(
    request: BasicRequest,
    client: OkHttpClient
): BasicTask(RequestMethod.HEAD, request, client)