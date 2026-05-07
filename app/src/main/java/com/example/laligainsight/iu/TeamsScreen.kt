package com.example.laligainsight.iu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
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
import com.example.laligainsight.modelo.Team

@Composable
fun TeamsScreen(teams: List<Team>, onTeamClick: (Team) -> Unit, onProfileClick: () -> Unit,  onRankingClick: () -> Unit) {

    // Estado del buscador
    var searchText by remember { mutableStateOf("") }


    // Filtramos los equipos según el texto de búsqueda
    val filteredTeams = teams.filter {
        it.name.contains(searchText, ignoreCase = true)
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF08142E))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {

        // Parte superior: buscador y título
        TopSection(
            searchText = searchText,
            onSearchTextChange = { searchText = it }
        )

        // Lista de equipos
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // Recorremos los equipos filtrados
            items(filteredTeams) { team ->

                // Card de cada equipo
                TeamCard(
                    team = team,
                    onClick = { onTeamClick(team) }
                )
            }

            // Espacio extra al final, no queda tan pegado a la barra
            item {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        // Barra inferior
        BottomBar(
            onProfileClick = onProfileClick,
            onRankingClick = onRankingClick
        )
    }
}

@Composable
fun TopSection(
    searchText: String,
    onSearchTextChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF27324B))
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menú",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            OutlinedTextField(
                value = searchText,
                onValueChange = onSearchTextChange,
                placeholder = {
                    Text("Search teams...", color = Color.DarkGray)
                },
                singleLine = true,
                shape = RoundedCornerShape(30.dp),
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = Color.Black
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF0F0F0),
                    unfocusedContainerColor = Color(0xFFF0F0F0),
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "LaLiga Teams",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun TeamCard(
    team: Team,
    onClick: () -> Unit
) {
    val gradientColors = getTeamGradient(team.name)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(126.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = Brush.horizontalGradient(gradientColors))
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(82.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = team.crest,
                    contentDescription = team.name,
                    modifier = Modifier.size(60.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.width(18.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = team.name,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 28.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = team.venue ?: "Estadio no disponible",
                    color = Color(0xFFE5E5E5),
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = "Ir al detalle",
                tint = Color.White,
                modifier = Modifier.size(34.dp)
            )
        }
    }
}

@Composable
fun BottomBar(
    onProfileClick: () -> Unit,
    onRankingClick: () -> Unit
) {
    NavigationBar(
        modifier = Modifier.navigationBarsPadding(),
        containerColor = Color(0xFF27324B)
    ) {
        NavigationBarItem(
            selected = true,
            onClick = {onRankingClick()},
            icon = {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Rankings",
                    tint = Color.White
                )
            },
            label = { Text("Rankings", color = Color.White) }
        )

        NavigationBarItem(
            selected = true,
            // todavía no lleva a nada
            onClick = {},
            icon = {
                Icon(
                    imageVector = Icons.Default.CompareArrows,
                    contentDescription = "Compare",
                    tint = Color.White
                )
            },
            label = {
                Text("Compare", color = Color.White)
            }
        )

        NavigationBarItem(
            selected = false,
            onClick = onProfileClick,
            icon = {
                Icon(
                    imageVector = Icons.Default.PersonOutline,
                    contentDescription = "Profile",
                    tint = Color.White
                )
            },
            label = {
                Text("Profile", color = Color.White)
            }
        )
    }
}

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