package com.example.laligainsight.iu

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.laligainsight.viewmodel.StandingsViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import coil.compose.AsyncImage
import com.example.laligainsight.modelo.FirebasePlayer
import com.example.laligainsight.modelo.Scorer
import com.example.laligainsight.modelo.StandingTeam
import com.example.laligainsight.viewmodel.PlayersViewModel
import com.example.laligainsight.viewmodel.ScorersViewModel
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Icon
import com.example.laligainsight.api.RetrofitCliente
import com.example.laligainsight.modelo.Match



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StandingsScreen(
    viewModel: StandingsViewModel = viewModel(),
    onHomeClick: () -> Unit,
    onRankingClick: () -> Unit,
    onCompareClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    // --- VARIABLES REFERENTES PARA STANDINGS (CLASIFICACIÓN) ---

    // Variables para cada tipo de clasificacion (GENERAL, HOME, AWAY)
    val totalStandings by viewModel.totalStandings.collectAsState()

    var selectedTab by remember { mutableStateOf("GENERAL") }

    var scorersViewMode by remember { mutableStateOf("CARDS") }

    // Botones para cambiar de clasificacion
    val standings = totalStandings

    // Estado de carga
    val isLoading by viewModel.isLoading.collectAsState()

    // Estado de error
    val error by viewModel.error.collectAsState()


    // --- VARIABLES REFERENTES PARA SCORERS (GOLEADORES) ---
    val scorersViewModel: ScorersViewModel = viewModel()

    // Lista de goleadores desde la API
    val scorers by scorersViewModel.scorers.collectAsState()

    // Estado de carga goleadores
    val scorersLoading by scorersViewModel.isLoading.collectAsState()

    // Estado de error goleadores
    val scorersError by scorersViewModel.error.collectAsState()

    // Variables para mostrar los partidos por jornada
    var matches by remember{ mutableStateOf<List<Match>>(emptyList())}

    // Variable necesaria para poder filtrar por jornada
    var selectedMatchday by remember { mutableStateOf(1) }

    // --- VARIABLES PARA EL VIEWMODEL DE JUGADORES Y FIREBASE ---
    val playersViewModel: PlayersViewModel = viewModel()
    val firebasePlayers by playersViewModel.players.collectAsState()


    // Cuando entre en la pantalla, cargamos todos los partidos de LaLiga

    //  Unit == se ejecuta una vez (unica) cuando entra el composable
    LaunchedEffect(Unit) {

        try {
            // Llamamos a la API para obtener todos los partidos de LaLiga
            val response = RetrofitCliente.api.getLaLigaMatches()

            // Guardamos los partidos que hemos recibido de la API
            matches = response.matches

            // Prueba para comprobar
            println("PARTIDOS DE LALIGA RECIBIDOS: ${matches.size}")

        } catch (e: Exception) {
            println("ERROR CARGANDO PARTIDOS DE LALIGA: ${e.message}")
        }

    }


    // Aquí iría el código para mostrar la clasificación
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.MainBackgroundBrush)
            .statusBarsPadding()
    ) {
        ScreenHeader(
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

            StatsTabs(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Cuando esté cargando:
            when {
                isLoading -> {
                    CircularProgressIndicator(color = Color.White)
                }

                // Si hay error:
                error != null -> {
                    Text(
                        text = error ?: "Error desconocido",
                        color = Color.Red
                    )
                }

                else -> {

                    // Según la pestaña seleccionada...
                    when (selectedTab) {


                        // Clasificación general
                        "GENERAL" -> {
                            StandingHeader()
                            LazyColumn {
                                items(standings) { team ->
                                    StandingItem(team = team)
                                }
                            }
                        }

                        // Lista de goleadores
                        "GOLEADORES" -> {

                            // Elegir entre diseño cards o diseño de tabla
                            ScorersViewSelector(
                                selectedMode = scorersViewMode,
                                onModeSelected = { scorersViewMode = it }
                            )

                            // Según el modo elegido, mostramos un diseño u otro
                            if (scorersViewMode == "CARDS") {

                                ScorersCardsList(
                                    scorers = scorers,
                                    players = firebasePlayers,
                                    isLoading = scorersLoading,
                                    error = scorersError
                                )
                            } else {
                                ScorersList(
                                    scorers = scorers,
                                    players = firebasePlayers,
                                    isLoading = scorersLoading,
                                    error = scorersError
                                )
                            }
                        }

                        // PENDIENTE POR HACER
                        "PARTIDOS" -> {

                            // Filtramos los partidos por jornada (1-38)
                            val partidosJornada = matches.filter { it.matchday == selectedMatchday }

                            // Estado para el despegable de elección de jornada (abierto o cerrado)
                            var extendido by remember { mutableStateOf(false) }

                            // Creamos un despegable para que el usuario pueda ver las jornadas
                            Box {

                                // Boton para abrir el despegable
                                Row(
                                    modifier = Modifier
                                        .background(
                                            AppColors.AccentGreen,
                                            RoundedCornerShape(20.dp)
                                        ) //Elegimos un fondo rojn
                                        .clickable {
                                            extendido = true
                                        } // Al pulsar se abre el despegale
                                        .padding(horizontal = 14.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    // Texto con la jornada que seleccionamos
                                    Text(
                                        text = "JORNADA ${selectedMatchday}",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .background(
                                                AppColors.AccentGreen,
                                                RoundedCornerShape(20.dp)
                                            )
                                            .clickable { extendido = true }
                                            .padding(horizontal = 14.dp, vertical = 8.dp)
                                    )

                                    // Icono de flecha hacia abajo para dar impresion de lista
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Abrir selector",
                                        tint = Color.White
                                    )
                                }

                                // Menú despegable --> DropDownMenu
                                DropdownMenu(
                                    expanded = extendido, // Se muestra cuando extendido = true
                                    onDismissRequest = {
                                        extendido = false
                                    } // Al cerrar el menu, extendido vuelve a false
                                ) {

                                    // Recorremos todas las jornadas
                                    (1..38).forEach() { jornada ->

                                        // Boton para cada jornada
                                        DropdownMenuItem(
                                            text = {
                                                // Texto de cada opción
                                                Text(text = "JORNADA $jornada")
                                            },

                                            // Al pulsar una jornada...
                                            onClick = {
                                                selectedMatchday = jornada //guardado de jornada
                                                extendido = false // se cierra el menú
                                            }
                                        )

                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))


                            // Mostramos la lista de partidos de esa jornada
                            LazyColumn {
                                items(partidosJornada) { match ->
                                    PartidoRankingItem(
                                        match = match
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Barra inferior de navegación entre pantallas
        AppBottomBar(
            selectedTab = "Rankings",
            onHomeClick = onHomeClick,
            onRankingClick = onRankingClick,
            onCompareClick = onCompareClick,
            onProfileClick = onProfileClick
        )
    }
}


// Funcion para cada fila de la clasificación
@Composable
fun StandingItem(team: StandingTeam) {

    val accentColor = when {
        team.position in 1..5 -> Color(0xFF1D75D8) // Champions
        team.position == 6 || team.team.name.contains("Real Sociedad", true) -> Color(0xFFFF6A00) // Europa League
        team.position == 7 -> Color(0xFF2ECC71) // Conference
        team.position >= 18 -> Color(0xFFE53935) // Descenso
        else -> Color(0xFF30415F)
    }


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .background(
                brush = Brush.horizontalGradient(
                    listOf(
                        AppColors.CardDark,
                        accentColor.copy(alpha = 0.75f)
                    )
                ),
                shape = RoundedCornerShape(22.dp)
            )
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .width(34.dp)
                .height(30.dp)
                .background(
                    color = accentColor,
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${team.position}.",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        AsyncImage(
            model = team.team.crest,
            contentDescription = team.team.name,
            modifier = Modifier.size(28.dp)
        )

        Text(
            text = team.team.name,
            color = Color.White,
            modifier = Modifier
                .weight(1.6f)
                .padding(start = 8.dp),
            fontWeight = FontWeight.SemiBold
        )

        Text("${team.playedGames}", color = Color.White, modifier = Modifier.weight(0.7f))
        Text("${team.points}", color = Color.White, modifier = Modifier.weight(0.7f), fontWeight = FontWeight.Bold)
        Text("${team.goalDifference}", color = Color.White, modifier = Modifier.weight(0.7f))
        Text("${team.goalsFor}:${team.goalsAgainst}", color = Color.White, modifier = Modifier.weight(0.9f))
    }
}


// Funcion para poner un encabezado en la tabla de clasificación
@Composable
fun StandingHeader(){
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically


    ) {
        Text("POS", color = Color.White, modifier = Modifier.width(28.dp))
        Spacer(modifier = Modifier.width(28.dp))
        Text("TEAM", color = Color.White, modifier = Modifier.weight(1.6f))
        Text("PJ", color = Color.White, modifier = Modifier.weight(0.7f))
        Text("PTS", color = Color.White, modifier = Modifier.weight(0.7f))
        Text("+/-", color = Color.White, modifier = Modifier.weight(0.7f))
        Text("GOLES", color = Color.White, modifier = Modifier.weight(0.9f))
    }
}

// Pestañas para cambiar secciones de estadisticas
@Composable
fun StatsTabs(selectedTab: String, onTabSelected: (String) -> Unit) {

    // Opciones de pestañas
    val tabs = listOf("GENERAL", "GOLEADORES", "PARTIDOS")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 8.dp),
    ) {
        tabs.forEach { tab ->

            // Comprueba si esta pestaña es la seleccionada actualmente
            val selected = selectedTab == tab

            Text(
                text = tab,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .background(
                        color = if (selected) AppColors.AccentGreen else AppColors.CardDark,
                        shape = RoundedCornerShape(20.dp)
                    )
                // Al pulsar una pestaña, cambiamos la seccion seleccionada
                .clickable { onTabSelected(tab) }
                .padding(horizontal = 12.dp, vertical = 8.dp)
            )

        }
    }
}



// Función para la fila individual de cada goleador
@Composable
fun ScorerItem(scorer: Scorer, players: List<FirebasePlayer>){

    // Buscamos la imagen del jugador en Firebase
    val imageUrl = players
        .firstOrNull { it.playerName == scorer.player.name }
        ?.imageUrl


    // Fila principal
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .background(
                color = AppColors.CardDark, // fondo oscuro
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 10.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // ======= BLOQUE DE FOTO, NOMBRE Y EQUIPO =====
        Row(
            modifier = Modifier.weight(2f),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Asignamos a la foto del jugador un fondo blanco circular
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(Color.White, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = scorer.player.name,
                    modifier = Modifier.size(32.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column {

                // Nombre del jugador
                Text(
                    text = scorer.player.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )

                // Nombre del equipo
                Text(
                    text = scorer.team.name,
                    color = Color(0xFFBFC7D5),
                    fontSize = 10.sp
                )
            }

        }

        // ===== BLOQUE DE COLUMNAS DE ESTADÍSTICAS =====

        // G/A --> Goles + Asistencias
        Text("${scorer.scorerPoints}", color = Color.White, modifier = Modifier.weight(0.6f))

        // Goles
        Text("${scorer.goals}", color = Color.White, modifier = Modifier.weight(0.6f))

        // Asistencias
        Text("${scorer.assists ?: 0}", color = Color.White, modifier = Modifier.weight(0.6f))

        // Media G/A por partido
        Text(
            text = "%.2f".format(scorer.scorerPointsPerMatch),
            color = Color.White,
            modifier = Modifier.weight(0.8f),
            fontSize = 12.sp
        )

        // Media goles por partido
        Text(
            text = "%.2f".format(scorer.goalsPerMatch),
            color = Color.White,
            modifier = Modifier.weight(0.8f),
            fontSize = 12.sp
        )
    }
}



@Composable
fun ScorersList(scorers: List<Scorer>, players: List<FirebasePlayer>, isLoading: Boolean, error: String?) {

    when {

        // Si está cargando...
        isLoading -> {
            CircularProgressIndicator(color = Color.White)
        }

        // Si hay error
        error != null -> {
            Text(
                text = error,
                color = Color.Red
            )
        }

        // Si no hay error ni estado de carga, mostramos la lista de goleadores
        else -> {
                Column {
                    // Cabecera para la tabla
                    ScorersHeader()

                    // Lista de goleadores
                    LazyColumn {
                        items(scorers) { scorer ->
                            ScorerItem(
                                scorer = scorer,
                                players = players
                            )
                        }
                    }
                }
            }
        }
    }


@Composable
fun ScorersCardsList(scorers: List<Scorer>, players: List<FirebasePlayer>, isLoading: Boolean, error: String?){

    when {
        // Si está cargando...
        isLoading -> {
            CircularProgressIndicator(color = Color.White)
        }

        // Si hay error...
        error != null -> {
            Text(text = error, color = Color.Red)
        }

        else -> {
            LazyColumn {
                itemsIndexed(scorers) { index, scorer ->
                    ScorersCardItem(
                        scorer = scorer,
                        position = index + 1,
                        players = players
                    )
                }
            }
        }
    }
}


@Composable
fun ScorersCardItem(scorer: Scorer, position: Int, players: List<FirebasePlayer>){

    // Asignamos el color de fondo según la posición del goleador
    // Color de medallas para primero, segundo, tercero
    val cardBrush = when (position) {
        1 -> Brush.horizontalGradient(listOf(Color(0xFFE4C313), Color(0xFFD9C878))) // Oro
        2 -> Brush.horizontalGradient(listOf(Color(0xFFC9CED6), Color(0xFFE3E7EF))) // Plata
        3 -> Brush.horizontalGradient(listOf(Color(0xFFC06A00), Color(0xFFD4A06A))) // Bronce
        else -> Brush.horizontalGradient(listOf(AppColors.CardDark, AppColors.CardSoftStrong)) // Normal
    }

    // Buscamos la imagen del jugador en la lista de jugadores
    val imageUrl = players
        .firstOrNull { it.playerName == scorer.player.name }
        ?.imageUrl


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp)
            .background(
                brush = cardBrush, // Asignamos el fondo con degradado hecho previamente
                shape = RoundedCornerShape(22.dp)
            )
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Foto del jugador obtenida desde Firestore
        AsyncImage(
            model = imageUrl,
            contentDescription = scorer.player.name,
            modifier = Modifier.size(46.dp)
        )


        // Posición del jugador (#1, #2...)
        Text(
            text = "$position.",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(38.dp)
        )



        // Escudo del equipo del goleador
        AsyncImage(
            model = scorer.team.crest,
            contentDescription = scorer.team.name,
            modifier = Modifier.size(36.dp)
        )

        // Columna con nombre del jugador y equipo
        Column(
            modifier = Modifier
                .weight(1f) // Ocupando todo el espacio disponible
                .padding(start = 10.dp)
        ) {

            // Nombre del jugador
            Text(
                text = scorer.player.name,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            // Número de goles (DATO PRINCIPAL)
            Text(
                text = "${scorer.goals} GOLES",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}



// Funcion para la cabecera de la lista de goleadores
@Composable
fun ScorersHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("JUGADOR", color = Color.White, modifier = Modifier.weight(2f))
        Text("G/A", color = Color.White, modifier = Modifier.weight(0.6f))
        Text("G", color = Color.White, modifier = Modifier.weight(0.6f))
        Text("A", color = Color.White, modifier = Modifier.weight(0.6f))
        Text("⌀ G/A", color = Color.White, modifier = Modifier.weight(0.8f))
        Text("⌀ G", color = Color.White, modifier = Modifier.weight(0.8f))
    }
}


// Función que actúa como selector visual para cambiar la vista entre tipo cards y tipo tabla general
@Composable
fun ScorersViewSelector(selectedMode: String, onModeSelected: (String) -> Unit) {

    // Botones para cambiar de vista
    val modes = listOf("CARDS", "TABLA")

    Row(
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        modes.forEach { mode ->
            val selected = selectedMode == mode

        Text(
            text = mode,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,

            modifier = Modifier
                .padding(end = 8.dp)
                .background(
                    color = if (selected) AppColors.AccentGreen else AppColors.CardDark,
                    shape = RoundedCornerShape(20.dp)
                )
                .clickable { onModeSelected(mode) }
                .padding(horizontal = 14.dp, vertical = 8.dp)
        )

        }
    }
}


// Funcion composable para mostrar item de partidos sin el badge del resultado
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PartidoRankingItem(match: Match) {
    PartidoItem(
        match = match,
        teamId = match.homeTeam.id,
        isFinished = match.status == "FINISHED",
        showBadge = false
    )
}