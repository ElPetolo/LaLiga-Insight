package com.example.laligainsight.iu

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.laligainsight.modelo.Player
import com.example.laligainsight.modelo.PlayerExtraInfo
import com.example.laligainsight.modelo.RatingSummary
import com.example.laligainsight.repository.RatingRepository
import com.example.laligainsight.viewmodel.PlayersViewModel
import kotlinx.coroutines.launch


// ======================================================================================
// PANTALLA DETALLE DEL JUGADOR
// ======================================================================================

@Composable
fun PlayerDetailScreen(
    player: Player,
    onBackClick: () -> Unit
) {

    // ==================================================================================
    // INFORMACIÓN EXTRA DEL JUGADOR
    // ==================================================================================

    // Creamos un objeto con información preparada para mostrar en pantalla
    // Aquí traducimos la posición y añadimos placeholders
    val extraInfo = PlayerExtraInfo(
        fullName = player.name,
        position = traducirPosicion(player.position),
        birthday = player.dateOfBirth ?: "No disponible",
        nationality = player.nationality ?: "No disponible",
        currentTeam = "No disponible",
        contract = "No disponible",
        marketValue = "No disponible",
        preferredFoot = "No disponible",
        matchesInDatabase = "No disponible",
        relatedItems = emptyList()
    )
    // ==================================================================================
    // SISTEMA DE VALORACIONES
    // ==================================================================================


    // Repositorio que se conecta con Firebase para guardar ratings
    val ratingRepository = remember { RatingRepository() }

    // Estado donde guardamos la media de valoración y número de votos
    var ratingSummary by remember { mutableStateOf(RatingSummary()) }

    // Scope necesario para lanzar corrutinas
    val scope = rememberCoroutineScope()

    // ==================================================================================
    // VIEWMODEL DE JUGADORES
    // ==================================================================================

    // ViewModel donde tenemos la lista de jugadores de Firebase
    val playersViewModel: PlayersViewModel = viewModel()

    // Observamos los jugadores de Firebase en tiempo real
    val firebasePlayers by playersViewModel.players.collectAsState()

    // ==================================================================================
    // BUSCAMOS LA IMAGEN DEL JUGADOR EN FIREBASE
    // ==================================================================================

    // Buscamos dentro de Firebase el jugador actual
    // para sacar su imagen
    val firebasePlayer = firebasePlayers.firstOrNull {
        it.playerName == player.name
    }
    // ==================================================================================
    // CARGA INICIAL DE LA VALORACIÓN
    // ==================================================================================

    // Cuando se carga la pantalla obtenemos la valoración actual
    LaunchedEffect(player.id) {
        ratingSummary = ratingRepository.getRatingSummary(
            entityType = "player",
            entityId = player.id.toString()
        )
    }
    // ==================================================================================
    // CONTENEDOR PRINCIPAL DE TODA LA PANTALLA
    // ==================================================================================
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // ==============================================================================
        // FOTO DE FONDO DEL JUGADOR
        // ==============================================================================

        // Mostramos la imagen ocupando prácticamente toda la pantalla
        AsyncImage(
            model = firebasePlayer?.imageUrl,
            contentDescription = player.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(690.dp)
                .align(Alignment.TopCenter),

            // Crop para que la imagen llene el espacio
            contentScale = ContentScale.Crop,

            // Bajamos un poco la opacidad
            alpha = 0.70f
        )
        // ==============================================================================
        // OVERLAY OSCURO PARA DAR EFECTO CINEMÁTICO
        // ==============================================================================

        // Degradado vertical negro para mejorar la legibilidad del texto
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x33000000),
                            Color(0x88000000),
                            Color(0xEE020617)
                        )
                    )
                )
        )
        // ==============================================================================
        // GLOW AZUL LATERAL
        // ==============================================================================

        // Glow azul para mantener la estética premium azulada de la app
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xAA0A1B3D),
                            Color.Transparent
                        )
                    )
                )
        )
        // ==============================================================================
        // CONTENIDO PRINCIPAL DE LA PANTALLA
        // ==============================================================================

        Column(
            modifier = Modifier
                .fillMaxSize()
                // Evita que el contenido choque con la barra superior del móvil
                .statusBarsPadding()
                .padding(horizontal = 22.dp)
        ) {
            // ==========================================================================
            // BOTÓN DE VOLVER ATRÁS
            // ==========================================================================

            IconButton(
                onClick = onBackClick
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.height(30.dp))

            // ==========================================================================
            // NOMBRE DEL JUGADOR
            // ==========================================================================

            Text(
                text = extraInfo.fullName,
                color = Color.White,
                fontSize = 42.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 42.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            // ==========================================================================
            // POSICIÓN DEL JUGADOR
            // ==========================================================================

            Text(
                text = extraInfo.position,
                color = Color(0xFF57F287),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            // Spacer con peso para empujar las cards hacia abajo
            Spacer(modifier = Modifier.weight(1f))

            // ==========================================================================
            // CARD DE FECHA DE NACIMIENTO
            // ==========================================================================

            PlayerFloatingInfoCard(
                title = "Nacimiento",
                value = extraInfo.birthday,
                accent = Color(0xFFFFB020)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // ==========================================================================
            // CARD DE NACIONALIDAD
            // ==========================================================================

            PlayerFloatingInfoCard(
                title = "Nacionalidad",
                value = extraInfo.nationality,
                accent = Color(0xFFFF4D8D)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // ==========================================================================
            // CARD DE VALORACIÓN
            // ==========================================================================

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                shape = RoundedCornerShape(30.dp),

                // Borde azul transparente
                border = BorderStroke(
                    1.dp,

                    Color(0x443B82F6)
                ),

                // Fondo azul translúcido
                colors = CardDefaults.cardColors(
                    containerColor = Color(0x331E3A8A)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 22.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    // Texto superior de la card
                    Text(
                        text = "Valora este jugador",
                        color = Color(0xFF57F287),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    // Sistema de estrellas
                    RatingStars(
                        summary = ratingSummary,
                        onRatingSelected = { rating ->

                            // Lanzamos corrutina para guardar voto en Firebase
                            scope.launch {

                                // Guardamos el rating
                                ratingRepository.rateEntity(
                                    entityType = "player",
                                    entityId = player.id.toString(),
                                    rating = rating
                                )

                                // Volvemos a pedir los datos actualizados
                                ratingSummary = ratingRepository.getRatingSummary(
                                    entityType = "player",
                                    entityId = player.id.toString()
                                )
                            }
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}


// ======================================================================================
// CARD FLOTANTE REUTILIZABLE
// ======================================================================================

@Composable
fun PlayerFloatingInfoCard(
    title: String,
    value: String,
    accent: Color
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp),
        // Bordes redondeados
        shape = RoundedCornerShape(30.dp),
        // Borde azul transparente
        border = BorderStroke(
            width = 1.dp,
            color = Color(0x443B82F6)
        ),

        // Fondo translúcido
        colors = CardDefaults.cardColors(
            containerColor = Color(0x331E3A8A)
        ),

        // Quitamos sombra
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp),
            verticalArrangement = Arrangement.Center
        ) {

            // Título superior
            Text(
                text = title,
                color = accent,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Valor principal
            Text(
                text = value,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

// ======================================================================================
// COMPONENTE REUTILIZABLE DE TEXTO SIMPLE
// ======================================================================================

@Composable
fun PlayerInfoLine(
    title: String,
    value: String
) {
    Column(
        modifier = Modifier.padding(bottom = 14.dp)
    ) {
        // Título superior
        Text(
            text = title.uppercase(),
            color = Color(0x99FFFFFF),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))

        // Valor principal
        Text(
            text = value,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}