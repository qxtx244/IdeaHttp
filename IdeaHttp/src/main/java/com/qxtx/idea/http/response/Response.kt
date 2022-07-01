package com.qxtx.idea.http.response

import okhttp3.Protocol
import okhttp3.Request
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * @author QXTX-WIN
 *
 * **Create Date** 2022/5/18 15:27
 *
 * **Description**
 *
 * http返回的响应结果，okhttp3.Response的包装类
 */
class Response(
    rawResponse: okhttp3.Response,
    body: Any?,
    cause: Throwable? = null
) {
    companion object {
        /**
         * 生成一个表示失败的本地请求结果[Response]对象
         * @param cause 异常对象，如果不为null，则说明请求过程中发生了异常
         * @return [Response]对象
         */
        @JvmStatic
        @JvmOverloads
        fun errorResponse(code: Int? = null, cause: Throwable? = null): Response {
            return Response(errorRawResponse(code, cause), null, cause)
        }

        /**
         * 生成一个表示失败的本地请求结果okhttp3.Response对象
         * @param cause 异常对象，如果不为null，则说明请求过程中发生了异常，需要手动构造一个本地请求响应对象以保持返回的一致性。
         * @return okhttp3.Response对象
         */
        @JvmStatic
        @JvmOverloads
        fun errorRawResponse(code: Int? = null, cause: Throwable? = null) = okhttp3.Response.Builder().let {
            //如果传进来的code不为null，直接使用code，否则根据cause判断实际的错误码，如果cause也为null，则错误码为BAD_REQUEST
            val realCode = code
                ?: (cause?.let { th ->
                    when (th) {
                        is SocketException -> {
                            ResponseCode.CONNECT_RESET
                        }
                        is UnknownHostException -> {
                            ResponseCode.UNKNOWN_HOST
                        }
                        is SocketTimeoutException -> {
                            ResponseCode.SOCKET_TIMEOUT
                        }
                        else -> {
                            //未知网络异常
                            if (th is Exception
                                && th::class.java.name.startsWith("java.net")) {
                                ResponseCode.NETWORK_ERROR
                            } else {
                                ResponseCode.UNKNOWN
                            }
                        }
                    }
                } ?: ResponseCode.BAD_REQUEST)

            val message: String = cause?.localizedMessage ?: "Unknown error."

            val url = cause?.let { _ ->
                null
            } ?: "http://localhost/"

            it.code(realCode)
                .message(message)
                .protocol(Protocol.HTTP_1_1)
                .request(Request.Builder().url(url).build())
                .build()
        }
    }

    /** 当发生异常时，记录异常信息 */
    @get:JvmName("cause") var cause = cause

    /** 请求返回的实体数据 */
    @get:JvmName("body") var body = body

    /** 请求响应码 */
    @get:JvmName("code") var code = rawResponse.code

    @get:JvmName("message") var message = rawResponse.message

    @get:JvmName("headers") var headers = rawResponse.headers

    @get:JvmName("isSuccessful") var isSuccessful = rawResponse.isSuccessful

    @get:JvmName("request") var request = rawResponse.request

    @get:JvmName("protocol") var protocol = rawResponse.protocol

    @get:JvmName("networkResponse") val networkResponse = rawResponse.networkResponse

    @get:JvmName("cacheResponse") val cacheResponse = rawResponse.cacheResponse

}