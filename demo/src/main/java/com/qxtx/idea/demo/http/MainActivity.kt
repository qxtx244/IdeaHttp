package com.qxtx.idea.demo.http

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.qxtx.idea.demo.http.databinding.ActivityMainBinding
import com.qxtx.idea.http.ContentType
import com.qxtx.idea.http.HttpBase
import com.qxtx.idea.http.interceptor.HttpInterceptor
import com.qxtx.idea.http.callback.IHttpCallback
import com.qxtx.idea.http.converter.fastjson.FastjsonConverterFactory
import com.qxtx.idea.http.converter.moshi.MoshiConverterFactory
import com.qxtx.idea.http.converter.moshi.MoshiHelper
import com.qxtx.idea.http.response.Response
import com.qxtx.idea.http.tools.MultiPair
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import kotlin.concurrent.thread

/**
 * @author QXTX-WIN
 *
 * **Create Date** 2022/4/25 13:49
 *
 * **Description**
 *
 * http库测试界面。
 * 注意：反序列化的演示需要配合特定的http服务端（Doc/HttpServer.kt），因为要求服务端返回对应格式的数据，反序列化才能成功。
 */
class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnConverterReq.setOnClickListener {
            testHttpAndConverter()
        }

        binding.btnCookieReq.setOnClickListener {
            testCookies()
        }
    }

    private fun testCookies() {
        thread {
            HttpBase().apply {
                init(2000)

                var resp= newRequest("http://192.168.1.6:12346/login")
                    .get()
                    .execute(Any())
                println("登录回应的请求头：${resp.headers.toMultimap()}")

                resp = newRequest("http://192.168.1.6:12346/logout")
                    .get()
                    .execute(Any())
                println("登出结果：${resp.stringBody()}")
            }
        }
    }

    private fun testHttpAndConverter() {
        val http = HttpBase()
        http.apply {
            init(5000)
            addInterceptor(object: HttpInterceptor {
                override fun intercept(chain: Interceptor.Chain): Boolean {
                    Log.e("TAG", "拦截器,不需要拦截1")
                    return false
                }
            })
            addInterceptor(object: HttpInterceptor {
                override fun intercept(chain: Interceptor.Chain): Boolean {
                    Log.e("TAG", "拦截器,不需要拦截2")
                    return false
                }
            })
            addNetworkInterceptor(object: HttpInterceptor {
                override fun intercept(chain: Interceptor.Chain): Boolean {
                    Log.e("TAG", "拦截器,拦截网络请求")
                    //测试拦截联网请求，如果不希望拦截以进行正常网络请求，返回false
                    return false
                }
            })
        }
        thread {
            http.apply {
                val request = newRequest("http://127.0.0.1:12346/httpserver")
                    .addHeader("header1", "HEADER1")
                    .addUrlParam("param1", "PARAM1")
                    .addUrlParam("param2", "中文示例")

                request //线程1，反序列化为Msg.java
                    .setExecutor { thread(name = "测试线程1") { it.run() } }
                    .setResponseConverter(MoshiConverterFactory(Msg::class.java))
                    .post()
                    .addBody("11222".toRequestBody())
                    .enqueue("请求1", object: IHttpCallback {
                        override fun onResponse(call: Call, response: Response) {
                            val body = response.parseBody<Msg>()
                            Log.e("TAG", "请求结果,moshi"
                                    + ", success=${response.isSuccessful}"
                                    + ", data=${body}"
                                    + ", code=${response.code}, message=${response.message}"
                                    + ", 线程=${Thread.currentThread().name}")
                            val json = MoshiHelper.toJsonString(body)
                            val map = MoshiHelper.toMap(json)
                            val jsonObj = MoshiHelper.toJsonObject(json)
                            Log.e("TAG", "序列化结果： str=$json, map=$map, obj=$jsonObj")
                        }
                        override fun onFailure(call: Call?, e: IOException?) {
                            Log.e("TAG","请求结果,fastjson, 请求失败！")
                        }
                    })

//                request //线程2，不做反序列化，直接返回body字段为String的Response对象
//                    .setExecutor { thread(name = "测试线程2") { it.run() } }
//                    .setResponseConverter(null)
//                    .post()
//                    .addBody("222222222222".toRequestBody())
//                    .enqueue("请求2", object: IHttpCallback {
//                        override fun onResponse(call: Call, response: Response) {
//                            Log.e("请求结果", "2, msg=${response.rawBody()}, 线程=${Thread.currentThread().name}")
//                        }
//                        override fun onFailure(call: Call?, e: IOException?) {
//                            Log.e("请求结果", "2, 请求失败！")
//                        }
//                    })

                request    //将结果反序列化为MsgKotlin.kt
                    .setResponseConverter(FastjsonConverterFactory(MsgData::class.java))
                    .get()
                    .execute(Any()).apply {
                        Log.e("TAG", "请求结果,fastjson"
                                + ", success=$isSuccessful"
                                + ", data=${parseBody<MsgData>()}, "
                                + ", code=$code, message=$message"
                                + ", 线程=${Thread.currentThread().name}")
                    }

//                request //patch请求
//                    .setResponseConverter(null)
//                    .requestMethod(RequestMethod.PATCH)
//                    .execute(Any()).apply {
//                        Log.e("请求结果", "3, msg=${rawBody()}")
//                    }
            }
        }
    }
}