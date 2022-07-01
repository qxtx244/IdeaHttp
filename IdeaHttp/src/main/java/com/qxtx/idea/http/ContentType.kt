package com.qxtx.idea.http

/**
 * @author QXTX-WIN
 *
 * **Create Date** 2022/5/11 9:31
 *
 * **Description**
 *
 * http请求数据的Content-Type
 */
class ContentType {

    companion object {

        /** 未知格式的字节流 */
        const val OCTET_STREAM = "application/octet-stream"

        /** utf-8编码的json传输 */
        const val JSON = "application/json;charset=utf-8"

        /** 内容将会被使用url编码，解析时需要先进行url解码，否则可能无法正确获取到一些特殊内容（如中文字符） */
        const val X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded"

        /** 传输多项不同格式的请求数据时，okhttp默认使用的格式。和[FORM_DATA]可互相替换 */
        const val MIXED = "multipart/mixed"

        /** 通常用来传输文件，当然可以包含多项数据。和[MIXED]可互相替换 */
        const val FORM_DATA = "multipart/form-data"

        /** 纯文本传输 */
        const val TEXT = "text/plain;charset=utf-8"

        const val MARKDOWN = "text/x-markdown;charset=utf-8"

        const val XML = "application/xml"

        const val HTML = "text/html"

        const val CSS = "text/css"

        const val JAVA_SCRIPT = "text/javascript"

        const val MP4 = "video/mp4"

        const val JPEG = "image/jpeg"

        const val MP3 = "audio/mp3"
    }
}