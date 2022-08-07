package com.qxtx.enjoystudy

import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress
import java.nio.charset.Charset
import kotlin.concurrent.thread

/**
 * @author QXTX-WIN
 *
 * **Create Date** 2022/4/30 18:14
 *
 * **Description**
 *
 * http服务端实现，用于测试IdeaHttp对Cookie的自动缓存和应用能力，或者get/post等一般请求
 */
fun main() {
    val server = HttpServer.create(InetSocketAddress("192.168.1.6", 12346), 0)

    val testCookie1 = "test-cookie=xxxxxx;Path=/;Expires=Sun, 07 Aug 2023 11:13:00 GMT"
    val testCookie2 = "test-cookie2=yyy;Path=/;Expires=Wed, 09 Jun 2024 10:18:14 GMT"
    server.createContext("/login") { p ->
        println("登录请求到来！")

        thread {
            var body: String? = null
            val buffer = ByteArray(1024)
            p.requestBody?.apply {
                read(buffer).apply {
                    if (this > 0) {
                        body = String(buffer, 0, this)
                    }
                }
            }
            body = body ?: "Empty body..."
            val resp = "server url=${p.localAddress}${p.requestURI}" +
                    "\nclient url=${p.remoteAddress}" +
                    "\nmethod=${p.requestMethod}" +
                    "\nheaders=${p.requestHeaders.toMap()}" +
                    "\nbody=$body"
            println("请求数据：\n$resp\n请求数据长度=${resp.length}")

            val len = resp.toByteArray(Charset.defaultCharset()).size
            p.responseHeaders.add("Set-Cookie", testCookie1)
            p.sendResponseHeaders(200, len.toLong())
            p.responseBody.write(resp.toByteArray(Charset.defaultCharset()))
            p.close()
        }
    }

    server.createContext("/logout") { p ->
        println("登出请求到来！")

        thread {
            var body: String? = null
            val buffer = ByteArray(1024)
            p.requestBody?.apply {
                read(buffer).apply {
                    if (this > 0) {
                        body = String(buffer, 0, this)
                    }
                }
            }
            body = body ?: "Empty body..."
            var resp = "server url=${p.localAddress}${p.requestURI}" +
                    "\nclient url=${p.remoteAddress}" +
                    "\nmethod=${p.requestMethod}" +
                    "\nheaders=${p.requestHeaders.toMap()}" +
                    "\nbody=$body"
            println("请求数据：\n$resp\n请求数据长度=${resp.length}")
            if (p.requestHeaders["Cookie"].isNullOrEmpty()) {
                println("找不到Cookie！")
                resp = "Error! 鉴权未通过"
            } else {
                resp = "Success! 鉴权通过！"
            }
            val len = resp.toByteArray(Charset.defaultCharset()).size
            p.sendResponseHeaders(200, len.toLong())
            p.responseBody.write(resp.toByteArray(Charset.defaultCharset()))
            p.close()
        }
    }

    server.start()

    println("Server start. Host=${server.address.hostString}, port=${server.address.port}")
}