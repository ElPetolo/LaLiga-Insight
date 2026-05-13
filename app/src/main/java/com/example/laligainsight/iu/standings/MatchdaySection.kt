package com.example.laligainsight.iu

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.laligainsight.modelo.Match

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MatchdaySection(
    matches: List<Match>,
    selectedMatchday: Int,
    onMatchdaySelected: (Int) -> Unit
) {
    // El filtro se hace aquí porque esta vista es la responsable de pintar la jornada.
    val partidosJornada = matches.filter { it.matchday == selectedMatchday }

    var extendido by remember { mutableStateOf(false) }

    Box {
        Row(
            modifier = Modifier
                .background(
                    AppColors.AccentGreen,
                    RoundedCornerShape(20.dp)
                )
                .clickable {
                    extendido = true
                }
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
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

            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Abrir selector",
                tint = Color.White
            )
        }

        DropdownMenu(
            expanded = extendido,
            onDismissRequest = {
                extendido = false
            }
        ) {
            (1..38).forEach { jornada ->
                DropdownMenuItem(
                    text = {
                        Text(text = "JORNADA $jornada")
                    },
                    onClick = {
                        onMatchdaySelected(jornada)
                        extendido = false
                    }
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    LazyColumn {
        items(partidosJornada) { match ->
            PartidoRankingItem(match = match)
        }
    }
}

// Esta versión reutiliza el mismo item base de partido sin mostrar el badge del resultado.
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
