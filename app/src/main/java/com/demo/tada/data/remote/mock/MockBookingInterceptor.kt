package com.demo.tada.data.remote.mock

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockBookingInterceptor @Inject constructor() : Interceptor {

    private val savedBookings = mutableListOf<JSONObject>()

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath

        return if (path.contains("books")) {
            when (request.method) {
                "POST" -> handlePostBook(chain)
                "GET" -> handleGetBooks(chain)
                else -> chain.proceed(request)
            }
        } else {
            chain.proceed(request)
        }
    }

    private fun handlePostBook(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val buffer = Buffer()
        request.body?.writeTo(buffer)
        val requestBodyString = buffer.readUtf8()
        val requestJson = JSONObject(requestBodyString)
        val a = requestJson.getJSONObject("a")
        val b = requestJson.getJSONObject("b")
        val aqiA = a.optInt("aqi", 50)
        val aqiB = b.optInt("aqi", 50)
        
        val price = 5000.0 + (aqiA + aqiB) * 10.0

        val responseJson = JSONObject().apply {
            put("id", (1000..9999).random())
            put("a", a)
            put("b", b)
            put("price", price)
        }

        savedBookings.add(responseJson)

        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .body(responseJson.toString().toResponseBody("application/json".toMediaType()))
            .build()
    }

    private fun handleGetBooks(chain: Interceptor.Chain): Response {
        val request = chain.request()
        
        // If empty, add some dummy data
        if (savedBookings.isEmpty()) {
            val dummy = JSONObject().apply {
                put("id", 1)
                put("a", JSONObject().apply {
                    put("latitude", 37.5665)
                    put("longitude", 126.9780)
                    put("aqi", 50)
                    put("name", "Seoul City Hall")
                })
                put("b", JSONObject().apply {
                    put("latitude", 37.5651)
                    put("longitude", 126.9895)
                    put("aqi", 60)
                    put("name", "Myeong-dong")
                })
                put("price", 15000.0)
            }
            savedBookings.add(dummy)
        }

        val jsonArray = JSONArray(savedBookings)

        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .body(jsonArray.toString().toResponseBody("application/json".toMediaType()))
            .build()
    }
}
