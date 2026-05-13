package com.example.laligainsight.iu

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.laligainsight.api.ClienteRetrofit
import com.example.laligainsight.modelo.Match
import com.example.laligainsight.viewmodel.JugadoresViewModel
import com.example.laligainsight.viewmodel.GoleadoresViewModel
import com.example.laligainsight.viewmodel.ClasificacionViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PantallaClasificacion(
    viewModel: ClasificacionViewModel = viewModel(),
    onHomeClick: () -> Unit,
    onRankingClick: () -> Unit,
    onCompareClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    // Estado principal de la clasificación que ya expone el ViewModel.
    val totalStandings by viewModel.totalStandings.collectAsState()

    // Pestaña visible en este momento dentro de la pantalla.
    var selectedTab by remember { mutableStateOf("GENERAL") }

    // Modo visual para la sección de goleadores.
    var scorersViewMode by remember { mutableStateOf("CARDS") }

    // Estados de carga y error de la clasificación.
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // ViewModel que ya se usa para cargar la tabla de goleadores.
    val scorersViewModel: GoleadoresViewModel = viewModel()
    val scorers by scorersViewModel.scorers.collectAsState()
    val scorersLoading by scorersViewModel.isLoading.collectAsState()
    val scorersError by scorersViewModel.error.collectAsState()

    // Lista completa de partidos de LaLiga para luego filtrarla por jornada.
    var matches by remember { mutableStateOf<List<Match>>(emptyList()) }

    // Jornada seleccionada en el desplegable de partidos.
    var selectedMatchday by remember { mutableStateOf(1) }

    // Jugadores guardados en Firebase para recuperar sus fotos.
    val playersViewModel: JugadoresViewModel = viewModel()
    val firebasePlayers by playersViewModel.players.collectAsState()

    // Esta llamada solo se hace al entrar para tener todas las jornadas disponibles.
    LaunchedEffect(Unit) {
        try {
            val response = ClienteRetrofit.api.getLaLigaMatches()
            matches = response.matches
            println("PARTIDOS DE LALIGA RECIBIDOS: ${matches.size}")
        } catch (e: Exception) {
            println("ERROR CARGANDO PARTIDOS DE LALIGA: ${e.message}")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColoresApp.MainBackgroundBrush)
            .statusBarsPadding()
    ) {
        CabeceraPantalla(
            title = "Clasificación",
            subtitle = "Tabla, goleadores y datos de LaLiga",
            badge = "Rankings",
            icon = Icons.Default.EmojiEvents
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Estas pestañas cambian entre clasificación, goleadores y partidos.
            PestanasClasificacion(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            Spacer(modifier = Modifier.height(8.dp))

            when {
                isLoading -> {
                    CircularProgressIndicator(color = Color.White)
                }

                error != null -> {
                    Text(
                        text = error ?: "Error desconocido",
                        color = Color.Red
                    )
                }

                else -> {
                    when (selectedTab) {
                        "GENERAL" -> {
                            TablaClasificacion(standings = totalStandings)
                        }

                        "GOLEADORES" -> {
                            // El selector solo cambia el formato visual, no la fuente de datos.
                            ScorersViewSelector(
                                selectedMode = scorersViewMode,
                                onModeSelected = { scorersViewMode = it }
                            )

                            if (scorersViewMode == "CARDS") {
                                TarjetasGoleadores(
                                    scorers = scorers,
                                    players = firebasePlayers,
                                    isLoading = scorersLoading,
                                    error = scorersError
                                )
                            } else {
                                ListaGoleadores(
                                    scorers = scorers,
                                    players = firebasePlayers,
                                    isLoading = scorersLoading,
                                    error = scorersError
                                )
                            }
                        }

                        "PARTIDOS" -> {
                            // Esta parte solo pinta la jornada elegida y su lista de partidos.
                            SeccionJornada(
                                matches = matches,
                                selectedMatchday = selectedMatchday,
                                onMatchdaySelected = { selectedMatchday = it }
                            )
                        }
                    }
                }
            }
        }

        // La navegación inferior se mantiene igual que en la versión anterior.
        BarraInferiorApp(
            selectedTab = "Rankings",
            onHomeClick = onHomeClick,
            onRankingClick = onRankingClick,
            onCompareClick = onCompareClick,
            onProfileClick = onProfileClick
        )
    }
}
