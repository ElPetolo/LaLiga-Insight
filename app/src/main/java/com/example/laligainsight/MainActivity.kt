package com.example.laligainsight

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import coil.compose.AsyncImage
import com.example.laligainsight.api.ClienteRetrofit
import com.example.laligainsight.iu.PantallaAutenticacion
import com.example.laligainsight.iu.PantallaEquipoFavorito
import com.example.laligainsight.iu.PantallaDetalleJugador
import com.example.laligainsight.iu.PantallaPerfil
import com.example.laligainsight.iu.PantallaDetalleEquipo
import com.example.laligainsight.iu.PantallaEquipos
import com.example.laligainsight.modelo.Player
import com.example.laligainsight.modelo.Equipo
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.laligainsight.iu.PantallaEditarPerfil
import com.example.laligainsight.iu.PantallaPrivacidadSeguridad
import com.example.laligainsight.iu.PantallaAmigos
import com.example.laligainsight.iu.PantallaPerfilUsuario
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.laligainsight.viewmodel.GoleadoresViewModel
import androidx.compose.runtime.collectAsState
import com.example.laligainsight.iu.ColoresApp
import com.example.laligainsight.iu.PantallaComparador
import com.example.laligainsight.iu.PantallaNotificaciones
import com.example.laligainsight.iu.PantallaClasificacion


enum class MainTab{
    TEAMS, STANDINGS, COMPARE, PROFILE
}

// Activity principal: controla splash, sesión y navegación entre pantallas de Compose.
class MainActivity : ComponentActivity() {

    // Estado base de navegación y datos compartidos entre pantallas.
    private var selectedTeam by mutableStateOf<Equipo?>(null)
    private var teams by mutableStateOf<List<Equipo>>(emptyList())
    private var selectedPlayer by mutableStateOf<Player?>(null)

    // Pestaña principal activa de la navegación inferior.
    private var selectedTab by mutableStateOf(MainTab.TEAMS)

    // Flags que controlan splash, autenticación y pantallas secundarias.
    private var showSplash by mutableStateOf(true)
    private var composeReady by mutableStateOf(false)
    private var isCheckingAuth by mutableStateOf(true)
    private var isLoggedIn by mutableStateOf(false)
    private var showProfile by mutableStateOf(false)
    private var showFavoriteTeamScreen by mutableStateOf(false)
    private var showEditProfileScreen by mutableStateOf(false)
    private var showPrivacySecurityScreen by mutableStateOf(false)
    private var showFriendsScreen by mutableStateOf(false)
    private var selectedUserId by mutableStateOf<String?>(null)

    private var showUserProfileScreen by mutableStateOf(false)
    private var showCompareScreen by mutableStateOf(false)
    private var showNotificationsScreen by mutableStateOf(false)
    private var notificationCount by mutableStateOf(0)




    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val splashScreen = installSplashScreen()
        // La splash nativa sigue visible hasta que Compose ya está preparado para dibujar.
        splashScreen.setKeepOnScreenCondition { !composeReady }

        lifecycleScope.launch {
            // Dejamos una pequeña pausa para que la entrada a la app no sea brusca.
            delay(2500L)
            showSplash = false
        }

