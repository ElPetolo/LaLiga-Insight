package com.example.laligainsight.api

import com.example.laligainsight.modelo.RespuestaPartidos
import com.example.laligainsight.modelo.RespuestaGoleadores
import com.example.laligainsight.modelo.RespuestaClasificacion
import com.example.laligainsight.modelo.RespuestaDetalleEquipo
import com.example.laligainsight.modelo.RespuestaEquipos
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query


// Interfaz que define las operaciones de la API
interface ApiFutbol {

    // Petición GET para obtener los equipos de la liga española
    @Headers("X-Auth-Token: 60a47381dc67484891646ec83edc953f")
    @GET("competitions/PD/teams")
    suspend fun getTeams(): Response<RespuestaEquipos>


    // Petición GET para obtener el detalle de cada equipo
    @Headers("X-Auth-Token: 60a47381dc67484891646ec83edc953f")
    @GET("teams/{id}")
    suspend fun getTeamDetail(
        @Path("id") id: Int
    ): Response<RespuestaDetalleEquipo>


    // Petición GET para obtener la clasificación de la liga española
    @Headers("X-Auth-Token: 60a47381dc67484891646ec83edc953f")
    @GET("competitions/PD/standings")
    suspend fun getStandings(): RespuestaClasificacion

    // Petición GET para obtener los goleadores de LaLiga
    @Headers("X-Auth-Token: 60a47381dc67484891646ec83edc953f")
    @GET("competitions/PD/scorers")
    suspend fun getScorers(
        @Query("limit") limit: Int = 20
    ): RespuestaGoleadores

    // Petición GET para devolver los partidos DE UN EQUIPO EN CONCRETO
    @Headers("X-Auth-Token: 60a47381dc67484891646ec83edc953f")
    @GET("teams/{id}/matches")
    suspend fun getTeamMatches(
        @Path("id") id: Int,
        // Filtramos los partidos de la liga española
        // En la API, LaLiga esta nombrada con el codigo PD (Primera División)
        @Query("competitions") competition: String = "PD",
    ): RespuestaPartidos

    // Peticion GET para mostrar todos los partidos de LaLiga
    @Headers("X-Auth-Token: 60a47381dc67484891646ec83edc953f")
    @GET("competitions/PD/matches")
    suspend fun getLaLigaMatches(): RespuestaPartidos




}


