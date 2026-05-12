package com.example.laligainsight.iu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.laligainsight.modelo.Scorer

@Composable
fun CompareScreen(
    scorers: List<Scorer>,
    onHomeClick: () -> Unit,
    onRankingClick: () -> Unit,
    onCompareClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    // Jugadores elegidos en cada lado de la comparativa.
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
        ) {
            // Los selectores de arriba solo cambian el jugador asociado a cada columna.
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

            // Estas cards muestran la identidad visual de cada jugador seleccionado.
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
