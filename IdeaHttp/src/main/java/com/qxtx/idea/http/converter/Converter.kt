package com.qxtx.idea.http.converter

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

    @Throws(IOException::class)
    fun convert(value: V): R

    //2022/6/13 11:06 V和V1，R和R1并没有任何关系，用不同名称来强调这个点
    /**
     * 抽象工厂类
     * @param V1 数值类型
     * @param R1 处理后的对象类型
     */
    abstract class Factory<V1, R1> {

        /**
         * 获取反序列化的目标类型
         * @return 类型对象
         */
        abstract fun type(): Type

        /**
         * 获取反序列化对象
         * @return 反序列化器对象
         */
        abstract fun responseBodyConverter(): Converter<V1, R1>

        //2022/5/17 16:28 当前设计不支持设置请求数据的converter
//        abstract fun requestBodyConverter(): Converter<Any, RequestBody>
    }
}