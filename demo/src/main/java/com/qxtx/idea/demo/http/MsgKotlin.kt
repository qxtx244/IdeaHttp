package com.qxtx.idea.demo.http

import com.alibaba.fastjson.annotation.JSONCreator

/**
 * @author QXTX-WIN
 *
 * **Create Date** 2022/7/1 10:26
 *
 * **Description**
 * 在fastjson1.2.32下，对构造函数使用@JSONCreator注解，
 * 能使kotlin类能较好地被反序列化，避免找不到构造方法的异常
 */
class MsgKotlin {

    var msg: String? = null

    @JSONCreator constructor()

    constructor(msg: String?) {
        this.msg = msg
    }

    override fun toString(): String {
        return "Msg{" +
                "msg='" + msg + '\'' +
                '}'
    }
}