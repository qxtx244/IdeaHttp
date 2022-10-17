package com.qxtx.idea.http.converter.moshi

import com.qxtx.idea.http.ContentType
import com.qxtx.idea.http.converter.Converter
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.lang.Exception
import java.lang.RuntimeException
import kotlin.jvm.Throws

/**
 * @author QXTX-WORK
 *
 * **Create Date** 2022/10/17 10:14
 *
 * **Description**
 * 基于Moshi的序列化器实现
 *
 * @param R 目标数据对象
 */
class MoshiRequestConverter<R>: Converter<R, RequestBody> {

    @Throws(Exception::class)
    override fun convert(value: R): RequestBody {
        return value?.let {
            val json = MoshiHelper.toJsonString(it)
            json?.toRequestBody(ContentType.JSON.toMediaType())
        } ?: throw RuntimeException("反序列化失败")
    }
}