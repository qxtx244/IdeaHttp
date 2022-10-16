package com.qxtx.idea.http.converter.fastjson

import com.alibaba.fastjson.JSON
import com.qxtx.idea.http.ContentType
import com.qxtx.idea.http.converter.Converter
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.lang.RuntimeException
import java.lang.reflect.Type
import kotlin.jvm.Throws

/**
 * 基于fastjson的序列化器
 *
 * @param V 数据类型
 */
class FastjsonRequestConverter<V>: Converter<V, RequestBody>{

    @Throws(Exception::class)
    override fun convert(value: V): RequestBody {
        val jsonStr = try {
            JSON.toJSONString(value).ifEmpty { null }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

        return jsonStr?.toRequestBody(ContentType.JSON.toMediaType()) ?: throw RuntimeException("Unable to convert")
    }
}