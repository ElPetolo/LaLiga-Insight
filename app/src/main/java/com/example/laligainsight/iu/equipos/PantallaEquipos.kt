package com.example.laligainsight.iu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.laligainsight.modelo.Equipo

@Composable
// Pantalla principal de equipos con búsqueda, listado y acceso a la navegación inferior.
fun PantallaEquipos(
    teams: List<Equipo>,
    onTeamClick: (Equipo) -> Unit,
    onHomeClick: () -> Unit,
    onRankingClick: () -> Unit,
    onCompareClick: () -> Unit,
    onProfileClick: () -> Unit,
    notificationCount: Int,
    onNotificationsClick: () -> Unit,
) {
    var searchText by remember { mutableStateOf("") }

    // El filtro se hace en memoria porque la lista de equipos es pequeña y ya viene cargada.
    val filteredTeams = teams.filter {
        it.name.contains(searchText, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColoresApp.MainBackgroundBrush)
            .statusBarsPadding()
    ) {
        // Parte superior fija con el título de la pantalla y el buscador.
        HomeHeader(
            searchText = searchText,
            onSearchTextChange = { searchText = it },
            notificationCount = notificationCount,
            onNotificationsClick = onNotificationsClick
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Cada elemento de la lista se pinta con una card propia para que el acceso al detalle sea claro.
            items(filteredTeams) { team ->
                TeamCard(
                    team = team,
                    onClick = { onTeamClick(team) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        BarraInferiorApp(
            selectedTab = "Home",
            onHomeClick = onHomeClick,
            onRankingClick = onRankingClick,
            onCompareClick = onCompareClick,
            onProfileClick = onProfileClick
        )
    }
}

@Composable
// Cabecera de la home con el buscador y el acceso rápido a notificaciones.
fun HomeHeader(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    notificationCount: Int,
    onNotificationsClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Esta cabecera grande da contexto a la pantalla antes de mostrar resultados.
        CabeceraPantalla(
            title = "LaLiga Teams",
            subtitle = "Consulta equipos, estadios y plantillas",
            badge = "Temporada 25/26",
            icon = Icons.Default.SportsSoccer,
            actionIcon = Icons.Default.Notifications,
            onActionClick = onNotificationsClick
        )

        // Campo de búsqueda para filtrar por nombre según se escribe.
        OutlinedTextField(
            value = searchText,
            onValueChange = onSearchTextChange,
            placeholder = {
                Text("Buscar equipo...", color = ColoresApp.TextSecondary)
            },
            singleLine = true,
            shape = RoundedCornerShape(26.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar",
                    tint = ColoresApp.AccentGreen
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = ColoresApp.CardDark,
                unfocusedContainerColor = ColoresApp.CardSoft,
                focusedBorderColor = ColoresApp.AccentGreen,
                unfocusedBorderColor = ColoresApp.AccentBlue.copy(alpha = 0.55f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = ColoresApp.AccentGreen
            )
        )

        Spacer(modifier = Modifier.height(18.dp))
    }
}

@Composable
// Versión alternativa de la cabecera que quedó preparada por si se quiere usar un bloque superior más grande.
fun TopSection(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    notificationCount: Int,
    onNotificationsClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 18.dp),
        colors = CardDefaults.cardColors(
            containerColor = ColoresApp.CardSoft
        ),
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // En esta versión la cabecera y el icono de notificaciones comparten la misma tarjeta.
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CabeceraPantalla(
                    title = "LaLiga Teams",
                    subtitle = "Consulta equipos, estadios y plantillas",
                    badge = "Temporada 25/26",
                    icon = Icons.Default.SportsSoccer,
                    actionIcon = Icons.Default.Notifications,
                    onActionClick = onNotificationsClick
                )

                Box {
                    // El icono abre la bandeja de notificaciones.
                    IconButton(onClick = onNotificationsClick) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notificaciones",
                            tint = Color.White
                        )
                    }

                    if (notificationCount > 0) {
                        // Este circulito rojo funciona como badge visual del número de avisos pendientes.
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .align(Alignment.TopEnd)
                                .background(Color(0xFFE53935), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = notificationCount.toString(),
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Buscador integrado dentro de la card superior.
            OutlinedTextField(
                value = searchText,
                onValueChange = onSearchTextChange,
                placeholder = {
                    Text("Buscar equipo...", color = Color(0x99FFFFFF))
                },
                singleLine = true,
                shape = RoundedCornerShape(22.dp),
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = ColoresApp.AccentGreen
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = ColoresApp.CardDark,
                    unfocusedContainerColor = ColoresApp.CardSoft,
                    focusedBorderColor = ColoresApp.AccentGreen,
                    unfocusedBorderColor = ColoresApp.AccentBlue.copy(alpha = 0.55f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = ColoresApp.AccentGreen
                )
            )
        }
    }
}

@Composable
// Tarjeta visual de cada equipo en el listado principal.
fun TeamCard(
    team: Equipo,
    onClick: () -> Unit
) {
    val gradientColors = getTeamGradient(team.name)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(118.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = ColoresApp.CardSoft
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        listOf(
                            ColoresApp.CardDark,
                            gradientColors[1].copy(alpha = 0.45f),
                            gradientColors[2].copy(alpha = 0.70f)
                        )
                    )
                )
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Caja circular blanca para que todos los escudos mantengan el mismo encuadre.
            Box(
                modifier = Modifier
                    .size(74.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = team.crest,
                    contentDescription = team.name,
                    modifier = Modifier.size(54.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.width(18.dp))

            // Bloque central con la información textual principal del equipo.
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = team.name,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 25.sp,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = team.venue ?: "Estadio no disponible",
                    color = Color(0x99FFFFFF),
                    fontSize = 13.sp,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Flecha final para dejar claro que la tarjeta es navegable.
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = "Ir al detalle",
                tint = ColoresApp.AccentGreen,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

// Asignamos un degradado fijo por equipo para que cada card tenga una identidad reconocible.
fun getTeamGradient(teamName: String): List<Color> {
    return when (teamName) {
        "FC Barcelona" ->
            listOf(Color(0xFF1B2440), Color(0xFF1D4E9E), Color(0xFFB5123B))

        "Real Madrid CF" ->
            listOf(Color(0xFF1B2440), Color(0xFF3A4FA3), Color(0xFFD4AF37))

        "Club Atlético de Madrid" ->
            listOf(Color(0xFF1B2440), Color(0xFF6A355B), Color(0xFFD94A38))

        "Villarreal CF" ->
            listOf(Color(0xFF1B2440), Color(0xFFB3202E), Color(0xFFF2D95C))

        "Real Betis Balompié" ->
            listOf(Color(0xFF1B2440), Color(0xFF2F5D50), Color(0xFF4E9A43))

        "RC Celta de Vigo" ->
            listOf(Color(0xFF1B2440), Color(0xFF4E89D8), Color(0xFF9ED0F5))

        "Real Sociedad de Fútbol" ->
            listOf(Color(0xFF1B2440), Color(0xFF2E6FD8), Color(0xFF8DB9F5))

        "RCD Espanyol de Barcelona" ->
            listOf(Color(0xFF1B2440), Color(0xFF2D6CDF), Color(0xFF9EC5F8))

        "Getafe CF" ->
            listOf(Color(0xFF1B2440), Color(0xFF2563EB), Color(0xFF5FA8FF))

        "Athletic Club" ->
            listOf(Color(0xFF1B2440), Color(0xFF8B1E2D), Color(0xFFD62839))

        "CA Osasuna" ->
            listOf(Color(0xFF1B2440), Color(0xFF8C2332), Color(0xFF1E3A8A))

        "Girona FC" ->
            listOf(Color(0xFF1B2440), Color(0xFFB22234), Color(0xFFE76F51))

        "Rayo Vallecano de Madrid" ->
            listOf(Color(0xFF1B2440), Color(0xFF8E1F2F), Color(0xFFD62828))

        "Valencia CF" ->
            listOf(Color(0xFF1B2440), Color(0xFF3A3A3A), Color(0xFFF2B233))

        "Sevilla FC" ->
            listOf(Color(0xFF1B2440), Color(0xFF7B1E3A), Color(0xFFE10613))

        "RCD Mallorca" ->
            listOf(Color(0xFF1B2440), Color(0xFF8B1E2D), Color(0xFFD9485F))

        "Deportivo Alavés" ->
            listOf(Color(0xFF1B2440), Color(0xFF1F5AA6), Color(0xFF8CB8E8))

        "UD Las Palmas" ->
            listOf(Color(0xFF1B2440), Color(0xFFF2C94C), Color(0xFF2563EB))

        "Elche CF" ->
            listOf(Color(0xFF1B2440), Color(0xFF2F6B3B), Color(0xFFD4AF37))

        "Levante UD" ->
            listOf(Color(0xFF1B2440), Color(0xFF7A1F3D), Color(0xFF1E3A8A))

        "Real Oviedo" ->
            listOf(Color(0xFF1B2440), Color(0xFFB08D2F), Color(0xFF2563EB))

        else ->
            listOf(Color(0xFF1B2440), Color(0xFF30415F), Color(0xFF4C658D))
    }
}
