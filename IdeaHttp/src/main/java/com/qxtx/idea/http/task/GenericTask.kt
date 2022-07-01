package com.qxtx.idea.http.task

import com.qxtx.idea.http.BasicRequest
import okhttp3.OkHttpClient

/**
 * @author QXTX-WIN
 *
 * **Create Date** 2022/6/6 14:48
 *
 * **Description**
 *
 * 通用的请求任务实现
 */
class GenericTask(
    method: String,
    request: BasicRequest,
    client: OkHttpClient
): BodyTask(method, request, client)