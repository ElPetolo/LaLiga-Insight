package com.example.laligainsight.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Creamos el cliente de la API con Retrofit
object RetrofitCliente {

    // Base URL de la API
    private const val BASE_URL = "https://api.football-data.org/v4/"

    // Creamos la instancia de Retrofit
    val api: FootballApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FootballApi::class.java)
    }

}