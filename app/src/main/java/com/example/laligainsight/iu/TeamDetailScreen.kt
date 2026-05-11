package com.example.laligainsight.iu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.laligainsight.api.RetrofitCliente
import com.example.laligainsight.modelo.Player
import com.example.laligainsight.modelo.PlayerExtraInfo
import com.example.laligainsight.modelo.Team
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.example.laligainsight.repository.RatingRepository
import com.example.laligainsight.modelo.RatingSummary
import kotlinx.coroutines.launch

@Composable
fun TeamDetailScreen(
    team: Team,
    onBackClick: () -> Unit,
    onPlayerClick: (Player) -> Unit
) {
    val ratingRepository = remember { RatingRepository() }
    var ratingSummary by remember { mutableStateOf(RatingSummary()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(team.id) {

        ratingSummary =
            ratingRepository.getRatingSummary(
                entityType = "team",
                entityId = team.id.toString()
            )
    }

    val gradientColors = getTeamGradient(team.name)
    val selectedTab = remember { mutableStateOf("Resumen") }

    var players by remember { mutableStateOf<List<Player>>(emptyList()) }
    var playerImages by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    val db = Firebase.firestore

    LaunchedEffect(team.id) {
        try {
            val response = RetrofitCliente.api.getTeamDetail(team.id)

            if (response.isSuccessful) {
                players = response.body()?.squad ?: emptyList()

                db.collection("player_images")
                    .whereEqualTo("teamName", team.name)
                    .get()
                    .addOnSuccessListener { result ->
                        val imagesMap = mutableMapOf<String, String>()

                        for (document in result) {
                            val playerName = document.getString("playerName") ?: ""
                            val imageUrl = document.getString("imageUrl") ?: ""

                            if (playerName.isNotBlank() && imageUrl.isNotBlank()) {
                                imagesMap[playerName] = imageUrl
                            }
                        }

                        playerImages = imagesMap
                    }
            } else {
                players = emptyList()
            }
        } catch (e: Exception) {
            players = emptyList()
        }
    }

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
            )
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        TeamDetailHeader(
            team = team,
            gradientColors = gradientColors,
            onBackClick = onBackClick
        )

        Spacer(modifier = Modifier.height(20.dp))

        RatingStars(

            summary = ratingSummary,

            onRatingSelected = { rating ->

                scope.launch {

                    ratingRepository.rateEntity(
                        entityType = "team",
                        entityId = team.id.toString(),
                        rating = rating
                    )

                    ratingSummary =
                        ratingRepository.getRatingSummary(
                            entityType = "team",
                            entityId = team.id.toString()
                        )
                }
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        TeamTabs(
            selectedTab = selectedTab.value,
            onTabSelected = { selectedTab.value = it }
        )

        Spacer(modifier = Modifier.height(18.dp))

        when (selectedTab.value) {
            "Resumen" -> {
                TeamSummarySection()
            }

            "Plantilla" -> {
                TeamSquadSection(
                    players = players,
                    playerImages = playerImages,
                    onPlayerClick = onPlayerClick
                )
            }

            "Partidos" -> {
                TeamMatchesSection()
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun TeamDetailHeader(
    team: Team,
    gradientColors: List<Color>,
    onBackClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        listOf(
                            Color(0xFF111827),
                            gradientColors[1].copy(alpha = 0.42f),
                            gradientColors[2].copy(alpha = 0.72f)
                        )
                    )
                )
                .padding(horizontal = 20.dp, vertical = 22.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
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
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

                Box(
                    modifier = Modifier
                        .size(122.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = team.crest,
                        contentDescription = team.name,
                        modifier = Modifier.size(84.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = team.name,
                    color = Color.White,
                    fontSize = 31.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = team.venue ?: "Estadio no disponible",
                    color = Color(0xCCFFFFFF),
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun TeamTabs(
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        listOf("Resumen", "Plantilla", "Partidos").forEach { tab ->
            val selected = selectedTab == tab

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onTabSelected(tab) }
                    .padding(vertical = 10.dp)
            ) {
                Text(
                    text = tab,
                    color = if (selected) Color(0xFF1D9E75) else Color(0x99FFFFFF),
                    fontSize = 16.sp,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(7.dp))

                Box(
                    modifier = Modifier
                        .height(4.dp)
                        .width(62.dp)
                        .background(
                            color = if (selected) Color(0xFF1D9E75) else Color.Transparent,
                            shape = RoundedCornerShape(50.dp)
                        )
                )
            }
        }
    }
}

@Composable
fun TeamSummarySection() {
    GlassSectionCard(title = "Resumen") {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Posición",
                value = "3º",
                modifier = Modifier.weight(1f)
            )

            StatCard(
                title = "Partidos",
                value = "30",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        StatCard(
            title = "Goles a favor",
            value = "55",
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun TeamSquadSection(
    players: List<Player>,
    playerImages: Map<String, String>,
    onPlayerClick: (Player) -> Unit
) {
    GlassSectionCard(title = "Plantilla") {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            players.forEach { player ->
                PlayerSquadCard(
                    player = player,
                    imageUrl = playerImages[player.name],
                    onClick = { onPlayerClick(player) }
                )
            }
        }
    }
}

@Composable
fun TeamMatchesSection() {
    GlassSectionCard(title = "Partidos") {
        Text(
            text = "Aquí mostraremos próximamente los partidos y resultados del equipo.",
            color = Color(0xCCFFFFFF),
            fontSize = 15.sp,
            lineHeight = 21.sp
        )
    }
}

@Composable
fun GlassSectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0x141D9E75)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            content()
        }
    }
}

@Composable
fun PlayerSquadCard(
    player: Player,
    imageUrl: String?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0x221D9E75)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                if (!imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = player.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = player.name.take(1).uppercase(),
                        color = Color(0xFF111827),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = player.name,
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = translatePosition(player.position),
                    color = Color(0x99FFFFFF),
                    fontSize = 14.sp
                )
            }

            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = "Ver jugador",
                tint = Color(0xFF1D9E75),
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0x221D9E75)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = value,
                color = Color(0xFF1D9E75),
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = title,
                color = Color(0x99FFFFFF),
                fontSize = 13.sp
            )
        }
    }
}

@Composable
fun PlayerDetailScreen(
    player: Player,
    onBackClick: () -> Unit
) {
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
    val ratingRepository = remember {
        RatingRepository()
    }

    var ratingSummary by remember {
        mutableStateOf(RatingSummary())
    }

    val scope = rememberCoroutineScope()

    LaunchedEffect(player.id) {
        ratingSummary = ratingRepository.getRatingSummary(
            entityType = "player",
            entityId = player.id.toString()
        )
    }

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
            )
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Volver",
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0x141D9E75)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.padding(22.dp)
            ) {
                Text(
                    text = extraInfo.fullName,
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = extraInfo.position,
                    color = Color(0xFF1D9E75),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(18.dp))

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

                Spacer(modifier = Modifier.height(22.dp))

                PlayerInfoLine("Día de nacimiento", extraInfo.birthday)
                PlayerInfoLine("Nacionalidad", extraInfo.nationality)
                PlayerInfoLine("Equipo actual", extraInfo.currentTeam)
                PlayerInfoLine("Contrato", extraInfo.contract)
                PlayerInfoLine("Valor de mercado", extraInfo.marketValue)
                PlayerInfoLine("Pie dominante", extraInfo.preferredFoot)
                PlayerInfoLine("Partidos BD", extraInfo.matchesInDatabase)
            }
        }
    }
}

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