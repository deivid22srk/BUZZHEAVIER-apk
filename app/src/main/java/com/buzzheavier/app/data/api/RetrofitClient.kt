package com.buzzheavier.app.data.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    
    private const val BASE_URL = "https://buzzheavier.com/"
    private const val UPLOAD_BASE_URL = "https://w.buzzheavier.com/"
    
    private var authToken: String? = null
    
    fun setAuthToken(token: String?) {
        authToken = token
    }
    
    private val authInterceptor = Interceptor { chain ->
        val request = chain.request()
        val newRequest = if (authToken != null) {
            request.newBuilder()
                .addHeader("Authorization", "Bearer $authToken")
                .build()
        } else {
            request
        }
        chain.proceed(newRequest)
    }
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()
    
    private val uploadOkHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)
        .build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    private val uploadRetrofit = Retrofit.Builder()
        .baseUrl(UPLOAD_BASE_URL)
        .client(uploadOkHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val api: BuzzHeavierApi = retrofit.create(BuzzHeavierApi::class.java)
    val uploadApi: BuzzHeavierUploadApi = uploadRetrofit.create(BuzzHeavierUploadApi::class.java)
}
