package com.example.laligainsight.iu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material3.*
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
import com.example.laligainsight.modelo.Scorer
import java.util.Locale
import androidx.compose.material.icons.filled.Person
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@Composable
fun CompareScreen(
    scorers: List<Scorer>,
    onHomeClick: () -> Unit,
    onRankingClick: () -> Unit,
    onCompareClick: () -> Unit,
    onProfileClick: () -> Unit
){
    var player1 by remember { mutableStateOf<Scorer?>(null) }
    var player2 by remember { mutableStateOf<Scorer?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.MainBackgroundBrush)
            .statusBarsPadding()
    ) {

        ScreenHeader(
            title = "Comparar jugadores",
            subtitle = "Analiza goles, asistencias y rendimiento",
            badge = "Comparador",
            icon = Icons.Default.CompareArrows
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ){

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                PlayerSelector(
                    title = "Jugador 1",
                    scorers = scorers,
                    selected = player1,
                    onSelected = { player1 = it },
                    modifier = Modifier.weight(1f)
                )

                PlayerSelector(
                    title = "Jugador 2",
                    scorers = scorers,
                    selected = player2,
                    onSelected = { player2 = it },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                PlayerCompareCard(
                    scorer = player1,
                    modifier = Modifier.weight(1f)
                )

                PlayerCompareCard(
                    scorer = player2,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Comparación",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (player1 != null && player2 != null) {
                val p1 = player1!!
                val p2 = player2!!

                CompareStatRow(
                    title = "Goles",
                    value1 = p1.goals.toString(),
                    value2 = p2.goals.toString(),
                    number1 = p1.goals.toDouble(),
                    number2 = p2.goals.toDouble()
                )

                CompareStatRow(
                    title = "Asistencias",
                    value1 = (p1.assists ?: 0).toString(),
                    value2 = (p2.assists ?: 0).toString(),
                    number1 = (p1.assists ?: 0).toDouble(),
                    number2 = (p2.assists ?: 0).toDouble()
                )

                CompareStatRow(
                    title = "Partidos",
                    value1 = p1.playedMatches.toString(),
                    value2 = p2.playedMatches.toString(),
                    number1 = p1.playedMatches.toDouble(),
                    number2 = p2.playedMatches.toDouble()
                )

                val p1GA = p1.goals + (p1.assists ?: 0)
                val p2GA = p2.goals + (p2.assists ?: 0)

                CompareStatRow(
                    title = "Goles + asistencias",
                    value1 = p1GA.toString(),
                    value2 = p2GA.toString(),
                    number1 = p1GA.toDouble(),
                    number2 = p2GA.toDouble()
                )

                CompareStatRow(
                    title = "Goles / partido",
                    value1 = formatDecimal(safeDivide(p1.goals, p1.playedMatches)),
                    value2 = formatDecimal(safeDivide(p2.goals, p2.playedMatches)),
                    number1 = safeDivide(p1.goals, p1.playedMatches),
                    number2 = safeDivide(p2.goals, p2.playedMatches)
                )

                CompareStatRow(
                    title = "G+A / partido",
                    value1 = formatDecimal(safeDivide(p1GA, p1.playedMatches)),
                    value2 = formatDecimal(safeDivide(p2GA, p2.playedMatches)),
                    number1 = safeDivide(p1GA, p1.playedMatches),
                    number2 = safeDivide(p2GA, p2.playedMatches)
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
        AppBottomBar(
            selectedTab = "Compare",
            onHomeClick = onHomeClick,
            onRankingClick = onRankingClick,
            onCompareClick = onCompareClick,
            onProfileClick = onProfileClick
        )
    }
}
@Composable
fun PlayerSelector(
    title: String,
    scorers: List<Scorer>,
    selected: Scorer?,
    onSelected: (Scorer) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(
            text = title.uppercase(),
            color = Color(0x99FFFFFF),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { expanded = true },
            colors = ButtonDefaults.buttonColors(
                containerColor = AppColors.CardSoftStrong
            ),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = selected?.player?.name ?: "Seleccionar",
                color = Color.White,
                maxLines = 1
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            scorers.forEach { scorer ->
                DropdownMenuItem(
                    text = {
                        Text(scorer.player.name)
                    },
                    onClick = {
                        onSelected(scorer)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun PlayerCompareCard(
    scorer: Scorer?,
    modifier: Modifier = Modifier
) {
    var imageUrl by remember { mutableStateOf("") }

    LaunchedEffect(scorer?.player?.name, scorer?.team?.name) {
        imageUrl = ""

        if (scorer != null) {
            imageUrl = getPlayerImageUrlFromFirebase(
                playerName = scorer.player.name,
                teamName = scorer.team.name
            )
        }
    }

    Card(
        modifier = modifier.height(190.dp),
        colors = CardDefaults.cardColors(
            containerColor = AppColors.CardSoftStrong
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(78.dp)
                    .clip(CircleShape)
                    .background(
                        if (scorer == null) Color.Black
                        else Color.White
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (scorer != null && imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = scorer.player.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = AppColors.AccentGreen,
                        modifier = Modifier.size(42.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = scorer?.player?.name ?: "Selecciona jugador",
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2
            )

            Text(
                text = scorer?.team?.name ?: "",
                color = Color(0x99FFFFFF),
                fontSize = 13.sp,
                maxLines = 1
            )
        }
    }
}

@Composable
fun CompareStatRow(
    title: String,
    value1: String,
    value2: String,
    number1: Double,
    number2: Double
) {
    val total = number1 + number2

    val leftWeight = if (total > 0) {
        (number1 / total).toFloat().coerceAtLeast(0.08f)
    } else {
        0.5f
    }

    val rightWeight = if (total > 0) {
        (number2 / total).toFloat().coerceAtLeast(0.08f)
    } else {
        0.5f
    }

    val color1 = if (number1 >= number2) AppColors.AccentGreen else Color.White
    val color2 = if (number2 >= number1) AppColors.AccentGreen else Color.White

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 14.dp),
        colors = CardDefaults.cardColors(
            containerColor = AppColors.CardSoftStrong
        ),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = value1,
                    color = color1,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = title.uppercase(),
                    color = Color(0x99FFFFFF),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(2f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Text(
                    text = value2,
                    color = color2,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.End
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .background(Color(0x22000000))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(leftWeight)
                        .background(
                            if (number1 >= number2) AppColors.AccentGreen
                            else Color(0x55FFFFFF)
                        )
                )

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(rightWeight)
                        .background(
                            if (number2 >= number1) AppColors.AccentGreen
                            else Color(0x55FFFFFF)
                        )
                )
            }
        }
    }
}
fun safeDivide(value: Int, total: Int): Double {
    return if (total > 0) value.toDouble() / total.toDouble() else 0.0
}

fun formatDecimal(value: Double): String {
    return String.format(Locale.US, "%.2f", value)
}

suspend fun getPlayerImageUrlFromFirebase(
    playerName: String,
    teamName: String
): String {
    val db = FirebaseFirestore.getInstance()

    val snapshot = db.collection("player_images")
        .whereEqualTo("playerName", playerName)
        .whereEqualTo("teamName", teamName)
        .limit(1)
        .get()
        .await()

    return snapshot.documents.firstOrNull()
        ?.getString("imageUrl")
        ?: ""
}