package com.example.laligainsight

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.lifecycleScope
import com.example.laligainsight.api.RetrofitCliente
import com.example.laligainsight.iu.TeamDetailScreen
import com.example.laligainsight.iu.TeamsScreen
import com.example.laligainsight.modelo.Team
import kotlinx.coroutines.launch
import androidx.compose.runtime.setValue
import com.example.laligainsight.iu.PlayerDetailScreen
import com.example.laligainsight.modelo.Player
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : ComponentActivity() {

    // CAMBIO feature/detalle-equipo:
    // Variable donde guardamos el equipo pulsado
    // si es null ---> pantalla principal
    // si no --> mostramos el detalle del equipo
    private var selectedTeam by mutableStateOf<Team?>(null)

    // CAMBIO feature/detalle-equipo:
    // Variable donde guardamos la lista de equipos obtenidos por la  api
    // De esta manera, no llamamos a setContent tantas veces
    private var teams by mutableStateOf<List<Team>>(emptyList())

    // Variable para el juagdor seleccionado
    private var selectedPlayer by mutableStateOf<Player?>(null)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        // Cambio feature/detalle-equipo
        // Llamamos a setContent una vez
        setContent {
            when {
                selectedPlayer != null -> {
                    PlayerDetailScreen(
                        player = selectedPlayer!!,
                        onBackClick = { selectedPlayer = null }
                    )
                }

                selectedTeam != null -> {
                    TeamDetailScreen(
                        team = selectedTeam!!,
                        onBackClick = { selectedTeam = null },
                        onPlayerClick = { player ->
                            selectedPlayer = player
                        }
                    )
                }

                else -> {
                    TeamsScreen(
                        teams = teams,
                        onTeamClick = { team ->
                            selectedTeam = team
                        }
                    )
                }
            }
        }

            // Llamamos a la API usando corrutinas
            lifecycleScope.launch {
                try {
                    // Petición a la API para obtener los equipos
                    val response = RetrofitCliente.api.getTeams()

                    // Si la respuesta es correcta
                    if (response.isSuccessful) {

                        // Obtenemos la lista de equipos
                        teams = response.body()?.teams ?: emptyList()

                    } else {
                        // Mostramos la lista vacía si la respuesta no es correcta
                        teams = emptyList()
                    }

                } catch (e: Exception) {
                    // Si ocurre un error de conexión, mostramos la lista vacía
                    teams = emptyList()
                }
            }
        }
    }

