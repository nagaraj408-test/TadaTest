package com.demo.tada.data.remote.mock

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class MockBookingInterceptor @Inject constructor() : Interceptor {

    override fun intercept(
        chain: Interceptor.Chain
    ): Response {

        return chain.proceed(chain.request())
    }
}