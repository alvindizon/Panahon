package com.alvindizon.panahon.networking.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConnectivityInterceptor @Inject constructor(private val monitor: LiveNetworkMonitor) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        return if (monitor.isConnected()) {
            chain.proceed(request)
        } else {
            throw com.alvindizon.panahon.networking.exception.NoInternetException()
        }
    }
}
