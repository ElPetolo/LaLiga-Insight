package com.example.laligainsight.iu

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.laligainsight.api.RetrofitCliente
import com.example.laligainsight.modelo.Match
import com.example.laligainsight.modelo.Player
import com.example.laligainsight.modelo.RatingSummary
import com.example.laligainsight.modelo.StandingTeam
import com.example.laligainsight.modelo.Team
import com.example.laligainsight.repository.RatingRepository
import com.example.laligainsight.viewmodel.PlayersViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TeamDetailScreen(
    team: Team,
    onBackClick: () -> Unit,
    onPlayerClick: (Player) -> Unit
) {
    // Colores del equipo. Se usan para que la cabecera tenga un degradado diferente según el club.
    val gradientColors = getTeamGradient(team.name)

    // Pestaña seleccionada dentro del detalle: Resumen, Plantilla o Partidos.
    var selectedTab by remember { mutableStateOf("Resumen") }

    // Lista de jugadores del equipo, partidos del equipo y datos de clasificación.
    // Al principio están vacíos y se rellenan cuando termina la llamada a la API.
    var players by remember { mutableStateOf<List<Player>>(emptyList()) }
    var matches by remember { mutableStateOf<List<Match>>(emptyList()) }
    var teamStanding by remember { mutableStateOf<StandingTeam?>(null) }

    // ViewModel que trae los jugadores guardados en Firebase.
    // Lo usamos sobre todo para obtener las imágenes de los jugadores.
    val playersViewModel: PlayersViewModel = viewModel()
    val firebasePlayers by playersViewModel.players.collectAsState()

    // Repositorio de valoraciones. Se encarga de leer y guardar ratings en Firebase.
    val ratingRepository = remember { RatingRepository() }

    // Estado donde guardamos la media de valoración y el número de votos del equipo.
    var ratingSummary by remember { mutableStateOf(RatingSummary()) }

    // Scope para ejecutar llamadas suspendidas cuando el usuario valora el equipo.
    val scope = rememberCoroutineScope()

    // Este bloque se ejecuta al entrar en la pantalla o cuando cambia el equipo.
    // Aquí cargamos la valoración, plantilla, clasificación y partidos.
    LaunchedEffect(team.id) {
        ratingSummary = ratingRepository.getRatingSummary(
            entityType = "team",
            entityId = team.id.toString()
        )

        try {
            // Llamada al endpoint de detalle del equipo para obtener su plantilla.
            val response = RetrofitCliente.api.getTeamDetail(team.id)

            players = if (response.isSuccessful) {
                response.body()?.squad ?: emptyList()
            } else {
                emptyList()
            }

            // Llamada a la clasificación general para buscar la fila de este equipo.
            val standingsResponse = RetrofitCliente.api.getStandings()

            teamStanding = standingsResponse.standings
                .firstOrNull { it.type == "TOTAL" }
                ?.table
                ?.firstOrNull { it.team.id == team.id }

            // Llamada separada para traer los partidos del equipo.
            // Está en otro try para que si fallan los partidos no se rompa toda la pantalla.
            try {
                val matchesResponse = RetrofitCliente.api.getTeamMatches(team.id)
                matches = matchesResponse.matches
            } catch (e: Exception) {
                println("ERROR CARGANDO PARTIDOS: ${e.message}")
                e.printStackTrace()
            }
        } catch (e: Exception) {
            println("ERROR EN TEAM DETAIL: ${e.message}")
            e.printStackTrace()
        }
    }

    // Contenedor principal de la pantalla.
    // Tiene el fondo general de la app y scroll porque el contenido puede crecer bastante.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.MainBackgroundBrush)
            .verticalScroll(rememberScrollState())
    ) {
        // Cabecera superior: botón de volver, escudo, nombre del equipo, estadio y valoración.
        TeamDetailHeader(
            team = team,
            gradientColors = gradientColors,
            ratingSummary = ratingSummary,
            onBackClick = onBackClick,
            onRatingSelected = { rating ->
                scope.launch {
                    // Guardamos la valoración del equipo en Firebase.
                    ratingRepository.rateEntity(
                        entityType = "team",
                        entityId = team.id.toString(),
                        rating = rating
                    )

                    // Después de votar, volvemos a pedir el resumen para actualizar estrellas/media.
                    ratingSummary = ratingRepository.getRatingSummary(
                        entityType = "team",
                        entityId = team.id.toString()
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Selector de pestañas del detalle.
        // Cambia el contenido central según lo que pulse el usuario.
        TeamDetailTabs(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Contenido principal dependiendo de la pestaña seleccionada.
        when (selectedTab) {
            "Resumen" -> {
                // Card del resumen del equipo: posición, puntos, racha, goles y progreso de temporada.
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = AppColors.CardSoftStrong
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ResumenEquipo(
                            teamStanding = teamStanding,
                            matches = matches,
                            teamId = team.id
                        )

                        // Separador visual al final del resumen.
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            thickness = 1.dp,
                            color = Color(0xFF1C2B4A)
                        )
                    }
                }
            }

            "Plantilla" -> {
                // Lista de jugadores del equipo.
                // También recibe los jugadores de Firebase para poder mostrar sus imágenes.
                TeamPlayerSection(
                    players = players,
                    firebasePlayers = firebasePlayers,
                    onPlayerClick = onPlayerClick
                )
            }

            "Partidos" -> {
                // Sección de resultados y próximos partidos del equipo.
                TeamMatchesSection(
                    matches = matches,
                    teamId = team.id
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}