package com.qxtx.idea.http.converter

import okhttp3.RequestBody
import okhttp3.ResponseBody
import java.io.IOException
import java.lang.reflect.Type

/**
 * @author QXTX-WIN
 *
 * **Create Date** 2022/5/17 14:15
 *
 * **Description**
 *
 * 反序列化接口
 * @param V 数值类型
 * @param R 处理后的对象类型
 */
interface Converter<V, R> {

    @Throws(Exception::class)
    fun convert(value: V): R

    /**
     * 抽象工厂类
     * @param V1 数值类型
     */
    abstract class Factory<T> {

        /**
         * 获取反序列化的目标类型
         * @return 类型对象
         */
        abstract fun type(): Type?

        /**
         * 获取反序列化对象
         * @return 反序列化器对象
         */
        abstract fun responseBodyConverter(): Converter<ResponseBody, T?>?

        abstract fun requestBodyConverter(): Converter<T, RequestBody>
    }
}