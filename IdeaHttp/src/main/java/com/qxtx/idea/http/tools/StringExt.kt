package com.qxtx.idea.http.tools

import android.text.BoringLayout
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

/**
 * 检查字符串中是否包含指定字符
 * @param ch 目标字符
 *
 * @return 检查结果
 */
fun String?.contains(ch: Char) = (this?.indexOf(ch) ?: -1) >= 0

/**
 * 检查字符串中是否包含指定子串
 * @param str 目标字串
 *
 * @return 检查结果
 */
fun String?.contains(str: String) = (this?.indexOf(str) ?: -1) >= 0

