package com.qxtx.idea.http.tools

import android.util.Log
import com.qxtx.idea.http.BuildConfig

/**
 * @author QXTX-WIN
 *
 * **Create Date** 2021/2/25 17:48
 *
 * **Description**
 *
 * 日志封装类
 */
object HttpLog {

    private val TAG = HttpLog::class.java.simpleName

    private val tag = TAG

    private val logEnable = BuildConfig.DEBUG

    /** 打印日志的等级  */
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    private annotation class Level {
        companion object {
            var VERBOSE = 0
            var DEBUG = 1
            var INFO = 2
            var WARN = 3
            var ERROR = 4
            var WTF = 5
        }
    }

    private fun console(msg: String, @Level level: Int) {
        if (!logEnable) {
            return
        }
        when (level) {
            Level.VERBOSE -> Log.v(tag, msg)
            Level.DEBUG -> Log.d(tag, msg)
            Level.INFO -> Log.i(tag, msg)
            Level.WARN -> Log.w(tag, msg)
            Level.ERROR -> Log.e(tag, msg)
            Level.WTF -> Log.wtf(tag, msg)
        }
    }

    @JvmStatic
    fun d(msg: String) {
        console(msg, Level.DEBUG)
    }

    @JvmStatic
    fun v(msg: String) {
        console(msg, Level.VERBOSE)
    }

    @JvmStatic
    fun i(msg: String) {
        console(msg, Level.INFO)
    }

    @JvmStatic
    fun w(msg: String) {
        console(msg, Level.WARN)
    }

    @JvmStatic
    fun e(msg: String) {
        console(msg, Level.ERROR)
    }

    @JvmStatic
    fun wtf(msg: String) {
        console(msg, Level.WTF)
    }
}