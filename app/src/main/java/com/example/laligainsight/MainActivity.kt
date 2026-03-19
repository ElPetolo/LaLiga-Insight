package com.example.laligainsight

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.example.laligainsight.api.RetrofitCliente
import com.example.laligainsight.iu.TeamsScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Al iniciar la app mostramos la pantalla Compose vacía
        setContent {
            TeamsScreen(teams = emptyList())
        }

        // Llamamos a la API usando corrutinas
        lifecycleScope.launch {
            try {
                // Petición a la API para obtener los equipos
                val response = RetrofitCliente.api.getTeams()

                // Si la respuesta es correcta
                if (response.isSuccessful) {

                    // Obtenemos la lista de equipos
                    val teams = response.body()?.teams ?: emptyList()

                    // Volvemos a pintar la pantalla, ahora con los datos reales
                    setContent {
                        TeamsScreen(teams = teams)
                    }

                } else {
                    // Si hay error, mostramos la pantalla sin datos
                    setContent {
                        TeamsScreen(teams = emptyList())
                    }
                }

            } catch (e: Exception) {
                // Si ocurre un error de conexión, mostramos la pantalla vacía
                setContent {
                    TeamsScreen(teams = emptyList())
                }
            }
        }
    }
}
