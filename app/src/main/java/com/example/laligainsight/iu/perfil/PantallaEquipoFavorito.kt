package com.example.laligainsight.iu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.laligainsight.autenticacion.RepositorioUsuario
import com.example.laligainsight.modelo.Equipo
import kotlinx.coroutines.launch
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton

@Composable
// Permite elegir y guardar el equipo favorito del usuario desde una rejilla sencilla.
fun PantallaEquipoFavorito(
    teams: List<Equipo>,
    onTeamSelected: () -> Unit,
    onBack: () -> Unit
) {
    val repo = remember { RepositorioUsuario() }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColoresApp.MainBackgroundBrush)
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        // Cabecera simple con botón de volver y título de la selección.
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 20.dp)
        ) {

            IconButton(
                onClick = onBack
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Selecciona tu equipo",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Rejilla de equipos para mostrar muchas opciones sin hacer una lista demasiado larga.
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)

        ) {
            items(teams) { team ->

                // Cada columna representa una opción de equipo lista para guardar con un toque.
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable {
                            scope.launch {
                                // Guardamos nombre y escudo para reutilizarlos luego en el perfil.
                                repo.updateFavoriteTeam(team.name, team.crest)
                                onTeamSelected()
                            }
                        }
                ) {

                    // Caja blanca circular para normalizar cómo se ven todos los escudos.
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = team.crest,
                            contentDescription = team.name,
                            modifier = Modifier.size(50.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Nombre del equipo debajo del escudo.
                    Text(
                        text = team.name,
                        color = Color.White,
                        fontSize = 12.sp,
                        maxLines = 2
                    )
                }
            }
        }
    }
}
