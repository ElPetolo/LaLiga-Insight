package com.example.laligainsight.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers


// Interfaz que define las operaciones de la API
interface FootballApi {

    // Petición GET para obtener los equipos de la liga española
    @Headers("X-Auth-Token: 60a47381dc67484891646ec83edc953f")
    @GET("competitions/PD/teams")
    suspend fun getTeams(): Response<TeamsResponse>
}


