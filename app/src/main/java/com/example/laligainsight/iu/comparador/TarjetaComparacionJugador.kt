package com.example.laligainsight.iu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.laligainsight.modelo.Scorer

@Composable
fun TarjetaComparacionJugador(
    scorer: Scorer?,
    modifier: Modifier = Modifier
) {
    // Guardamos la URL de la foto para no mezclar la consulta con la UI principal.
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
            containerColor = ColoresApp.CardSoftStrong
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        // Tarjeta resumen del jugador seleccionado en uno de los lados de la comparativa.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Si no hay jugador o foto, enseñamos el icono por defecto.
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
                    // Si encontramos foto del jugador en Firebase, la mostramos ocupando todo el círculo.
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = scorer.player.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Si no hay jugador elegido o no existe imagen, usamos un icono neutro.
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = ColoresApp.AccentGreen,
                        modifier = Modifier.size(42.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Nombre del jugador seleccionado o placeholder si aún no se ha elegido ninguno.
            Text(
                text = scorer?.player?.name ?: "Selecciona jugador",
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2
            )

            // Club del jugador para añadir contexto a la comparación.
            Text(
                text = scorer?.team?.name ?: "",
                color = Color(0x99FFFFFF),
                fontSize = 13.sp,
                maxLines = 1
            )
        }
    }
}
