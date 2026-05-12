package com.example.laligainsight.iu
import android.os.Build
import android.provider.Telephony
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.laligainsight.api.RetrofitCliente
import com.example.laligainsight.modelo.Match
import com.example.laligainsight.modelo.Player
import com.example.laligainsight.modelo.PlayerExtraInfo
import com.example.laligainsight.modelo.RatingSummary
import com.example.laligainsight.modelo.Standing
import com.example.laligainsight.modelo.StandingTeam
import com.example.laligainsight.modelo.Team
import com.example.laligainsight.repository.RatingRepository
import com.example.laligainsight.viewmodel.PlayersViewModel
import kotlinx.coroutines.launch


// función donde se muestra el detalle de cada equipo
// recibe un objeto Team, que es el equipo que hemos pulsado
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TeamDetailScreen(team: Team, onBackClick: () -> Unit, onPlayerClick: (Player) -> Unit) {
    // Variable para guardar los colores del equipo y reutilizar el mismo estilo
    val gradientColors = getTeamGradient(team.name)

    // Variable para estado de la pestaña seleccionada
    val selectedTab = remember { mutableStateOf("Resumen") }

    // Variable para los jugadores
    var players by remember { mutableStateOf<List<Player>>(emptyList()) }

    // Variable para guardar los partidos de un equipo (EN CONCRETO)
    var matches by remember { mutableStateOf<List<Match>>(emptyList()) }


    // Variable para guardar los datos del equipo en clasificacion
    var teamStanding by remember { mutableStateOf<StandingTeam?>(null) }

    // --- VARIABLES PARA EL VIEWMODEL DE JUGADORES Y FIREBASE ---
    val playersViewModel: PlayersViewModel = viewModel()
    val firebasePlayers by playersViewModel.players.collectAsState()

    // Variable para gestionar las valoraciones en Firebase
    val ratingRepository = remember { RatingRepository() }

    // Estado de donde guardamos la media de valoraciones y votos
    var ratingSummary by remember { mutableStateOf(RatingSummary()) }

    // Variable scope para poder lanzar corrutinas (a su vez es necesario para las llamadas async)
    // Las llamadas async son llamadas que no bloquean el hilo principal
    val scope = rememberCoroutineScope()



    // Ejecuta cuando cambia el ID del equipo o se entra por primera vez
    LaunchedEffect(team.id) {

        // Cogemos la valoración del equipo
        ratingSummary = ratingRepository.getRatingSummary(
            entityType = "team", // indicamos que es un equipo
            entityId = team.id.toString() // el id del equipo
        )


        try {
            // 1. Llamamos a la API para traer el detalle del equipo
            val response = RetrofitCliente.api.getTeamDetail(team.id)

            // Si la respuesta es exitosa...
            players = if (response.isSuccessful) {

                // Obtenemos los jugadores
                response.body()?.squad ?: emptyList()
            } else {

                // Si la respuesta falla --> lista vacía
                emptyList()
            }

            // Obtenemos la clasificacion llamando a la API
            val standingsResponse = RetrofitCliente.api.getStandings()

            // Buscamos el equipo dentro de la clasificacion TOTAL
            teamStanding = standingsResponse.standings
                .firstOrNull { it.type == "TOTAL" }
                ?.table
                ?.firstOrNull { it.team.id == team.id }


            // Con este try evitamos que se rompan otras ventanas
            try {
                // Obtenemos los partidos del equipo en concreto
                val matchesResponse = RetrofitCliente.api.getTeamMatches(team.id)

                // Guardamos la lista de partidos obtenidos
                matches = matchesResponse.matches

                // PRUEBA: Variables para filtrar entre partidos finalizados y partidos próximos
                val finishedMatches = matches.filter { it.status == "FINISHED" }
                val upcomingMatches = matches.filter { it.status == "SCHEDULED" }

                println("Partidos FINAlIZADOS: ${finishedMatches.size}")
                println("Partidos PROXIMOS: ${upcomingMatches.size}")
                println("Partidos RECIBIDOS: ${matches.size}")

            } catch (e: Exception) {
                println("ERROR CARGANDO PARTIDOS: ${e.message}")
                e.printStackTrace()
            }


        } catch (e: Exception) {
            println("ERROR EN TEAM DETAIL: ${e.message}")
            e.printStackTrace()
        }
    }

    // Column = contenedor vertical principal
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.MainBackgroundBrush)
            .verticalScroll(rememberScrollState())
    ) {


        // ======================= CABECERA VISUAL DEL EQUIPO ===============================

        // Sustituimos la card blanca básica por un bloque superior con degradado utilizado previamente
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(gradientColors)
                )
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 34.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // FILA SUPERIOR DONDE VA EL BOTON DE VOLVER
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { onBackClick() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Detalle del equipo",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ESCUDO DEL EQUIPO
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .background(Color.White, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = team.crest,
                        contentDescription = team.name,
                        modifier = Modifier.size(80.dp), // el escudo ahora lo ponemos dentro y no se recorta
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // NOMBRE DEL EQUIPO
                Text(
                    text = team.name,
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // ESTADIO
                Text(
                    text = team.venue ?: "Estadio no disponible",
                    color = Color(0xFFE5E5E5),
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(18.dp))

                RatingStars(
                    summary = ratingSummary,
                    onRatingSelected = { rating ->
                        scope.launch {
                            ratingRepository.rateEntity(
                                entityType = "team",
                                entityId = team.id.toString(),
                                rating = rating
                            )

                            ratingSummary = ratingRepository.getRatingSummary(
                                entityType = "team",
                                entityId = team.id.toString()
                            )
                        }
                    }
                )
            }
        }

        // ===================== VALORACIONES ==========================


        Spacer(modifier = Modifier.height(16.dp))

        // ====== PESTAÑAS  ==========
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            listOf("Resumen", "Plantilla", "Partidos").forEach { tab ->
                // Cada pestaña se representa con texto + línea inferior
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedTab.value = tab }
                        .padding(vertical = 16.dp)
                ) {
                    Text(
                        text = tab,
                        color = if (tab == selectedTab.value) Color.White else Color.Gray,
                        fontSize = 16.sp,
                        fontWeight = if (selectedTab.value == tab) FontWeight.Bold else FontWeight.Normal
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    // Línea inferior para resaltar la pestaña activa
                    Box(
                        modifier = Modifier
                            .height(3.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp)
                            .background(
                                if (tab == selectedTab.value) AppColors.AccentGreen else Color.Transparent,
                                shape = RoundedCornerShape(50)
                            )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ========= Si la pestaña seleccionada es "Resumen", mostramos la card resumen ============
        if (selectedTab.value == "Resumen") {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = AppColors.CardSoftStrong)
            ) {

                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    // Funcion para mostrar el resumen
                    ResumenEquipo(
                        teamStanding = teamStanding,
                        matches = matches,
                        teamId = team.id

                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        thickness = 1.dp,
                        color = Color(0xFF1C2B4A)
                    )
                }
            }
        }


        // ========= Si la pestaña seleccionada es "Plantilla", mostramos contenido provisional ===========
        if (selectedTab.value == "Plantilla") {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = AppColors.CardSoftStrong)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Plantilla",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        players.forEach { player ->

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onPlayerClick(player) },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = AppColors.ButtonSecondary
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    // F0TO DEL JUGADOR
                                    // Buscamos si hay imagen en Firebase
                                    val imageUrl = firebasePlayers
                                        .firstOrNull { it.playerName == player.name }
                                        ?.imageUrl

                                    Box(
                                        modifier = Modifier
                                            .size(68.dp)
                                            .background(Color.White, shape = CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (!imageUrl.isNullOrBlank()) {
                                            AsyncImage(
                                                model = imageUrl,
                                                contentDescription = player.name,
                                                modifier = Modifier
                                                    .size(52.dp), // más pequeña dentro del círculo
                                                contentScale = ContentScale.Fit
                                            )
                                        } else {
                                            Text(
                                                text = player.name.first().toString(),
                                                color = Color.DarkGray,
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }



                                    Spacer(modifier = Modifier.width(12.dp))

                                    // NOMBRE Y POSICIÓN
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = player.name,
                                            color = Color.White,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )

                                        Text(
                                            text = traducirPosicion(player.position),
                                            color = Color(0xFFB0B0B0),
                                            fontSize = 13.sp
                                        )

                                        // --- INDICADOR DE FLECHA ---
                                        Text(
                                            text = "→",
                                            color = Color.Gray
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(20.dp))
                            }
                        }
                    }
                }
            }
        }

        // ======= Si la pestaña seleccionada es "Partidos", mostramos contenido provisional =======
        if (selectedTab.value == "Partidos") {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                // Estado para la pestaña seleccionada
                var selectedMatchTab by remember { mutableStateOf("Resultados") }

                // ==== Variables para dividir entre resultados y próximos partidos  ====
                val resultados = matches
                    .filter { it.status == "FINISHED" }
                    .reversed() // Con esto, invertimos la lista
                // Salen primero los últimos partidos jugados

                val proximos = matches.filter {
                    it.status == "SCHEDULED" || it.status == "TIMED"
                }


                // El usuario puede elegir entre consultar los próximos partidos o resultados

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    BotonPartidos(
                        text = "RESULTADOS",
                        selected = selectedMatchTab == "Resultados",
                        modifier = Modifier.weight(1f)
                    ) {
                        selectedMatchTab = "Resultados"
                    }

                    BotonPartidos(
                        text = "PRÓXIMOS",
                        selected = selectedMatchTab == "Próximos",
                        modifier = Modifier.weight(1f)
                    ) {
                        selectedMatchTab = "Próximos"
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))


                // ============ MUESTREO DE PARTIDOS ==========================0
                if (selectedMatchTab == "Resultados") {

                    // Column donde mostramos la lista de resultados
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

                        // Recorremos los partidos finalizados
                        resultados.forEach { match ->

                            // Pintamos cada partido usando la funcion composable PartidoItem
                            PartidoItem(
                                match = match,
                                teamId = team.id,
                                isFinished = true
                            )
                        }
                    }

                } else {


                    // Cuando el usuario pulse PRÓXIMOS dentro de la pestaña Partidos
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {


                        // Recorremos los partidos pendientes
                        /* No quedarán muchos ya que estamos en mayo... */
                        proximos.forEach { match ->

                            // Al ser proximo, no hay resultado todavía
                            // Pintamos el partido sin marcador
                            PartidoItem(
                                match = match,
                                teamId = team.id,
                                isFinished = false // Gracias a esto indicamos que es próximo
                            )
                        }
                    }
                }
            }

        }
    }
}


