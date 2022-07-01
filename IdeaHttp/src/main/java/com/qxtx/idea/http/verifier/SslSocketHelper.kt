package com.qxtx.idea.http.verifier

import java.net.Socket
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.cert.X509Certificate
import javax.net.ssl.*

/**
 * @author QXTX-WIN
 *
 * **Create Date** 2022/5/19 14:34
 *
 * **Description**
 *
 * 用于https请求的帮助类
 */
class SslSocketHelper {

    companion object {
        @JvmField
        var sslContext: SSLContext? = null

        @JvmField
        val hostnameVerifier = HostnameVerifier { _, _ -> true }

        @JvmField
        var trustManager: X509TrustManager? = null

        init {
            val trustManagers = arrayOf<X509TrustManager>(TrustAllCert())
            trustManager = trustManagers[0]
            try {
                sslContext = SSLContext.getInstance("TLS")
                sslContext!!.init(null, trustManagers, null)
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            } catch (e: KeyManagementException) {
                e.printStackTrace()
            }
        }
    }
}

class TrustAllCert: X509ExtendedTrustManager() {

    override fun getAcceptedIssuers(): Array<X509Certificate> {
        return arrayOf()
    }

    override fun checkClientTrusted(
        chain: Array<out X509Certificate>?,
        authType: String?,
        socket: Socket?
    ) {
    }

    override fun checkClientTrusted(
        chain: Array<out X509Certificate>?,
        authType: String?,
        engine: SSLEngine?
    ) {
    }

    override fun checkServerTrusted(
        chain: Array<out X509Certificate>?,
        authType: String?,
        socket: Socket?
    ) {
    }

    override fun checkServerTrusted(
        chain: Array<out X509Certificate>?,
        authType: String?,
        engine: SSLEngine?
    ) {
    }

    override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
    }

    override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
    }
}

class TrustAllHostnameVerifier: HostnameVerifier {

    override fun verify(hostname: String?, session: SSLSession?): Boolean {
        return true
    }
}
