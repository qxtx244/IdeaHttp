package com.qxtx.idea.demo.http

import com.alibaba.fastjson.annotation.JSONCreator

/**
 * @author QXTX-WIN
 *
 * **Create Date** 2022/7/1 10:49
 *
 * **Description**
 *
 * kotlin的data class在fastjson下的反序列化测试
 */
data class MsgData(
    var msg: String?
) {

    @JSONCreator
    constructor(): this(null)

    override fun toString(): String {
        return "MsgData(msg=$msg)"
    }
}