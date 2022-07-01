package com.qxtx.idea.http.response

/**
 * @author QXTX-WIN
 *
 * **Create Date** 2022/6/9 14:40
 *
 * **Description**
 *
 * 请求响应码
 * 扩展的请求响应码为6xx，仅用于本地构造的[Response]中，表示请求发生了异常
 */
class ResponseCode {

    companion object {

        /** 无法使用的请求。发生在请求的本地解析阶段，此时还没有进行缓存处理和网络访问 */
        const val BAD_REQUEST = 400

        //601~609 库自身的功能异常
        /** 反序列化器异常 */
        const val CONVERTER_ERROR = 601

        //621~629 网络相关的异常
        /** 未明确的网络错误 */
        const val NETWORK_ERROR = 621

        /** 无法请求到目标服务器或目标服务器不存在(404) */
        const val NOT_FOUND = 622

        /** 无法到达的host，通常属于无网络状态 */
        const val UNKNOWN_HOST = 623

        /** 发生Connect Reset异常 */
        const val CONNECT_RESET = 624

        /** socket连接超时 */
        const val SOCKET_TIMEOUT = 625

        /** 未知错误 */
        const val UNKNOWN = 699
    }
}