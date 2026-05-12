package com.example.laligainsight.iu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.laligainsight.modelo.RatingSummary
import com.example.laligainsight.modelo.Team

@Composable
fun TeamDetailHeader(
    team: Team,
    gradientColors: List<Color>,
    ratingSummary: RatingSummary,
    onBackClick: () -> Unit,
    onRatingSelected: (Int) -> Unit
) {
    // Cabecera superior del detalle del equipo.
    // Aquí se muestra el degradado del equipo, botón de volver, escudo, nombre, estadio y valoración.
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(gradientColors))
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 34.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Fila superior con el botón de volver y el título de la pantalla.
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón para volver a la pantalla anterior.
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Título fijo de la cabecera.
                Text(
                    text = "Detalle del equipo",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Contenedor circular blanco donde se coloca el escudo del equipo.
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .background(Color.White, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                // Imagen del escudo cargada desde la URL que viene de la API.
                AsyncImage(
                    model = team.crest,
                    contentDescription = team.name,
                    modifier = Modifier.size(80.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Nombre del equipo seleccionado.
            Text(
                text = team.name,
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Estadio del equipo. Si la API no lo trae, se muestra un texto por defecto.
            Text(
                text = team.venue ?: "Estadio no disponible",
                color = Color(0xFFE5E5E5),
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Componente de estrellas para mostrar y enviar la valoración del equipo.
            RatingStars(
                summary = ratingSummary,
                onRatingSelected = onRatingSelected
            )
        }
    }
}