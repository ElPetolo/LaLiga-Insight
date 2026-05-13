package com.example.laligainsight.api

import com.example.laligainsight.modelo.RespuestaTheSportsDb
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiTheSportsDb {
    // Busca jugador por nombre
    @GET("api/v1/json/3/searchplayers.php")
    suspend fun searchPlayerByName(
        @Query("p") playerName: String
    ): RespuestaTheSportsDb
}