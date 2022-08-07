package com.qxtx.idea.http.tools

import okhttp3.Headers

/**
 * @author QXTX-WIN
 *
 * **Create Date** 2022/8/6 0:08
 *
 * **Description**
 *
 * 扩展函数
 */

operator fun Headers.Builder.minusAssign(key: String) {
    removeAll(key)
}