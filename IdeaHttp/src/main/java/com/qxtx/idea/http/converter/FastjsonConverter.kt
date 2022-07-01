package com.qxtx.idea.http.converter

import com.alibaba.fastjson.JSON
import okhttp3.ResponseBody
import java.io.IOException
import java.lang.reflect.Type
import java.nio.charset.Charset
import kotlin.jvm.Throws

/**
 * @author QXTX-WIN
 *
 * **Create Date** 2022/5/17 15:23
 *
 * **Description**
 *
 * 基于fastjson的反序列化器，将okhttp3.ResponseBody反序列化成指定的类型
 *
 * @param R 反序列化后的对象类型
 * @property type 反序列化的目标类型
 * @constructor 通过传入的类型对象，构造一个反序列化器
 */
class FastjsonConverter<R>(
    private val type: Type
): Converter<ResponseBody, R> {

    @Throws(IOException::class)
    override fun convert(value: ResponseBody): R {
        value.use {
            //2022/5/17 16:23 用字节流的方式应对大数据
            val stream = it.byteStream()
            val result: R = JSON.parseObject(stream, Charset.defaultCharset(), type)
            return result
        }
    }
}