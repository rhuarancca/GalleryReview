package com.review.galleryapp02.data.rest

import com.ebookfrenzy.galleryapp02.data.rest.FirebaseApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "https://api.example.com/"

    val instance: FirebaseApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(FirebaseApiService::class.java)
    }
}
