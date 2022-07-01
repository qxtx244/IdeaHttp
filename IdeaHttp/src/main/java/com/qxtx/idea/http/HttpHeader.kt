package com.qxtx.idea.http

/**
 * @author QXTX-WIN
 *
 * **Create Date** 2022/5/11 9:54
 *
 * **Description**
 *
 * http请求头的字段
 */
class HttpHeader {

    companion object {
        /**
         * 约定http请求数据的格式，非协议规定。
         * 开源库一般会自动添加，不需要手动设置
         * 对应值见[ContentType]
         */
        const val CONTENT_TYPE = "Content-Type"

        /** http请求数据的长度，开源库一般会自动添加，不需要手动设置 */
        const val CONTENT_LENGTH = "Content-Length"

        /** 请求携带的cookie。cookie之间用;隔开 */
        const val COOKIE = "Cookie"

        /** 授权证书 */
        const val AUTHORIZATION = "Authorization"

        /** 是否需要保持连接，HTTP1.1后默认为true */
        const val CONNECTION = "Connection"

        /** 可以接收的内容类型 */
        const val ACCEPT = "Accept"

        /** 可以接收的字符集 */
        const val ACCEPT_CHARSET = "Accept-Charset"

        /** 可以识别的语言 */
        const val ACCEPT_LANGUAGE = "Accept-Language"

        const val DATE = "Date"
        const val VIA = "Via"
        const val REFERER = "Referer"
        const val USER_AGENT = "User-Agent"
        const val FROM = "From"
        const val HOST = "Host"

        /**
         * 请求和响应的缓存机制，分为请求端和接收端。
         * 在请求中使用：
         * only-if-cached：告知服务器客户端仅使用缓存的内容（如果有），而不会向原服务器请求
         * no-cache：告知服务端不使用任何缓存，必须向原服务器发起请求
         * no-store：不保存任何缓存
         * max-age=xxx：告知服务器客户端希望接收一个存在时间不超过xxx秒的资源
         * max-state=xxx：告知服务器客户端愿意接收一个超过缓存时间xxx秒的资源
         * min-fresh=xxx：告知服务器客户端希望接收一个在xxx秒内被更新过的资源
         * no-transform：告知服务器客户端希望获取没有被转换过（如压缩）的资源
         * cache-extension：自定义的扩展值，服务器可以选择忽略掉
         * 在响应中使用：
         * must-revalidate：必须从原服务器上请求，而不使用缓存。失败时返回504
         * proxy-revalidate：类似于must-revalidate，但仅用于共享缓存（如代理）
         * only-if-cached：告知客户端仅使用缓存（如果有）
         * no-cache：告知客户端不使用任何缓存，必须向原服务器发起请求
         * no-store：不保存任何缓存。这个字段的优先级最高
         * max-age=xxx：告知客户端该资源的有效期为xxx秒
         * s-maxage：同max-age，但仅用于共享缓存（如代理）
         * cache-extension：扩展值，客户端可选择忽略
         * public：表明任何情况下都缓存该资源（即使是需要HTTP认证的资源）
         * private[=”field-name”]：表明返回报文中的全部或部分（field-name）仅开放给某些		用户（服务器指定的share-user）做缓存使用，其它用户不能缓存这些数据
         */
        const val CACHE_CONTROL = "Cache-Control"
    }
}