// Funcion para calcular el resultado del partido
// Utilizada para calcular la racha de los últimos 5 partidos
fun calcularResultadoPartido(match: Match, teamId: Int): String{

    // Variables para goles del equipo local y visitante
    val homeGoals = match.score.fullTime.home ?:0
    val awayGoals = match.score.fullTime.away ?:0

    // Comprobamos si el equipo es loca
    val isHomeTeam = match.homeTeam.id == teamId

    return when {
        homeGoals == awayGoals -> "E"
        isHomeTeam && homeGoals > awayGoals -> "G"
        isHomeTeam && homeGoals < awayGoals -> "P"
        !isHomeTeam && awayGoals > homeGoals -> "G"
        else -> "P"
    }
}


// Funcion para traducir el nombre de la posicion en el campo a español.
fun traducirPosicion(position: String?): String {
    return when (position) {
        "Goalkeeper" -> "Portero"
        "Centre-Back" -> "Defensa central"
        "Left-Back" -> "Lateral izquierdo"
        "Right-Back" -> "Lateral derecho"
        "Defence" -> "Defensa"

        "Defensive Midfield" -> "Mediocentro defensivo"
        "Central Midfield" -> "Centrocampista"
        "Attacking Midfield" -> "Mediocentro ofensivo"
        "Midfield" -> "Centrocampista"
        "Left Midfield" -> "Interior izquierdo"
        "Right Midfield" -> "Interior derecho"

        "Left Winger" -> "Extremo izquierdo"
        "Right Winger" -> "Extremo derecho"
        "Offence" -> "Atacante"
        "Second Striker" -> "Segundo delantero"
        "Centre-Forward" -> "Delantero centro"
        "Attacker" -> "Delantero"


        else -> position ?: "Posición no disponible"
    }
}


