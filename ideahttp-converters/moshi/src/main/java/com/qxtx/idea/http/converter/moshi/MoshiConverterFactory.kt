package com.qxtx.idea.http.converter.moshi

import com.qxtx.idea.http.converter.Converter
import okhttp3.RequestBody
import okhttp3.ResponseBody
import java.lang.reflect.Type

/**
 * @author QXTX-WIN
 *
 * **Create Date** 2022/10/17 13:41
 *
 * **Description**
 * 使用Moshi进行反序列化的Converter工厂类
 * @property type 反序列化的目标类型对象，通过此对象，构造具体的反序列化器
 * @property V 数据类型
 * @constructor 构造反序列化工厂对象
 */
class MoshiConverterFactory<V> @JvmOverloads constructor(
    private val type: Type? = null
): Converter.Factory<V>() {

    override fun type() = type

    override fun responseBodyConverter(): Converter<ResponseBody, V?>? {
        return type?.let { MoshiResponseConverter(it) }
    }

    override fun requestBodyConverter(): Converter<V, RequestBody> {
        return MoshiRequestConverter()
    }
}