package com.example.laligainsight.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitTheSportsDb {

    private const val BASE_URL = "https://www.thesportsdb.com/"

    val api: ApiTheSportsDb by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiTheSportsDb::class.java)
    }
}