        // Forzamos una recarga del usuario para comprobar si la sesión sigue siendo válida.
        FirebaseAuth.getInstance().currentUser?.reload()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful && FirebaseAuth.getInstance().currentUser != null) {
                    isLoggedIn = true
                } else {
                    FirebaseAuth.getInstance().signOut()
                    isLoggedIn = false
                }

                isCheckingAuth = false
            } ?: run {
            isLoggedIn = false
            isCheckingAuth = false
        }

        setContent {

            val scorersViewModel: GoleadoresViewModel = viewModel()
            val scorers by scorersViewModel.scorers.collectAsState()

            DisposableEffect(isLoggedIn) {
                val uid = FirebaseAuth.getInstance().currentUser?.uid

                if (isLoggedIn && uid != null) {
                    // Escuchamos cambios en solicitudes recibidas para pintar el contador en tiempo real.
                    val listener = com.google.firebase.firestore.FirebaseFirestore
                        .getInstance()
                        .collection("users")
                        .document(uid)
                        .addSnapshotListener { snapshot, _ ->
                            val requests = snapshot?.get("receivedRequests") as? List<*>
                            notificationCount = requests?.size ?: 0
                        }

                    onDispose {
                        listener.remove()
                    }
                } else {
                    onDispose { }
                }
            }

            LaunchedEffect(Unit) {
                // En cuanto Compose entra por primera vez, ya podemos soltar la splash del sistema.
                composeReady = true // Compose listo -> splash del sistema desaparece
            }

            if (showSplash || isCheckingAuth) {
                // Mientras no sabemos si hay sesión válida, dejamos la pantalla de carga.
                SplashScreen()
            } else if (!isLoggedIn) {
                // Si no hay sesión activa se muestra el flujo de autenticación.
                PantallaAutenticacion(
                    onLoginSuccess = {
                        isLoggedIn = true
                    }
                )
            } else {
                when {

                    selectedUserId != null -> {
                        // Si se ha pulsado otro usuario, abrimos su perfil desde la pantalla de amigos.
                        PantallaPerfilUsuario(
                            userId = selectedUserId!!,
                            onBack = {
                                selectedUserId = null
                                showUserProfileScreen = false
                                showFriendsScreen = true
                            }
                        )
                    }

                    showFavoriteTeamScreen -> {
                        // Pantalla para seleccionar o cambiar el equipo favorito.
                        PantallaEquipoFavorito(
                            teams = teams,
                            onTeamSelected = {
                                showFavoriteTeamScreen = false
                                showProfile = true
                            },
                            onBack = {
                                showFavoriteTeamScreen = false
                                showProfile = true
                            }
                        )
                    }

                    showEditProfileScreen -> {
                        // Pantalla de edición del perfil del usuario actual.
                        PantallaEditarPerfil(
                            currentUsername = "",
                            currentProfileImageUrl = "",
                            onBack = {
                                showEditProfileScreen = false
                                showProfile = true
                            },
                            onSaved = {
                                showEditProfileScreen = false
                                showProfile = false

                                showProfile = true
                            }
                        )
                    }

                    showPrivacySecurityScreen -> {
                        // Sección separada con acciones sensibles de la cuenta.
                        PantallaPrivacidadSeguridad(
                            onBack = {
                                showPrivacySecurityScreen = false
                                showProfile = true
                            },
                            onLogout = {
                                FirebaseAuth.getInstance().signOut()
                                isLoggedIn = false
                                showPrivacySecurityScreen = false
                                showProfile = false
                                showFavoriteTeamScreen = false
                                showEditProfileScreen = false
                                selectedTeam = null
                                selectedPlayer = null
                            }
                        )
                    }

                    showFriendsScreen -> {
                        // Pantalla social con búsquedas y gestión de amistades.
                        PantallaAmigos(
                            onBack = {
                                showFriendsScreen = false
                                showProfile = true
                            },
                            onUserClick = { userId ->
                                selectedUserId = userId
                                showFriendsScreen = false
                            }
                        )
                    }

                    showNotificationsScreen -> {
                        // Bandeja de solicitudes pendientes.
                        PantallaNotificaciones(
                            onBack = {
                                showNotificationsScreen = false
                                selectedTab = MainTab.TEAMS
                            }
                        )
                    }

                    showProfile -> {
                        // Perfil propio del usuario logueado.
                        PantallaPerfil(
                            teams = teams,
                            onHomeClick = {
                                showProfile = false
                                showFavoriteTeamScreen = false
                                showEditProfileScreen = false
                                showPrivacySecurityScreen = false
                                showFriendsScreen = false
                                selectedUserId = null
                                selectedTeam = null
                                selectedPlayer = null
                                selectedTab = MainTab.TEAMS
                            },
                            onRankingClick = {
                                showProfile = false
                                showFavoriteTeamScreen = false
                                showEditProfileScreen = false
                                showPrivacySecurityScreen = false
                                showFriendsScreen = false
                                selectedUserId = null
                                selectedTeam = null
                                selectedPlayer = null
                                selectedTab = MainTab.STANDINGS
                            },
                            onCompareClick = {
                                showProfile = false
                                showFavoriteTeamScreen = false
                                showEditProfileScreen = false
                                showPrivacySecurityScreen = false
                                showFriendsScreen = false
                                selectedUserId = null
                                selectedTeam = null
                                selectedPlayer = null
                                selectedTab = MainTab.COMPARE
                            },
                            onSelectTeamClick = {
                                showFavoriteTeamScreen = true
                                showProfile = false
                            },
                            onEditProfileClick = {
                                showEditProfileScreen = true
                                showProfile = false
                            },
                            onPrivacySecurityClick = {
                                showPrivacySecurityScreen = true
                                showProfile = false
                            },
                            onFriendsClick = {
                                showFriendsScreen = true
                                showProfile = false
                            },
                            onProfileClick = {
                                showProfile = true
                            },
                            onLogout = {
                                FirebaseAuth.getInstance().signOut()
                                isLoggedIn = false
                                showProfile = false
                                showFavoriteTeamScreen = false
                                showEditProfileScreen = false
                                selectedTeam = null
                                selectedPlayer = null
                            }
                        )
                    }

                    selectedPlayer != null -> {
                        // Si se selecciona un jugador desde el detalle del equipo, se abre su ficha.
                        PantallaDetalleJugador(
                            player = selectedPlayer!!, // Suponemos que selectedPlayer no es nulo
                            onBackClick = { selectedPlayer = null }
                        )
                    }

                    selectedTeam != null -> {
                        // Si se toca un equipo en el listado principal, entramos a su detalle.
                        PantallaDetalleEquipo(
                            team = selectedTeam!!,
                            onBackClick = { selectedTeam = null },
                            onPlayerClick = { player -> selectedPlayer = player }
                        )
                    }

                    else -> {

                        // Si no hay ninguna pantalla secundaria abierta, entramos en la navegación por pestañas.
                        when (selectedTab) {

                            MainTab.TEAMS -> {
                                // Home principal con el catálogo de equipos.
                                PantallaEquipos(
                                    teams = teams,
                                    onTeamClick = { team ->
                                        selectedTeam = team
                                    },
                                    onHomeClick = {
                                        selectedTab = MainTab.TEAMS
                                    },
                                    onRankingClick = {
                                        selectedTab = MainTab.STANDINGS
                                    },

                                    onCompareClick = {
                                        selectedTab = MainTab.COMPARE
                                    },

                                    onProfileClick = {
                                        showProfile = true
                                    },

                                    notificationCount = notificationCount,

                                    onNotificationsClick = {
                                        showNotificationsScreen = true
                                    }
                                )
                            }

                            MainTab.STANDINGS -> {
                                // Pantalla de clasificación y goleadores.
                                PantallaClasificacion(
                                    onHomeClick = {
                                        selectedTab = MainTab.TEAMS
                                    },
                                    onRankingClick = {
                                        selectedTab = MainTab.STANDINGS
                                    },
                                    onCompareClick = {
                                        selectedTab = MainTab.COMPARE
                                    },
                                    onProfileClick = {
                                        selectedTab = MainTab.PROFILE
                                        showProfile = true
                                    }
                                )
                            }

                            MainTab.COMPARE -> {
                                // Comparador de jugadores usando los goleadores cargados por ViewModel.
                                PantallaComparador(
                                    scorers = scorers,
                                    onHomeClick = {
                                        selectedTab = MainTab.TEAMS
                                    },
                                    onRankingClick = {
                                        selectedTab = MainTab.STANDINGS
                                    },
                                    onCompareClick = {
                                        selectedTab = MainTab.COMPARE
                                    },
                                    onProfileClick = {
                                        selectedTab = MainTab.PROFILE
                                        showProfile = true
                                    }
                                )
                            }

                            MainTab.PROFILE -> {
                                // Esta pestaña no pinta contenido directo; redirige al flujo del perfil.
                                showProfile = true
                            }
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            try {
                // Los equipos se cargan una vez al arrancar y se comparten con las pantallas que lo necesiten.
                val response = ClienteRetrofit.api.getTeams()
                teams = if (response.isSuccessful) {
                    response.body()?.teams ?: emptyList()
                } else {
                  emptyList()
                }
            } catch (e: Exception) {
                teams = emptyList()
            }
        }
    }
}





@Composable
// Splash personalizada mientras se resuelve la sesión y Compose termina de arrancar.
fun SplashScreen() {
    val infiniteTransition = rememberInfiniteTransition(label = "loader")
    // La barra inferior se anima en bucle mientras termina la inicialización de la app.
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        ColoresApp.BackgroundTop,
                        ColoresApp.BackgroundMiddle,
                        ColoresApp.BackgroundBottom
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Mancha de color superior para dar profundidad al fondo.
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-80).dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            ColoresApp.AccentBlue.copy(alpha = 0.28f),
                            ColoresApp.AccentBlue.copy(alpha = 0.12f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Columna central con logo, nombre de la app y barra de progreso.
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Anillos concéntricos para destacar visualmente el isotipo.
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(155.dp)) {
                Box(
                    modifier = Modifier
                        .size(155.dp)
                        .clip(CircleShape)
                        .background(ColoresApp.AccentBlue.copy(alpha = 0.18f))
                )
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .clip(CircleShape)
                        .background(ColoresApp.AccentBlue.copy(alpha = 0.28f))
                )
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(ColoresApp.AvatarBackground)
                ) {
                    AsyncImage(
                        model = R.drawable.isotipo,
                        contentDescription = "Logo",
                        modifier = Modifier.size(70.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Row {
                Text(
                    text = "LaLiga",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.02).em
                )
                Text(
                    text = "Insight",
                    color = ColoresApp.AccentBlue,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.02).em
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "ESTADÍSTICAS EN TIEMPO REAL",
                color = Color(0x61FFFFFF),
                fontSize = 10.sp,
                letterSpacing = 0.2.em
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Indicador mínimo de carga para reforzar que la app sigue trabajando.
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .width(120.dp)
                    .height(2.dp)
                    .clip(CircleShape),
                color = ColoresApp.AccentGreen,
                trackColor = Color(0x1AFFFFFF)
            )

            Spacer(modifier = Modifier.height(56.dp))
        }

        // Firma del proyecto colocada abajo para cerrar la composición visual.
        Text(
            text = "HÉCTOR CUÉLLAR Y CÉSAR ALONSO",
            color = Color(0x33FFFFFF),
            fontSize = 10.sp,
            letterSpacing = 0.15.em,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        )
    }
}
