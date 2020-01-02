package com.cwh.mvvm_coroutines_base.base.net

import com.cwh.mvvm_coroutines_base.*
import com.ihsanbal.logging.Level
import com.ihsanbal.logging.LoggingInterceptor
import okhttp3.OkHttpClient
import okhttp3.internal.platform.Platform
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.net.InetAddress
import java.net.Socket
import java.net.UnknownHostException
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

/**
 * Description:
 * Date：2020/1/2 0002-11:32
 * Author: cwh
 */

class RetrofitUtils {
    private constructor()


    companion object {
        private var mOkHttpClient: OkHttpClient? = null

        private var mRetrofit: Retrofit? = null

        private fun newOkHttpClient(): OkHttpClient {
            return mOkHttpClient ?: synchronized(this) {
                if (mOkHttpClient != null) {
                    mOkHttpClient!!
                } else {
                    val okHttpClientBuilder = OkHttpClient().newBuilder()
                        .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                        .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                        .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                        .retryOnConnectionFailure(true)
                    //.addInterceptor(ChangeBaseUrlInterceptor())
                    //.cookieJar(mCookieJar)
                    okHttpClientBuilder.addInterceptor(
                        LoggingInterceptor.Builder()
                            .loggable(isShowLog)
                            .setLevel(Level.BASIC)
                            //add token header
                            //.addHeader(Constant.HEADER, SPUtils.getToken(UWApplication.getApplication()))
                            .log(Platform.INFO)
                            .tag("MVVM")
                            .request("request")
                            .response("response")
                            .build()
                    )
                    addSSLManager(okHttpClientBuilder)
                    mOkHttpClient = okHttpClientBuilder.build()
                    mOkHttpClient!!
                }
            }
        }

        private fun addSSLManager(okHttpClientBuilder: OkHttpClient.Builder) {
            //处理https协议
            var sc: SSLContext?
            val tm = TrustAllManager()
            try {
                sc = SSLContext.getInstance("TLS")
                sc!!.init(null, arrayOf<TrustManager>(tm), SecureRandom())
            } catch (e: Exception) {
                e.printStackTrace()
                sc = null
            }

            if (sc != null) {
                okHttpClientBuilder.sslSocketFactory(Tls12SocketFactory(sc.socketFactory), tm)
                    .hostnameVerifier(HostnameVerifier { hostname, session -> true })
            } else {
                okHttpClientBuilder.hostnameVerifier(HostnameVerifier { hostname, session -> true })
            }
        }


        private fun newRetrofitInstance(): Retrofit {
            return mRetrofit ?: synchronized(this) {
                if (mRetrofit != null) {
                    mRetrofit!!
                } else {
                    mRetrofit = Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(newOkHttpClient())
                        .build()
                    mRetrofit!!
                }
            }
        }

        /**
         * 创建对应的API Service 实例
         */
        fun <T> createServiceInstance(cls: Class<T>): T {
            return newRetrofitInstance().create(cls)
        }


    }


}

private class TrustAllManager : X509TrustManager {
    @Throws(CertificateException::class)
    override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
    }

    @Throws(CertificateException::class)
    override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
    }

    override fun getAcceptedIssuers(): Array<X509Certificate?> {
        return arrayOfNulls(0)
    }
}

private class Tls12SocketFactory(internal val delegate: SSLSocketFactory) : SSLSocketFactory() {

    private val TLS_SUPPORT_VERSION = arrayOf("TLSv1", "TLSv1.1", "TLSv1.2")

    override fun getDefaultCipherSuites(): Array<String> {
        return delegate.defaultCipherSuites
    }

    override fun getSupportedCipherSuites(): Array<String> {
        return delegate.supportedCipherSuites
    }

    @Throws(IOException::class)
    override fun createSocket(s: Socket, host: String, port: Int, autoClose: Boolean): Socket? {
        return patch(delegate.createSocket(s, host, port, autoClose))
    }

    @Throws(IOException::class, UnknownHostException::class)
    override fun createSocket(host: String, port: Int): Socket? {
        return patch(delegate.createSocket(host, port))
    }

    @Throws(IOException::class, UnknownHostException::class)
    override fun createSocket(
        host: String,
        port: Int,
        localHost: InetAddress,
        localPort: Int
    ): Socket? {
        return patch(delegate.createSocket(host, port, localHost, localPort))
    }

    @Throws(IOException::class)
    override fun createSocket(host: InetAddress, port: Int): Socket? {
        return patch(delegate.createSocket(host, port))
    }

    @Throws(IOException::class)
    override fun createSocket(
        address: InetAddress,
        port: Int,
        localAddress: InetAddress,
        localPort: Int
    ): Socket? {
        return patch(delegate.createSocket(address, port, localAddress, localPort))
    }

    private fun patch(s: Socket): Socket {
        if (s is SSLSocket) {
            s.enabledProtocols = TLS_SUPPORT_VERSION
        }
        return s

    }
}