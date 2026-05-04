package com.example.laligainsight.api

import com.example.laligainsight.modelo.ScorersResponse
import com.example.laligainsight.modelo.StandingsResponse
import com.example.laligainsight.modelo.TeamDetailResponse
import com.example.laligainsight.modelo.TeamsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query


// Interfaz que define las operaciones de la API
interface FootballApi {

    // Petición GET para obtener los equipos de la liga española
    @Headers("X-Auth-Token: 60a47381dc67484891646ec83edc953f")
    @GET("competitions/PD/teams")
    suspend fun getTeams(): Response<TeamsResponse>


    // Petición GET para obtener el detalle de cada equipo
    @Headers("X-Auth-Token: 60a47381dc67484891646ec83edc953f")
    @GET("teams/{id}")
    suspend fun getTeamDetail(
        @Path("id") id: Int
    ): Response<TeamDetailResponse>


    // Petición GET para obtener la clasificación de la liga española
    @Headers("X-Auth-Token: 60a47381dc67484891646ec83edc953f")
    @GET("competitions/PD/standings")
    suspend fun getStandings(): StandingsResponse

    // Petición GET para obtener los goleadores de LaLiga
    @Headers("X-Auth-Token: 60a47381dc67484891646ec83edc953f")
    @GET("competitions/PD/scorers")
    suspend fun getScorers(
        @Query("limit") limit: Int = 20
    ): ScorersResponse


}


