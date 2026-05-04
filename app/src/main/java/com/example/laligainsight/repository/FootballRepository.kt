package com.example.laligainsight.repository

import com.example.laligainsight.api.RetrofitCliente
import com.example.laligainsight.modelo.ScorersResponse
import com.example.laligainsight.modelo.StandingsResponse


// Clase que actúa como intermediaria entre la API (Retrofit)
// y el resto de la aplicación

// Se encarga de centralizar las llamadas a la API y proporcionar los datos al viewModel

class FootballRepository {

    // Obtenemos la clasificación de LaLiga desde la API
    suspend fun getStandings(): StandingsResponse{
        return RetrofitCliente.api.getStandings()
    }

    // Obtenemos los máximos goleadores de LaLiga
    suspend fun getScorers(): ScorersResponse {
        return RetrofitCliente.api.getScorers(limit = 20)
    }
}