// =================  FUNCION COMPOSABLE DETALLE DEL JUGADOR ====================
// Nombre, posicion, rating...
@Composable
fun PlayerDetailScreen(
    player: Player,
    onBackClick: () -> Unit
) {

    val extraInfo = PlayerExtraInfo(
        fullName = player.name,
        position = traducirPosicion(player.position),
        birthday = player.dateOfBirth ?: "No disponible",
        nationality = player.nationality ?: "No disponible",
        currentTeam = "No disponible",
        contract = "No disponible",
        marketValue = "No disponible",
        preferredFoot = "No disponible",
        matchesInDatabase = "No disponible",
        relatedItems = emptyList()
    )

    val ratingRepository = remember { RatingRepository() }
    var ratingSummary by remember { mutableStateOf(RatingSummary()) }
    val scope = rememberCoroutineScope()

    val playersViewModel: PlayersViewModel = viewModel()
    val firebasePlayers by playersViewModel.players.collectAsState()

    val firebasePlayer = firebasePlayers.firstOrNull {
        it.playerName == player.name
    }

    LaunchedEffect(player.id) {
        ratingSummary = ratingRepository.getRatingSummary(
            entityType = "player",
            entityId = player.id.toString()
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        // ================= FOTO FONDO =================

        AsyncImage(
            model = firebasePlayer?.imageUrl,
            contentDescription = player.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(690.dp)
                .align(Alignment.TopCenter),
            contentScale = ContentScale.Crop,
            alpha = 0.70f
        )

        // Overlay oscuro premium
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x33000000),
                            Color(0x88000000),
                            Color(0xEE020617)
                        )
                    )
                )
        )

        // Glow azul lateral
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xAA0A1B3D),
                            Color.Transparent
                        )
                    )
                )
        )

        // ================= CONTENIDO =================

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 22.dp)
        ) {

            // BACK
            IconButton(
                onClick = onBackClick
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            // NOMBRE
            Text(
                text = extraInfo.fullName,
                color = Color.White,
                fontSize = 42.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 42.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // POSICIÓN
            Text(
                text = extraInfo.position,
                color = Color(0xFF57F287),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.weight(1f))

            // ================= CARDS =================

            PlayerFloatingInfoCard(
                title = "Nacimiento",
                value = extraInfo.birthday,
                accent = Color(0xFFFFB020)
            )

            Spacer(modifier = Modifier.height(16.dp))

            PlayerFloatingInfoCard(
                title = "Nacionalidad",
                value = extraInfo.nationality,
                accent = Color(0xFFFF4D8D)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ================= VALORACIÓN =================

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                shape = RoundedCornerShape(30.dp),
                border = BorderStroke(
                    1.dp,
                    Color(0x443B82F6)
                ),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0x331E3A8A)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 22.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Valora este jugador",
                        color = Color(0xFF57F287),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    RatingStars(
                        summary = ratingSummary,
                        onRatingSelected = { rating ->
                            scope.launch {
                                ratingRepository.rateEntity(
                                    entityType = "player",
                                    entityId = player.id.toString(),
                                    rating = rating
                                )

                                ratingSummary = ratingRepository.getRatingSummary(
                                    entityType = "player",
                                    entityId = player.id.toString()
                                )
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun PlayerFloatingInfoCard(
    title: String,
    value: String,
    accent: Color
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp),

        shape = RoundedCornerShape(30.dp),

        border = BorderStroke(
            width = 1.dp,
            color = Color(0x443B82F6)
        ),

        colors = CardDefaults.cardColors(
            containerColor = Color(0x331E3A8A)
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp),

            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = title,
                color = accent,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

// Funcion para mostrar una linea de información del jugador
@Composable
fun PlayerInfoLine(
    title: String,
    value: String
) {
    Column(
        modifier = Modifier.padding(bottom = 14.dp)
    ) {
        Text(
            text = title.uppercase(),
            color = Color(0x99FFFFFF),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = value,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}


// ===== FUNCIONES COMPLEMENTARIAS PARA LA PESTAÑA RESUMEN DEL EQUIPO ===

// Composable auxiliar de etiqueta de sección (títulos de secciones)
@Composable
fun SectionLabel(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(Color(0xFF4A90E2), shape = CircleShape)
        )

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = text.uppercase(),
            color = Color(0xFF8899BB),
            fontSize = 15.sp,
            letterSpacing = 1.sp,
            fontWeight = FontWeight.Bold
        )
    }
}


// Composable auxiliar de stat pequeña
@Composable
fun MiniStatCard(
    value: String,
    label: String,
    valueColor: Color = Color.White,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C2B4A))
    ) {

        Column(modifier = Modifier.padding(14.dp)) {
            // Línea de color arriba
            Box(
                modifier = Modifier
                    .width(28.dp)
                    .height(3.dp)
                    .background(valueColor, shape = RoundedCornerShape(2.dp))
            )
            Spacer(modifier = Modifier.height(10.dp))

            Text(value, color = valueColor, fontSize = 28.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(2.dp))

            Text(label, color = Color(0xFF5A6A8A), fontSize = 13.sp)
        }
    }
}

// Card para mostrar goles a favor y goles en contra
@Composable
fun GoalStatCard(
    value: String,
    label: String,
    icon: String,
    mainColor: Color,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C2B4A))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = icon,
                    color = mainColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = value,
                    color = mainColor,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = label,
                    color = Color(0xFF8D99B8),
                    fontSize = 12.sp
                )
            }
        }
    }
}


