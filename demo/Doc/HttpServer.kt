package com.qxtx.idea.demo.http

import com.sun.net.httpserver.HttpServer
import java.io.OutputStream
import java.net.InetSocketAddress
import java.nio.charset.Charset
import java.util.concurrent.Executors
import kotlin.concurrent.thread

val pool = Executors.newFixedThreadPool(1)

/**
 * http 服务端实现
 * com.sun.net.httpserver.HttpServer需要使用jdk corretto-11.0.14.1，推荐在idea中运行
 */
fun main() {
    val server = HttpServer.create(InetSocketAddress("127.0.0.1",  12346), 0)
    server.executor = Executors.newCachedThreadPool()
    server.createContext("/httpserver") {
        pool.execute {
            val serverAddr = it.localAddress
            val remoteAddr = it.remoteAddress
            val requestUrl = serverAddr.toString() + it.requestURI
            val requestMethod = it.requestMethod
            val requestHeaders = it.requestHeaders.toMap()
            val reqBodyStream = it.requestBody

            var size = 0
            val array = ByteArray(4096)
            while (true) {
                val readLen = reqBodyStream.read(array)
                if (readLen < 4096) {
                    break
                }
                println("接收大小：$readLen")
                size += readLen
            }
            println("接收的请求体大小：$size")

            val reqBody: String?
            if (size > 1024) {
                println("请求数据太大， 只返回提示消息")
                reqBody = "大数据请求"
            } else {
                reqBody = if (size > 0) String(array, 0, size) else null
            }

            var msg =
                    "\naddr[$remoteAddr]" +
                    "\nurl[$requestUrl]" +
                    "\nmethod[$requestMethod]" +
                    "\nheaders[$requestHeaders]" +
                    "\nbody[$reqBody]"
            println("读取请求数据完成。time=${System.currentTimeMillis()}, 请求数据: \n$msg")

            msg ="{\"msg\":\"body is $reqBody\"}"
            val respBody = msg.toByteArray(Charset.defaultCharset())
            try {
                it.sendResponseHeaders(200, respBody.size.toLong())
                val os = it.responseBody
                println("write: $msg, time=${System.currentTimeMillis()}")
                os.write(respBody)
                os.flush()
                os.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            it.close()
        }
    }
    println("http server starting... ${server.address}/httpserver")
    server.start()
}