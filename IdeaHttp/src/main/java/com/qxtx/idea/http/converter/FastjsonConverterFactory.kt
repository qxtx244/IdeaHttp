package com.qxtx.idea.http.converter

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
 * @constructor 构造反序列化工厂对象
 */
class FastjsonConverterFactory(
    val type: Type
): Converter.Factory<ResponseBody, Any>() {

    override fun type() = type

    override fun responseBodyConverter(): Converter<ResponseBody, Any> {
        return FastjsonConverter(type)
    }
}