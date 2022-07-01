package com.qxtx.idea.http

/**
 * @author QXTX-WIN
 *
 * **Create Date** 2022/5/12 14:21
 *
 * **Description**
 *
 * http的请求方法token。
 * 请求方法可存在三种属性：安全（safe）、幂等（idempotent）、可缓存（cacheable）
 * * 安全：安全请求应该是语义为只读性质，它们不会对目标资源造成影响。如：GET，HEAD，OPTIONS，TRACE
 * * 幂等：这种请求无论重复多少次，都会得到一致的结果，只存在于用户请求种。这个可以用在请求失败做重试处理的场景。
 *   安全请求也是幂等的。如：PUT，DELETE，和安全请求
 * * 可缓存：这种请求的响应结果可以被缓存下来。一般来说，不依赖于当前或需要授权响应的安全请求方法都是可缓存的。
 *   如：GET，HEAD，POST
 */
class RequestMethod {

    companion object {
        /**
         * http服务端必须支持的请求.
         * 不应携带请求体（但没明确规定不能带请求体）
         */
        const val GET = "GET"
        /**
         * 与GET请求类似，http服务端必须支持的请求
         * 无论是服务端还是客户端，都只应传输状态行和请求头（但没明确规定不能带请求体）
         * 服务端还应该返回一致的请求头字段
         */
        const val HEAD = "HEAD"

        /**
         * 用于提交一些数据，如发布资源、在服务器上创建资源或往已经存在的资源中拼接新数据
         * 一些例外的响应状态码：206（部分内容），304（未修改），416（范围不满足），201（成功【创建】了一个或多个资源）
         */
        const val POST = "POST"

        /**
         * 幂等的请求类型
         * 这种请求的特点是”创建“或“全部替换”（服务器上的）目标资源
         */
        const val PUT = "PUT"
        const val PATCH = "PATCH"

        const val DELETE = "DELETE"

        const val CONNECT = "CONNECT"

        const val OPTIONS = "OPTIONS"

        const val TRACE = "TRACE"
    }
}