package com.example.laligainsight.repository

import com.example.laligainsight.api.ClienteRetrofit
import com.example.laligainsight.modelo.RespuestaGoleadores
import com.example.laligainsight.modelo.RespuestaClasificacion


// Clase que actúa como intermediaria entre la API (Retrofit)
// y el resto de la aplicación

// Se encarga de centralizar las llamadas a la API y proporcionar los datos al viewModel

class RepositorioFutbol {

    // Obtenemos la clasificación de LaLiga desde la API
    suspend fun getStandings(): RespuestaClasificacion{
        return ClienteRetrofit.api.getStandings()
    }

    // Obtenemos los máximos goleadores de LaLiga
    suspend fun getScorers(): RespuestaGoleadores {
        return ClienteRetrofit.api.getScorers(limit = 20)
    }
}

