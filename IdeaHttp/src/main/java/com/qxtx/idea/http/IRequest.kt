package com.qxtx.idea.http

import com.qxtx.idea.http.converter.Converter
import com.qxtx.idea.http.task.IBodyTask
import com.qxtx.idea.http.task.ITask
import com.qxtx.idea.http.tools.MultiPair
import okhttp3.ResponseBody
import java.util.concurrent.Executor

/**
 * @author QXTX-WIN
 *
 * **Create Date** 2022/5/11 17:40
 *
 * **Description**
 *
 * 基本的http请求接口
 */
interface IRequest {

    /**
     * 创建http get请求对象
     * @return [ITask]对象
     */
    fun get(): ITask

    /**
     * 创建http head请求对象
     * @return [ITask]对象
     */
    fun head(): ITask

    /**
     * 创建http post请求对象
     * @return [IBodyTask]对象
     */
    fun post(): IBodyTask

    /**
     * 创建http put请求对象，OkHttp限制这种请求消息必须携带payload
     * @return [IBodyTask]对象
     */
    fun put(): IBodyTask

    /**
     * 创建http patch请求对象，OkHttp限制这种请求消息必须携带payload
     * @return [IBodyTask]对象
     */
    fun patch(): IBodyTask

    /**
     * 创建http 自定义请求方法对象
     * @param method 取值见[RequestMethod]
     */
    fun requestMethod(method: String): IBodyTask

    /**
     * 设置结果回调的executor，用于线程切换，设置后持久生效
     * @param executor [Executor]对象
     */
    fun setExecutor(executor: Executor): IRequest

    /**
     * 设置请求结果数据的反序列化器
     * @param factory Converter的工厂对象，用于生成[Converter]对象
     */
    fun setResponseConverter(factory: Converter.Factory<ResponseBody, Any>?): IRequest

    /**
     * 与baseUrl拼接得到完整的url地址，每次设置将覆盖上一次的设置
     * @param uri 拼接的uri
     * @return [IRequest]对象
     */
    fun setSubUrl(uri: String): IRequest

    /**
     * 添加请求头
     * @param key 索引
     * @param value 值
     * @return [IRequest]对象
     */
    fun addHeader(key: String, value: String): IRequest

    /**
     * 移除指定的请求头
     * @param key 目标请求头索引
     */
    fun removeHeader(key: String)

    /**
     * 清除请求头数据
     */
    fun clearHeader()

    /**
     * 添加url参数，这将会将键值对拼接到url尾部
     * @param key 索引
     * @param value 值
     * @return [IRequest]对象
     */
    fun addUrlParam(key: String, value: String): IRequest

    /**
     * 添加一个或多个url参数，这将会将键值对拼接到url尾部
     * @param params 包含一个或多个url参数
     * @return [IRequest]对象
     */
    fun addUrlParam(params: MultiPair<String, String>): IRequest

    /**
     * 移除url参数
     * @param key 目标url参数的索引
     */
    fun removeUrlParam(key: String)

    /**
     * 清除url参数
     */
    fun clearUrlParam()
}