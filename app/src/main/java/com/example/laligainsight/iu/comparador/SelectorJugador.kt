package com.example.laligainsight.iu

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
fun SelectorJugador(
    title: String,
    scorers: List<Scorer>,
    selected: Scorer?,
    onSelected: (Scorer) -> Unit,
    modifier: Modifier = Modifier
) {
    // Estado visual del menú desplegable para escoger jugador.
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        // Título pequeño para distinguir qué lado de la comparación estamos editando.
        Text(
            text = title.uppercase(),
            color = Color(0x99FFFFFF),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Botón que hace de selector visible del jugador actual.
        Button(
            onClick = { expanded = true },
            colors = ButtonDefaults.buttonColors(
                containerColor = ColoresApp.CardSoftStrong
            ),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = selected?.player?.name ?: "Seleccionar",
                color = Color.White,
                maxLines = 1
            )
        }

        // Desplegable con todos los jugadores disponibles en la comparativa.
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            scorers.forEach { scorer ->
                // Cada opción actualiza el jugador de ese lado y cierra el menú.
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
