package com.qxtx.idea.http.interceptor

import okhttp3.Interceptor
import okhttp3.Response

/**
 * @author QXTX-WIN
 *
 * **Create Date** 2022/6/8 17:19
 *
 * **Description**
 *
 * 拦截器接口
 */
interface HttpInterceptor {

    /**
     * @param chain 责任链中的请求封装对象
     * @return true为拦截，不再往后传递，false为不拦截
     */
    fun intercept(chain: Interceptor.Chain): Boolean
}