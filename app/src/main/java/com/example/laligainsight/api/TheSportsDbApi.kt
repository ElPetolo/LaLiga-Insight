package com.example.laligainsight.api

import com.example.laligainsight.modelo.TheSportsDbResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface TheSportsDbApi {
    // Busca jugador por nombre
    @GET("api/v1/json/3/searchplayers.php")
    suspend fun searchPlayerByName(
        @Query("p") playerName: String
    ): TheSportsDbResponse
}