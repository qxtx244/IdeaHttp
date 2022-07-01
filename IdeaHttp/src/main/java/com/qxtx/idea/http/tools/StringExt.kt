package com.qxtx.idea.http.tools

import java.net.URLDecoder
import java.net.URLEncoder

/*
 * @author QXTX-WIN
 *
 * **Create Date** 2022/5/13 11:12
 *
 * **Description**
 *
 * String类的扩展函数
 */

/**
 * 对字符串进行url编码，默认使用utf-8
 * @param charset 字符编码格式
 * @return url编码后的字符串
 */
@JvmOverloads
fun String?.urlEncode(charset: String = "utf-8"): String = URLEncoder.encode(this, charset)

/**
 * 对字符串进行url解码，默认使用utf-8
 * @param charset 字符编码格式
 * @return url解码后的字符串
 */
@JvmOverloads
fun String?.urlDecode(charset: String = "utf-8"): String = URLDecoder.decode(this, charset)

