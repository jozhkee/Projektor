package com.example.eventappgroup17.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    // 10.0.2.2 = Android emulator's alias for host localhost.
    // Change to your machine's LAN IP when testing on a physical device.
    // https://projektor.fog.pt is our hosted API
    private const val BASE_URL = "https://projektor.fog.pt/"

    var token: String? = null
    val bearerToken get() = "Bearer $token"

    private val retrofit: Retrofit by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    val api: ApiService by lazy { retrofit.create(ApiService::class.java) }
}