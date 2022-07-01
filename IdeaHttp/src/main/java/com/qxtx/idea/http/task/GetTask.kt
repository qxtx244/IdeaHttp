package com.qxtx.idea.http.task

import com.qxtx.idea.http.RequestMethod
import com.qxtx.idea.http.BasicRequest
import com.qxtx.idea.http.BasicTask
import okhttp3.OkHttpClient

/**
 * @author QXTX-WIN
 *
 * **Create Date** 2022/5/12 11:33
 *
 * **Description**
 *
 * http get请求的实现
 */
class GetTask(
    request: BasicRequest,
    client: OkHttpClient
): BasicTask(RequestMethod.GET, request, client)