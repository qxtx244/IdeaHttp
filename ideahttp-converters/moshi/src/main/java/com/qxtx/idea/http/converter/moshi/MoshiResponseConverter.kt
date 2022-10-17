package com.qxtx.idea.http.converter.moshi

import androidx.core.util.lruCache
import com.qxtx.idea.http.converter.Converter
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import okhttp3.ResponseBody
import java.lang.Exception
import java.lang.reflect.Type
import kotlin.jvm.Throws

/**
 * @author QXTX-WIN
 * Create Date 2022/10/15 14:05
 * Description 基于Moshi的对请求数据反序列化实现
 */
class MoshiResponseConverter<R>(
    private val type: Type
): Converter<ResponseBody, R?> {

    override fun convert(value: ResponseBody): R? {
        return try {
            val source = value.source()
            val adapter: JsonAdapter<R> = MoshiHelper.adapter(type)
            adapter.fromJson(source)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}