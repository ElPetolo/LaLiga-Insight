package com.example.laligainsight.iu

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.laligainsight.autenticacion.RepositorioUsuario
import com.example.laligainsight.R
import com.example.laligainsight.modelo.Equipo
import com.example.laligainsight.modelo.Usuario
import com.google.firebase.auth.FirebaseAuth

@Composable
// Pantalla del perfil propio: carga los datos del usuario y da acceso a sus ajustes principales.
fun PantallaPerfil(
    teams: List<Equipo>,
    onHomeClick: () -> Unit,
    onSelectTeamClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    onPrivacySecurityClick: () -> Unit,
    onFriendsClick: () -> Unit,
    onLogout: () -> Unit,
    onRankingClick: () -> Unit,
    onCompareClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val repo = remember { RepositorioUsuario() }

    var user by remember { mutableStateOf<Usuario?>(null) }
    var refreshTrigger by remember { mutableStateOf(0) }

    LaunchedEffect(refreshTrigger) {
        // Nos aseguramos de que exista el documento del usuario antes de leer su perfil.
        val firebaseUser = FirebaseAuth.getInstance().currentUser

        if (firebaseUser != null) {
            repo.createUserIfNotExists(firebaseUser.email ?: "")
            user = repo.getUser()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColoresApp.MainBackgroundBrush)
            .statusBarsPadding()
    ) {
        // Esta parte ocupa casi toda la pantalla y se puede desplazar si el contenido crece.
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            user?.let { currentUser ->
                // Avatar principal del usuario.
                ProfileAvatar(currentUser = currentUser)

                Spacer(modifier = Modifier.height(22.dp))

                // Nombre visible del perfil.
                Text(
                    text = currentUser.username,
                    color = Color.White,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold
                )

                // Número de amigos para resumir la actividad social del perfil.
                Text(
                    text = "${currentUser.friends.size} amigos",
                    color = Color(0x80FFFFFF),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Etiqueta de apoyo para separar los datos personales del equipo favorito.
                Text(
                    text = "Equipo favorito",
                    color = Color(0x80FFFFFF),
                    fontSize = 14.sp,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (currentUser.favoriteTeamCrest.isNotEmpty()) {
                        // Contenedor del escudo para que no se mezcle visualmente con el fondo.
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = currentUser.favoriteTeamCrest,
                                contentDescription = currentUser.favoriteTeam,
                                modifier = Modifier.size(26.dp),
                                contentScale = ContentScale.Fit
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Text(
                        text = currentUser.favoriteTeam.ifEmpty { "Sin equipo favorito" },
                        color = ColoresApp.AccentGreen,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Card central con las acciones principales del perfil.
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = ColoresApp.ButtonSecondary
                    ),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(22.dp)
                    ) {
                        ProfileButton(
                            text = "Editar perfil",
                            onClick = onEditProfileClick
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        ProfileButton(
                            text = "Privacidad y seguridad",
                            onClick = onPrivacySecurityClick
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        ProfileButton(
                            text = "Amigos",
                            onClick = onFriendsClick
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        ProfileButton(
                            text = if (currentUser.favoriteTeam.isEmpty()) {
                                "Seleccionar equipo"
                            } else {
                                "Cambiar equipo"
                            },
                            onClick = onSelectTeamClick
                        )

                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Botón aislado para remarcar que cerrar sesión es una acción distinta al resto.
                Button(
                    onClick = {
                        FirebaseAuth.getInstance().signOut()
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ColoresApp.Danger
                    ),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                ) {
                    Text(
                        text = "Cerrar sesión",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            } ?: run {
                // Mientras aún no ha llegado Firestore mostramos un estado de carga sencillo.
                Spacer(modifier = Modifier.height(120.dp))

                Text(
                    text = "Cargando perfil...",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        BarraInferiorApp(
            selectedTab = "Profile",
            onHomeClick = onHomeClick,
            onRankingClick = onRankingClick,
            onCompareClick = onCompareClick,
            onProfileClick = onProfileClick
        )
    }
}

@Composable
// Muestra la imagen del perfil si existe; si no, usa el isotipo de la app como fallback.
fun ProfileAvatar(
    currentUser: Usuario
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(110.dp)
            .clip(CircleShape)
            .background(ColoresApp.AvatarBackground)
    ) {
        if (currentUser.profileImageUrl.isNotEmpty()) {
            // Si el usuario ya subió foto, esta imagen ocupa todo el círculo.
            AsyncImage(
                model = currentUser.profileImageUrl,
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            // Si no hay foto usamos el isotipo para que el perfil no quede vacío.
            Image(
                painter = painterResource(id = R.drawable.isotipo),
                contentDescription = "Logo",
                modifier = Modifier.size(62.dp)
            )
        }
    }
}

@Composable
// Botón reutilizable para las acciones rápidas del perfil.
fun ProfileButton(
    text: String,
    onClick: () -> Unit
) {
    // Mismo estilo para todas las entradas del menú del perfil.
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = ColoresApp.ButtonPrimary
        ),
        shape = RoundedCornerShape(18.dp),

        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
