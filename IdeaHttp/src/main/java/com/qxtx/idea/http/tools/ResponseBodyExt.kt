package com.qxtx.idea.http.tools

import androidx.annotation.IntRange
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.asResponseBody
import okio.Buffer

/**
 * 观察请求数据。此操作不会对请求数据产生影响，但要留意OOM的风险。最大允许观察1MB的数据
 *
 * @param byteCount 最大允许解析的数据大小，单位为byte
 * @return okhttp3.ResponseBody对象
 */
@JvmOverloads
fun ResponseBody.peekBody(@IntRange(from = 0L, to = 1024L * 1024) byteCount: Long = 1024L * 1024): ResponseBody? {
    return try {
        val peeked = source().peek()
        val buffer = Buffer()
        peeked.request(byteCount)
        buffer.write(peeked, minOf(byteCount, peeked.buffer.size))
        buffer.asResponseBody(contentType(), buffer.size)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}