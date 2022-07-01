package com.qxtx.idea.http.task

import com.qxtx.idea.http.ContentType
import com.qxtx.idea.http.tools.MultiPair
import okhttp3.FormBody
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

/**
 * @author QXTX-WIN
 *
 * **Create Date** 2022/6/6 11:16
 *
 * **Description**
 *
 * 请求体（payload of messages）接口
 */
interface IBodyTask: ITask {

    /**
     * 设置表单数据，Content-Type为[ContentType.X_WWW_FORM_URLENCODED]
     * @param body 请求数据
     * @return [ITask]对象
     */
    fun setBody(body: FormBody): ITask

    /**
     *  设置多项请求数据，Content-Type为[ContentType.MIXED]（与OkHttp保持一致）
     *  @param body 请求数据
     *  @return [ITask]对象
     */
    fun setBody(body: MultipartBody): ITask

    /**
     * 设置文本请求数据，并指定Content-type，使用[addBody]方法设置的请求数据将被丢弃
     * @param text 文本内容
     * @param contentType 请求头的Content-Type，取值参考[ContentType]
     * @return [ITask]对象
     */
    fun setBody(text: String, contentType: String): ITask

    /**
     * 从文件中获取请求内容，并指定Content-Type，使用[addBody]方法设置的请求数据将被丢弃
     * @param file 文件对象
     * @param contentType 请求头的Content-Type，取值参考[ContentType]
     * @return [ITask]对象
     */
    fun setBody(file: File, contentType: String): ITask

    /**
     * 设置byte数组的请求数据，并指定Content-Type，使用[addBody]方法设置的请求数据将被丢弃
     * @param contentType 请求头的Content-Type，取值参考[ContentType]
     * @return [ITask]对象
     */
    fun setBody(byteArray: ByteArray, contentType: String): ITask

    /**
     * 设置json字符串，Content-Type为[ContentType.JSON]，使用[addBody]方法设置的请求数据将被丢弃
     * @return [ITask]对象
     */
    fun setJsonBody(json: String): ITask

    /**
     * 设置表单数据，Content-Type为[ContentType.X_WWW_FORM_URLENCODED]，
     * 使用[addBody]方法设置的请求数据将被丢弃
     * @return [IBodyTask]对象
     */
    fun setFormBody(formData: MultiPair<String, Any>): ITask

    /**
     * 添加一项请求数据
     * @param body 请求体对象
     * @return [IBodyTask]对象
     */
    fun addBody(body: RequestBody): IBodyTask

    /**
     * 添加一项请求数据。字符串数据，可以是普通文本，json，html，markdown等等，根据Content-Type识别格式
     * 即[ContentType.TEXT]
     * @param text 文本数据
     * @param contentType 请求头Content-Type，取值参考[ContentType]
     * @return [IBodyTask]对象
     */
    fun addBody(text: String, contentType: String?): IBodyTask

    /**
     * 添加一项请求数据。字节数组
     * @param array 字节数组
     * @param contentType 请求头Content-Type，取值参考[ContentType]
     * @return [IBodyTask]对象
     */
    fun addBody(array: ByteArray, contentType: String?): IBodyTask

    /**
     * 添加一项请求数据，从文件中获取内容
     * @param file 文件对象
     * @param contentType 请求头Content-Type，取值参考[ContentType]
     * @return [IBodyTask]对象
     */
    fun addBody(file: File, contentType: String?): IBodyTask

    /**
     * 添加一项表单数据，Content-Type为[ContentType.X_WWW_FORM_URLENCODED]。
     * @param formData 键值对列表，作为表单项
     * @return [IBodyTask]对象
     */
    fun addBody(formData: MultiPair<String, Any>): IBodyTask

    /**
     * 清除已添加的请求数据
     * @return [IBodyTask]对象
     */
    fun clearBody(): IBodyTask
}