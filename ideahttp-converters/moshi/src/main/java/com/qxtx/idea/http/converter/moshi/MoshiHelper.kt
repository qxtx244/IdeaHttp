package com.qxtx.idea.http.converter.moshi

import androidx.core.util.lruCache
import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.ResponseBody
import org.json.JSONObject
import java.lang.Exception
import java.lang.RuntimeException
import java.lang.reflect.Type

/**
 * @author QXTX-WIN
 *
 * **Create Date** 2022/10/17 10:16
 *
 * **Description**
 *
 */
object MoshiHelper {

    /**
     * 反序列化适配器缓存列表，最大缓存个数为64，lru缓存算法
     */
    private val typeCaches by lazy { lruCache<Type, JsonAdapter<Any>>(64) }

    private val defaultFactory by lazy { KotlinJsonAdapterFactory() }

    /** 预置的builder */
    private val mapBuilders by lazy { newBuilder().add(MapAdapter) }

    private val builder: Moshi by lazy { Moshi.Builder()
        .add(defaultFactory)
        .build()
    }

    @JvmStatic
    fun newBuilder() = builder.newBuilder()

    /**
     * 适配对象类型，仅仅是对Moshi api的简单封装
     * @param type 目标反序列化对象类型
     * @return JsonAdapter对象
     */
    @JvmStatic
    fun <T> adapter(type: Type): JsonAdapter<T> {
        var result = typeCaches[type] as JsonAdapter<T>?
        if (result == null) {
            result = builder.adapter(type)
            typeCaches.put(type, result as JsonAdapter<Any>)
        }
        return result
    }

    /**
     * 反序列化
     * @param json json字符串
     * @return org.json.JsonObject对象，可能为null
     */
    @JvmStatic
    fun toJsonObject(json: String?): JSONObject? {
        return toMap(json)?.let { JSONObject(it) }
    }

    /**
     * 反序列化
     * @param json json字符串
     * @return Map<String, Any?>对象，可能为null
     */
    @JvmStatic
    fun toMap(json: String?): Map<String, Any?>? {
        if (json == null) return null
        if (json.isEmpty()) return emptyMap()

        val result = try {
            mapBuilders.build()?.adapter<Map<String, Any?>>(Map::class.java)?.fromJson(json)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
        return result
    }

    /**
     * 将对象序列化成json字符串
     *
     * @param R 对象类型
     * @param value 目标序列化对象
     * @return json字符串。如果序列化失败或者发生异常，返回null
     */
    @JvmStatic
    fun <R> toJsonString(value: R): String? {
        value?.apply {
            try {
                val adapter: JsonAdapter<R> = adapter(this::class.java)
                return adapter.toJson(value)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }

    /**
     * 将json字符串反序列化成指定对象
     *
     * @param R 反序列化的目标类型（返回类型）
     * @param value 自定义对象
     * @param type 反序列化的目标类型（同[R]）
     * @return 反序列化得到的对象
     */
    fun <R> parseObject(value: String, type: Type): R? {
        val adapter: JsonAdapter<R> = adapter(type)
        return try {
            adapter.fromJson(value)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private object MapAdapter {

        @FromJson
        fun from(reader: JsonReader): Map<String, Any?>? {
            val result = try {
                reader.readJsonValue() as Map<String, Any?>?
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
            return result
        }

        @ToJson
        fun to(writer: JsonWriter, value: Map<String, Any?>?) {
            //no implementation.
            throw RuntimeException("Not support")
        }
    }
}