// Función para el circulo que indica un resultado en la racha de los últimos 5 partidos
@Composable
fun CirculoRacha(resultado: String){

    // Definimos color en función del resultado (victoria, empate, derrota)
    val color = when (resultado) {
        "G" -> Color(0xFF4CAF50)
        "E" -> Color(0xFFFFC107)
        "P" -> Color(0xFFEF5350)
        else -> Color.Gray
    }

    // Box para el circulo donde se muestra el resultado
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(color),
        contentAlignment = Alignment.Center
    ){
        Text(
            text = resultado,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}


// Funcion para el boton de partidos
@Composable
fun BotonPartidos(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
){
    Card(
        modifier = modifier
            .height(42.dp)
            .clickable{ onClick()},
        shape = RoundedCornerShape(50),

        // Color en función de si esta seleccionado o no
        colors = CardDefaults.cardColors(
            containerColor = if (selected) AppColors.AccentGreen else AppColors.ButtonSecondary)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


// Funcion composable para mostrar resumen del equipo: clasificacion, estadisticas, racha de partidos...
@Composable
fun ResumenEquipo(
    teamStanding: StandingTeam?, // datos de clasificación
    matches: List<Match>, // datos de la lista de partidos
    teamId: Int // Con el id del equipo podemos saber si es local o visitante (calc. resultado)
){

    // Contenedor vertical ; separacion entre bloques
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

        // Si tenemos datos de clasificación ya...
        if (teamStanding != null) {

            // Variable de calculo de diferencia de goles
            val difGoles = teamStanding.goalDifference

            // Añadimos + o - dependiendo de la diferencia de goles
            val diffStr = if (difGoles >= 0) "+$difGoles" else "-$difGoles"


            // ========== PARTE DE CLASIFICACIÓN =======================0
            SectionLabel("CLASIFICACIÓN")

            // Creamos un grid y lo implementamos con estadísticas principales
            LazyVerticalGrid(
                columns = GridCells.Fixed(2), // 2 columnas
                modifier = Modifier.height(295.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                userScrollEnabled = false // desactivamos el scroll interno
            ) {

                // ITEM ---> ESTADISTICA

                item { MiniStatCard("${teamStanding.position}º", "Posición") }
                item { MiniStatCard("${teamStanding.points}", "Puntos") }
                item { MiniStatCard("${teamStanding.won}", "Victorias") }
                item { MiniStatCard("${teamStanding.draw}", "Empates") }
                item { MiniStatCard("${teamStanding.lost}", "Derrotas") }
                item { MiniStatCard(diffStr, "Dif. goles") }
            }


            // ========== RACHA ÚLTIMOS 5 PARTIDOS ==========

            // Variable para recoger el resultado de los ultimos 5 partidos
            val ultimos5Partidos = matches
                .filter { it.status == "FINISHED" }
                .sortedByDescending { it.utcDate } // Ordenamos por fecha
                .take(5) // Nos quedamos con los 5 ultimos

            SectionLabel("ÚLTIMOS 5 PARTIDOS")

            // Creamos la fila para mostrar los circulos de los resultados
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                // Recorremos los resultados de los últimos 5 partidos recogidos del equipo
                ultimos5Partidos.forEach { match ->

                    // Calculamos el resultado --> funcion CalcularResultadoPartido
                    val resultado = calcularResultadoPartido(match, teamId)

                    // Pintamos el circulo una vez sabido el resultado
                    CirculoRacha(resultado)
                }
            }

            // ================== GOLES ==================
            SectionLabel("GOLES")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GoalStatCard(
                    value = teamStanding.goalsFor.toString(),
                    label = "A favor",
                    icon = "↑",
                    mainColor = Color(0xFF4CAF50),
                    backgroundColor = Color(0xFF143D2A),
                    modifier = Modifier.weight(1f)
                )

                GoalStatCard(
                    value = teamStanding.goalsAgainst.toString(),
                    label = "En contra",
                    icon = "↓",
                    mainColor = Color(0xFFEF5350),
                    backgroundColor = Color(0xFF4A1E1E),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // =================== PROGRESO DE LA TEMPORADA ===================

            SectionLabel("PROGRESO DE TEMPORADA")
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "${teamStanding.playedGames} / 38 jornadas",
                    color = Color(0xFF8899BB),
                    fontSize = 17.sp
                )
                Text(
                    "${((teamStanding.playedGames / 38f) * 100).toInt()}%",
                    color = Color(0xFF4A90E2),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))


            // Barra de progreso
            LinearProgressIndicator(
                progress = { teamStanding.playedGames / 38f },
                modifier = Modifier.fillMaxWidth().height(8.dp)
                    .clip(RoundedCornerShape(6.dp)),
                color = Color(0xFF4A90E2),
                trackColor = Color(0xFF1C2B4A)
            )

        } else {

            // Mensaje mientras carga la API
            Text(
                text = "Cargando datos...",
                color = Color.White,
                fontSize = 15.sp
            )
        }
    }
}



// Funcion para pasar la hora UTC de la API a la hora española
@RequiresApi(Build.VERSION_CODES.O)
fun formatoHoraPartido(utcDate: String): String {
        return try {
        // Convertimos la hora UTC a un objeto Date
        val instant = java.time.Instant.parse(utcDate)

        // Formateamos la hora a la zona horaria de España
        val horaEspanyola = java.time.LocalDateTime.ofInstant(instant, java.time.ZoneId.of("Europe/Madrid"))

        // Devolvemos la hora formateada en HH:mm
        horaEspanyola.toLocalTime().toString().substring(0,5)

        }   catch (e: Exception) {
            // Mostramos placeHolder
            "--:--"
    }
}

// Funcion para mostrar la fecha de partido en formato dd/MM
@RequiresApi(Build.VERSION_CODES.O)
fun formatoFechaPartido(utcDate: String): String {

    return try {
        val instant = java.time.Instant.parse(utcDate)

        // Formateamos la fecha a la zona horaria de España
        val fechaEspanyola = java.time.LocalDateTime.ofInstant(
            instant,
            java.time.ZoneId.of("Europe/Madrid")
        )

        val dia = fechaEspanyola.dayOfMonth.toString().padStart(2, '0')
        val mes = fechaEspanyola.monthValue.toString().padStart(2, '0')

        // Devolvemos la fecha formateada en dd/MM
        "$dia/$mes"

    } catch (e: Exception) {
        // Mostramos placeHolder
        "--/--"
    }






}








// Función composable para mostrar items de partidos, ya sean proximos o resultados
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PartidoItem(
    match: Match,
    teamId: Int,
    isFinished: Boolean,
    showBadge: Boolean = true // se utiliza en Standings screen para mostrar los partidos sin el badge del resultado
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Asignamos la fecha del partido con formato dd.MM a la izquierda
        Text(
            text = formatoHoraPartido(match.utcDate),
            color = Color(0XFFB0B0B0),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(55.dp)
        )

        // Column para los nombres de los equipos en el centro
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ){
            EquipoLinea(match.homeTeam.crest, match.homeTeam.name)
            EquipoLinea(match.awayTeam.crest, match.awayTeam.name)
        }


            // Si el partido ha finalizado...
            if (isFinished){
                Row(verticalAlignment = Alignment.CenterVertically) {

                    // Marcador del partido u hora a la derecha
                    Row(verticalAlignment = Alignment.CenterVertically) {


                        Column(horizontalAlignment = Alignment.CenterHorizontally) {

                            // Local
                            Text(
                                text = match.score.fullTime.home?.toString() ?: "-",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )

                            // Visitante
                            Text(
                                text = match.score.fullTime.away?.toString() ?: "-",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Spacer para dejar espacio entre marcador y resultado
                    Spacer(modifier = Modifier.width(8.dp))

                    // LLamamos al badge del resultado ---> funcion composable ResultadoBadge
                    if (showBadge) {
                        ResultadoBadge(match = match, teamId = teamId)
                    }
                }

            } else {

                // Si no se ha jugado,
                // Con la funcion creada ponemos la fecha en la que se jugará el partido
                Text(
                    text = formatoFechaPartido(match.utcDate),
                    color = Color(0XFFB0B0B0),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }


// Badge para poner el resultado del partido
@Composable
fun ResultadoBadge(match: Match, teamId: Int){

    // Variables de goles para el equipo local y visitante
    val homeGoals = match.score.fullTime.home ?: 0
    val awayGoals = match.score.fullTime.away ?: 0

    // Con el id del equipo, averiguamos si es local o visitante
    // Así, dictaminamos el resultado del equipo y lo mostramos

    val isHomeTeam = match.homeTeam.id == teamId

    // Calculamos el resultado depiendo de si nuestro equipo es local o visitante
    val resultado = when {

        // Mismos goles --> EMPATE
        homeGoals == awayGoals -> "E"

        // Locales y golesLocal > golesVisitante --> GANA
        isHomeTeam && homeGoals > awayGoals -> "G"

        // Locales y golesLocal < golesVisitante --> PIERDE
        isHomeTeam && homeGoals < awayGoals -> "P"

        // Visitantes y golesLocal < golesVisitante --> GANA
        !isHomeTeam && homeGoals < awayGoals -> "G"

        // Visitantes y golesLocal > golesVisitante --> PIERDE
        !isHomeTeam && homeGoals > awayGoals -> "P"

        else -> "P"
    }

    // Asignamos color al Badge en función del resultado
    val badgeColor = when (resultado){
        "G" -> Color(0xFF4CAF50)
        "P" -> Color(0xFFEF5350)
        "E" -> Color(0xFFFFC107)
        else -> Color.Gray
    }

    // Creamos el BOX del badge
    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(badgeColor),
        contentAlignment = Alignment.Center
    ){
        Text(
            text = resultado,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}



// Funcion composable para cada linea del equipo al mostrar los partidos
@Composable
fun EquipoLinea(crest: String?, name: String){
    Row(verticalAlignment = Alignment.CenterVertically){

        // AsyncImage para mostrar el escudo del equipo
        AsyncImage(
            model = crest,
            contentDescription = name,
            modifier = Modifier.size(24.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = name,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}



