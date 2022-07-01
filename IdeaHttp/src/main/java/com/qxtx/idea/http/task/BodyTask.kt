package com.qxtx.idea.http.task

import com.qxtx.idea.http.ContentType
import com.qxtx.idea.http.tools.MultiPair
import com.qxtx.idea.http.tools.forEach
import com.qxtx.idea.http.BasicTask
import com.qxtx.idea.http.BasicRequest
import com.qxtx.idea.http.tools.HttpLog
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

/**
 * @author QXTX-WIN
 *
 * **Create Date** 2022/6/6 13:50
 *
 * **Description**
 *
 * http 可携带payload的请求实现。注意，在调用多次addBody(...)方法时，可能会导致产生多项不同类型的请求数据，
 * 则变更为使用[MultipartBody]来装载请求数据
 */
open class BodyTask(
    requestMethod: String,
    basicRequest: BasicRequest,
    client: OkHttpClient
): IBodyTask, BasicTask(requestMethod, basicRequest, client) {

    override fun setBody(body: FormBody): ITask = apply {
        requestBodys.clear()
        requestBodys.add(body)
    }

    override fun setBody(body: MultipartBody): ITask = apply {
        requestBodys.clear()
        requestBodys.add(body)
    }

    override fun setBody(text: String, contentType: String): ITask = apply {
        requestBodys.clear()
        requestBodys.add(text.toRequestBody(parseContentType(contentType)))
    }

    override fun setBody(file: File, contentType: String): ITask = apply {
        if (!file.exists() || !file.canRead()) {
            HttpLog.e("待上传的文件不存在！")
            return@apply
        }

        requestBodys.clear()
        requestBodys.add(file.asRequestBody(parseContentType(contentType)))
    }

    override fun setBody(byteArray: ByteArray, contentType: String): ITask = apply {
        requestBodys.clear()
        requestBodys.add(byteArray.toRequestBody(parseContentType(contentType)))
    }

    override fun setJsonBody(json: String): ITask = apply {
        requestBodys.clear()
        requestBodys.add(json.toRequestBody(ContentType.JSON.toMediaType()))
    }

    override fun setFormBody(formData: MultiPair<String, Any>): ITask = apply {
        requestBodys.clear()
        val builder = FormBody.Builder()
        formData.forEach { (k,v) -> builder.add(k, v.toString()) }
        requestBodys.add(builder.build())
    }

    override fun addBody(body: RequestBody): IBodyTask = apply {
        requestBodys.add(body)
    }

    override fun addBody(text: String, contentType: String?): IBodyTask = apply {
        val body = text.toRequestBody(contentType?.toMediaType())
        requestBodys.add(body)
    }

    override fun addBody(array: ByteArray, contentType: String?): IBodyTask = apply {
        val body = array.toRequestBody(contentType?.toMediaType())
        requestBodys.add(body)
    }

    override fun addBody(file: File, contentType: String?): IBodyTask = apply {
        val body = file.asRequestBody(contentType?.toMediaType())
        requestBodys.add(body)
    }

    override fun addBody(formData: MultiPair<String, Any>): IBodyTask = apply {
        if (!formData.isEmpty()) {
            val body = FormBody.Builder().also {
                formData.forEach { (k, v) -> it.add(k, v.toString()) }
            }.build()
            requestBodys.add(body)
        }
    }

    override fun clearBody(): IBodyTask = apply {
        requestBodys.clear()
    }

    private fun parseContentType(contentType: String): MediaType? {
        return try {
            contentType.toMediaType()
        } catch (e: Exception) {
            null
        }
    }
}