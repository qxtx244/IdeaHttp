package com.qxtx.idea.http.response

import androidx.annotation.IntRange
import com.qxtx.idea.http.R
import com.qxtx.idea.http.converter.Converter
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.asResponseBody
import okio.Buffer
import okio.ByteString
import java.io.InputStream
import java.lang.RuntimeException
import java.lang.reflect.Type
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.math.max

/**
 * @author QXTX-WIN
 *
 * **Create Date** 2022/5/18 15:27
 *
 * **Description**
 *
 * http返回的响应结果，okhttp3.Response的包装类
 */

/**
 * 请求结果的包装对象
 *
 * @property body 请求返回的实体数据
 * @property cause 异常信息
 * @property mConverter 反序列化器
 * @constructor
 *
 * @param rawResponse
 */
class Response @JvmOverloads constructor(
    rawResponse: okhttp3.Response,
    private var body: ResponseBody?,
    @get:JvmName("cause") var cause: Throwable? = null,
    private var converter: Converter<ResponseBody, *>? = null
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

    /**
     * 获取请求数据的大小。如果无法获取，则返回-1
     *
     * @return 请求数据的大小，单位为字节
     */
    fun getDataLength(): Long {
        return body?.contentLength() ?: -1L
    }

    /**
     * 观察请求数据。此操作不会对请求数据产生影响，但会增加内存负担。
     * 最大允许处理的请求数据大小为1MB。预设限制为1MB
     *
     * @return  获取请求数据对象的拷贝。当无请求数据时，返回null
     */
    fun peekBody(@IntRange(from = 0L, to = 1024L * 1024) byteCount: Long = 1024L * 1024): ResponseBody? {
        body?.apply {
            val peeked = source().peek()
            val buffer = Buffer()
            peeked.request(byteCount)
            buffer.write(peeked, minOf(byteCount, peeked.buffer.size))
            return buffer.asResponseBody(contentType(), buffer.size)
        }
        return null
    }

    /**
     * 获取请求数据的字节流。大数据的最佳做法。
     *
     * @return 字节流对象
     *
     * @see [stringBody]
     * @see [streamBody]
     * @see [rawBody]
     * @see [parseBody]
     */
    fun streamBody(): InputStream? {
        return body?.byteStream()
    }

    /**
     * 获取字符串形式的请求数据。这可能会因请求数据过大导致OOM。最佳的做法是使用try-catch捕获方法可能抛出的异常。
     * 此方法仅允许处理1MB以内的请求数据。如果需要处理更大的数据，应使用[streamBody]方法。
     *
     * @param maxSize 数据的最大解析大小，单位为byte。预设为1MB。
     * @return 请求数据字符串。如果请求数据超过此大小，则返回最大解析长度的数据。
     */
    @JvmOverloads
    fun stringBody(@IntRange(from = 0L, to = 1024L * 1024) maxSize: Long = 1024L * 1024): String? {
        var result: String? = null
            body?.apply {
            result = if (contentLength() > maxSize) {
                val buffer = ByteArray(maxSize.toInt())
                val len = byteStream().read(buffer)
                if (len < 0) return@apply

                val array = ByteArray(len)
                System.arraycopy(buffer, 0, array, 0, len)
                array.contentToString()
            } else {
                string()
            }
            body = null
        }

        return result
    }

    /**
     * 获取未被反序列化的ResponseBody
     * @return okhttp3.ResponseBody对象
     */
    fun rawBody() = body

    /**
     * 获取请求数据。
     * 注意，如果请求数据过大，可能会造成OOM。
     *
     * @param R 反序列化的目标类型（返回的对象类型)
     * @param converter 反序列化器。如果为null，则会使用[mConverter]
     * @param defValue 如果反序列化失败或者无法匹配返回类型，返回的预设值
     * @return 获取结果。可能返回null
     */
    @JvmOverloads
    fun <R> parseBody(fixConverter: Converter<ResponseBody, R>? = null, defValue: R? = null): R? {
        val useConverter = fixConverter ?: converter
        val result = try {
            if (useConverter == null) {
                body?.contentLength()
                body as R?
            } else {
                body?.let {
                    useConverter.convert(it) as R?
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            defValue
        }
        body = null
        return result
    }

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