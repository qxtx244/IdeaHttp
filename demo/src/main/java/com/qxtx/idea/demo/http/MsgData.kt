package com.qxtx.idea.demo.http

import com.alibaba.fastjson.annotation.JSONCreator

/**
 * @author QXTX-WIN
 *
 * **Create Date** 2022/7/1 10:49
 *
 * **Description**
 *
 * data class通过moshi的反序列化测试类
 */
data class MsgData(
    var msg: String?
) {
    override fun toString(): String {
        return "MsgData(msg=$msg)"
    }
}