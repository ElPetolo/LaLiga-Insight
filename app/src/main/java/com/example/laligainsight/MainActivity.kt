package com.example.laligainsight

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import com.example.laligainsight.api.RetrofitCliente
import com.example.laligainsight.iu.AuthScreen
import com.example.laligainsight.iu.FavoriteTeamScreen
import com.example.laligainsight.iu.PlayerDetailScreen
import com.example.laligainsight.iu.ProfileScreen
import com.example.laligainsight.iu.TeamDetailScreen
import com.example.laligainsight.iu.TeamsScreen
import com.example.laligainsight.modelo.Player
import com.example.laligainsight.modelo.Team
import com.google.firebase.auth.FirebaseAuth
import com.example.laligainsight.iu.StandingsScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.laligainsight.iu.EditProfileScreen
import com.example.laligainsight.iu.PrivacySecurityScreen
import com.example.laligainsight.iu.FriendsScreen
import com.example.laligainsight.iu.UserProfileScreen
import com.example.laligainsight.iu.CompareScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.laligainsight.viewmodel.ScorersViewModel
import androidx.compose.runtime.collectAsState
import com.example.laligainsight.iu.NotificationsScreen


enum class MainTab{
    TEAMS, STANDINGS, COMPARE, PROFILE
}

class MainActivity : ComponentActivity() {

    private var selectedTeam by mutableStateOf<Team?>(null)
    private var teams by mutableStateOf<List<Team>>(emptyList())
    private var selectedPlayer by mutableStateOf<Player?>(null)

    private var selectedTab by mutableStateOf(MainTab.TEAMS)

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




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { !composeReady }

        lifecycleScope.launch {
            delay(2500L)
            showSplash = false
        }

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

            val scorersViewModel: ScorersViewModel = viewModel()
            val scorers by scorersViewModel.scorers.collectAsState()

            DisposableEffect(isLoggedIn) {
                val uid = FirebaseAuth.getInstance().currentUser?.uid

                if (isLoggedIn && uid != null) {
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

                composeReady = true // Compose listo -> splash del sistema desaparece
            }

            if (showSplash || isCheckingAuth) {
                SplashScreen()
            } else if (!isLoggedIn) {
                AuthScreen(
                    onLoginSuccess = {
                        isLoggedIn = true
                    }
                )
            } else {
                when {

                    selectedUserId != null -> {
                        UserProfileScreen(
                            userId = selectedUserId!!,
                            onBack = {
                                selectedUserId = null
                                showUserProfileScreen = false
                                showFriendsScreen = true
                            }
                        )
                    }

                    showFavoriteTeamScreen -> {
                        FavoriteTeamScreen(
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
                        EditProfileScreen(
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
                        PrivacySecurityScreen(
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
                        FriendsScreen(
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
                        NotificationsScreen(
                            onBack = {
                                showNotificationsScreen = false
                                selectedTab = MainTab.TEAMS
                            }
                        )
                    }

                    showProfile -> {
                        ProfileScreen(
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
                        PlayerDetailScreen(
                            player = selectedPlayer!!, // Suponemos que selectedPlayer no es nulo
                            onBackClick = { selectedPlayer = null }
                        )
                    }

                    selectedTeam != null -> {
                        TeamDetailScreen(
                            team = selectedTeam!!,
                            onBackClick = { selectedTeam = null },
                            onPlayerClick = { player -> selectedPlayer = player }
                        )
                    }

                    else -> {

                        // Mantenemos la navegación principal por pestañas.
                        // Esto respeta el flujo que ya existía en main.
                        when (selectedTab) {

                            // Pantalla principal de equipos
                            MainTab.TEAMS -> {

                                TeamsScreen(
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

                            // Pantalla nueva de clasificación/goleadores
                            MainTab.STANDINGS -> {
                                StandingsScreen(
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

                            // Lo dejamos provisional para no romper el tab
                            MainTab.COMPARE -> {
                                CompareScreen(
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

                            // Lo dejamos provisional para no tocar aún el perfil/login de Héctor
                            MainTab.PROFILE -> {
                                showProfile = true
                            }
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            try {
                val response = RetrofitCliente.api.getTeams()
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
fun SplashScreen() {
    val infiniteTransition = rememberInfiniteTransition(label = "loader")
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
                        Color(0xFF0D1F1A),
                        Color(0xFF0A1A14),
                        Color(0xFF060E0B)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-80).dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x551D9E75), Color(0x001D9E75))
                    ),
                    shape = CircleShape
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(155.dp)) {
                Box(
                    modifier = Modifier
                        .size(155.dp)
                        .clip(CircleShape)
                        .background(Color(0x1A1D9E75))
                )
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .clip(CircleShape)
                        .background(Color(0x261D9E75))
                )
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0F6E56))
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
                    color = Color(0xFF1D9E75),
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

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .width(120.dp)
                    .height(2.dp)
                    .clip(CircleShape),
                color = Color(0xFF1D9E75),
                trackColor = Color(0x1AFFFFFF)
            )

            Spacer(modifier = Modifier.height(56.dp))
        }

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