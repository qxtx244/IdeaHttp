package com.qxtx.idea.http.converter.moshi

import com.qxtx.idea.http.converter.Converter
import okhttp3.ResponseBody
import java.lang.reflect.Type

/**
 * @author QXTX-WIN
 * Create Date 2022/10/15 14:05
 * Description
 */
class MoshiConverter<R>(
    private val type: Type
): Converter<ResponseBody, R> {

    override fun convert(value: ResponseBody): R? {
        return null
    }
}