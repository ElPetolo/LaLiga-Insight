package com.example.laligainsight.iu
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.laligainsight.api.RetrofitCliente
import com.example.laligainsight.modelo.Player
import com.example.laligainsight.modelo.PlayerExtraInfo
import com.example.laligainsight.modelo.Team
import com.example.laligainsight.viewmodel.PlayersViewModel


// función donde se muestra el detalle de cada equipo
// recibe un objeto Team, que es el equipo que hemos pulsado
@Composable
fun TeamDetailScreen(team: Team, onBackClick: () -> Unit, onPlayerClick: (Player) -> Unit) {
    // Variable para guardar los colores del equipo y reutilizar el mismo estilo
    val gradientColors = getTeamGradient(team.name)

    // Variable para estado de la pestaña seleccionada
    val selectedTab = remember { mutableStateOf("Resumen") }

    // Variable para los jugadores
    var players by remember { mutableStateOf<List<Player>>(emptyList()) }


    // --- VARIABLES PARA EL VIEWMODEL DE JUGADORES Y FIREBASE ---
    val playersViewModel: PlayersViewModel = viewModel()
    val firebasePlayers by playersViewModel.players.collectAsState()


    // Ejecuta cuando cambia el ID del equipo o se entra por primera vez
    LaunchedEffect(team.id) {
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

        } catch (e: Exception) {

            // Si ocurre un error evitamos el crash y dejamos la lista vacía
            players = emptyList()
        }
    }

    // Column = contenedor vertical
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0D1F1A),
                        Color(0xFF07140F),
                        Color(0xFF020605)
                    )
                )
            )            .statusBarsPadding() // Con esto evitamos que junte con la barra de arriba
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        // CABECERA VISUAL DEL EQUIPO
        // Sustituimos la card blanca básica por un bloque superior con degradado utilizado previamente
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        listOf(
                            Color(0xFF111827),
                            gradientColors[1].copy(alpha = 0.45f),
                            gradientColors[2].copy(alpha = 0.70f)
                        )
                    ),
                    shape = RoundedCornerShape(28.dp)
                )                .padding(horizontal = 20.dp, vertical = 20.dp)
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
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // PESTAÑAS
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("Resumen", "Plantilla", "Partidos").forEach { tab ->
                // Cada pestaña se representa con texto + línea inferior
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { selectedTab.value = tab }
                        .padding(vertical = 12.dp)
                ) {
                    Text(
                        text = tab,
                        color = if (tab == selectedTab.value) Color(0xFF1D9E75) else Color(0x99FFFFFF),
                        fontSize = 16.sp,
                        fontWeight = if (selectedTab.value == tab) FontWeight.Bold else FontWeight.Normal
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    // Línea inferior para resaltar la pestaña activa
                    Box(
                        modifier = Modifier
                            .height(3.dp)
                            .width(60.dp)
                            .background(
                                if (tab == selectedTab.value) Color(0xFF1D9E75) else Color.Transparent,
                                shape = RoundedCornerShape(50)
                            )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Si la pestaña seleccionada es "Resumen", mostramos la card resumen
        if (selectedTab.value == "Resumen") {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0x141D9E75))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Resumen",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Column {
                        // FILA 1 - POSICIÓN Y PARTIDOS
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatCard(title = "Posición", value = "3º", modifier = Modifier.weight(1f))
                            StatCard(title = "Partidos", value = "30", modifier = Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        // FILA 2 - GOLES A FAVOR
                        StatCard(title = "Goles a favor", value = "55", modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        }

        // Si la pestaña seleccionada es "Plantilla", mostramos contenido provisional
        if (selectedTab.value == "Plantilla") {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0x141D9E75))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Plantilla",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Column (verticalArrangement = Arrangement.spacedBy(12.dp)){
                        players.forEach { player ->

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable{onPlayerClick(player)},
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0x221D9E75)
                                )
                           ) {
                                Row(
                                    modifier = Modifier
                                        .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                ){

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
                                    Column(modifier = Modifier.weight(1f)){
                                        Text(
                                            text = player.name,
                                            color = Color.White,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )

                                        Text(
                                            text = translatePosition(player.position),
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

        // Si la pestaña seleccionada es "Partidos", mostramos contenido provisional
        if (selectedTab.value == "Partidos") {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0x141D9E75))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Partidos",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Aquí mostraremos próximamente los partidos y resultados del equipo.",
                        color = Color(0xFFE5E5E5),
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x221D9E75))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = value,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                color = Color(0xFFB0B0B0),
                fontSize = 13.sp
            )
        }
    }
}


    @Composable
    fun PlayerDetailScreen(player: Player, onBackClick: () -> Unit) {

        val extraInfo = PlayerExtraInfo(
            fullName = player.name,
            position = translatePosition(player.position),
            birthday = player.dateOfBirth ?: "No disponible",
            nationality = player.nationality ?: "No disponible",
            currentTeam = "No disponible",
            contract = "No disponible",
            marketValue = "No disponible",
            preferredFoot = "No disponible",
            matchesInDatabase = "No disponible",
            relatedItems = emptyList()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF08142E))
                .statusBarsPadding()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "← Volver",
                color = Color.White,
                modifier = Modifier.clickable { onBackClick() }
            )

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0x141D9E75))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = extraInfo.fullName,
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

            Spacer(modifier = Modifier.height(12.dp))

            Spacer(modifier = Modifier.height(16.dp))

                    Text(text = "Día de nacimiento: ${extraInfo.birthday}", color = Color.White)
                    Text(text = "Nacionalidad: ${extraInfo.nationality}", color = Color.White)
                    Text(text = "Equipo actual: ${extraInfo.currentTeam}", color = Color.White)
                    Text(text = "Contrato: ${extraInfo.contract}", color = Color.White)
                    Text(text = "Valor de mercado: ${extraInfo.marketValue}", color = Color.White)
                    Text(text = "Pie dominante: ${extraInfo.preferredFoot}", color = Color.White)
                    Text(text = "Partidos BD: ${extraInfo.matchesInDatabase}", color = Color.White)
                }
            }
        }
    }



    fun translatePosition(position: String?): String {
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



