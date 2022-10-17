package com.qxtx.idea.http.converter.fastjson

import com.qxtx.idea.http.converter.Converter
import okhttp3.RequestBody
import okhttp3.ResponseBody
import java.lang.reflect.Type

/**
 * @author QXTX-WIN
 *
 * **Create Date** 2022/5/17 14:38
 *
 * **Description**
 *
 * 使用FastJson进行反序列化的Converter工厂类
 * @property type 反序列化的目标类型对象，通过此对象，构造具体的反序列化器
 * @property V 数据类型
 * @constructor 构造反序列化工厂对象
 */
class FastjsonConverterFactory<V> @JvmOverloads constructor(
    private val type: Type? = null
): Converter.Factory<V>() {

    override fun type() = type

    /**
     * 解析ResponseBody对象，转换成指定的类型对象。如果[type]为null，则不生成反序列化器。
     *
     * @return [Converter]对象
     */
    override fun responseBodyConverter(): Converter<ResponseBody, V?>? {
        return type?.let { FastjsonResponseConverter(it) }
    }

    /**
     * 解析ResponseBody对象，转换成指定的类型对象。如果[type]为null，则视为转换成String类型
     *
     * @return [Converter]对象
     */
    override fun requestBodyConverter(): Converter<V, RequestBody> {
        return FastjsonRequestConverter()
    }
}