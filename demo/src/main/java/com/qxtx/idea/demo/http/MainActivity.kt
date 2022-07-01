package com.qxtx.idea.demo.http

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.qxtx.idea.demo.http.databinding.ActivityMainBinding
import com.qxtx.idea.http.HttpBase
import com.qxtx.idea.http.callback.HttpInterceptor
import com.qxtx.idea.http.callback.IHttpCallback
import com.qxtx.idea.http.converter.FastjsonConverterFactory
import com.qxtx.idea.http.response.Response
import okhttp3.Call
import okhttp3.Interceptor
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import kotlin.concurrent.thread

/**
 * @author QXTX-WIN
 * <p>
 * <b>Create Date</b><p> 2022/4/25 13:49
 * <p>
 * <b>Description</b>
 * <pre>
 *   http库测试界面。
 *   注意：反序列化的演示需要配合特定的http服务端（Doc/HttpServer.kt），因为要求服务端返回对应格式的数据，反序列化才能成功。
 *   如果
 * </pre>
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            testHttp()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun testHttp() {
        val http = HttpBase()
        http.apply {
            init(30000)
            addInterceptor(object: HttpInterceptor {
                override fun intercept(chain: Interceptor.Chain): Boolean {
                    Log.d("拦截器", "不需要拦截1")
                    return false
                }
            })
            addInterceptor(object: HttpInterceptor {
                override fun intercept(chain: Interceptor.Chain): Boolean {
                    Log.d("拦截器", "不需要拦截2")
                    return false
                }
            })
            addNetworkInterceptor(object: HttpInterceptor {
                override fun intercept(chain: Interceptor.Chain): Boolean {
                    Log.d("拦截器", "拦截网络请求")
                    //测试拦截联网请求，如果不希望拦截以进行正常网络请求，返回false
                    return true
                }
            })
        }
        thread {
            http.apply {
                val request = newRequest("http://127.0.0.1/httpserver")
                    .addHeader("header1", "HEADER1")
                    .addUrlParam("param1", "PARAM1")
                    .addUrlParam("param2", "中文示例")

                request //线程1，反序列化为Msg.java
                    .setExecutor { thread(name = "测试线程1") { it.run() } }
                    .setResponseConverter(FastjsonConverterFactory(Msg::class.java))
                    .post()
                    .addBody("111111111111".toRequestBody())
                    .enqueue("请求1", object: IHttpCallback<Response> {
                        override fun onResponse(call: Call, response: Response) {
                            Log.d("请求结果", "1, success=${response.isSuccessful}" +
                                    "msg=${response.body as Msg}" +
                                    ", time=${System.currentTimeMillis()} " +
                                    "线程=${Thread.currentThread().name}")
                        }
                        override fun onFailure(call: Call?, e: IOException?) {
                            Log.d("请求结果","1, 请求失败！")
                        }
                    })

                request //线程2，不做反序列化，直接返回body字段为String的Response对象
                    .setExecutor { thread(name = "测试线程2") { it.run() } }
                    .setResponseConverter(null)
                    .post()
                    .addBody("222222222222".toRequestBody())
                    .enqueue("请求2", object: IHttpCallback<Response> {
                        override fun onResponse(call: Call, response: Response) {
                            Log.d("请求结果", "2, msg=${response.body}, time=${System.currentTimeMillis()}" +
                                    ", 线程=${Thread.currentThread().name}")
                        }
                        override fun onFailure(call: Call?, e: IOException?) {
                            Log.d("请求结果", "2, 请求失败！")
                        }
                    })

                request    //将结果反序列化为MsgKotlin.kt
                    .setResponseConverter(FastjsonConverterFactory(MsgData::class.java))
                    .get()
                    .execute(Any()).apply {
                        Log.d("请求结果", "3, msg=${this.body as MsgData?}, time=${System.currentTimeMillis()}" +
                                ", 线程=${Thread.currentThread().name}")
                    }
            }
        }
    }
}