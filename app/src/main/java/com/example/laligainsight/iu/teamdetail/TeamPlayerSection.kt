package com.example.laligainsight.iu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.laligainsight.modelo.FirebasePlayer
import com.example.laligainsight.modelo.Player

@Composable
fun TeamPlayerSection(
    players: List<Player>,
    firebasePlayers: List<FirebasePlayer>,
    onPlayerClick: (Player) -> Unit
) {
    // Card principal de la pestaña Plantilla.
    // Dentro se muestran todos los jugadores del equipo seleccionado.
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.CardSoftStrong)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Título de la sección.
            Text(
                text = "Plantilla",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Lista vertical de jugadores.
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                players.forEach { player ->

                    // Buscamos la imagen del jugador en Firebase comparando por nombre.
                    // Si existe, se muestra; si no existe, se usa la inicial del jugador.
                    val imageUrl = firebasePlayers
                        .firstOrNull { it.playerName == player.name }
                        ?.imageUrl

                    // Card individual de cada jugador.
                    // Al pulsarla abrimos la pantalla de detalle del jugador.
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
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Círculo blanco donde va la foto del jugador o su inicial.
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .background(Color.White, shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (!imageUrl.isNullOrBlank()) {
                                    // Imagen del jugador cargada desde Firebase.
                                    AsyncImage(
                                        model = imageUrl,
                                        contentDescription = player.name,
                                        modifier = Modifier.size(52.dp),
                                        contentScale = ContentScale.Fit
                                    )
                                } else {
                                    // Placeholder si no hay imagen disponible.
                                    Text(
                                        text = player.name.first().toString(),
                                        color = Color.DarkGray,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            // Información básica del jugador.
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

                                // Flecha simple para indicar que se puede entrar al detalle.
                                Text(
                                    text = "→",
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}