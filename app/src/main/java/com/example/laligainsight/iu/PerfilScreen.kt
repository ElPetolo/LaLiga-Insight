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
import com.example.laligainsight.Auth.UserRepository
import com.example.laligainsight.R
import com.example.laligainsight.modelo.Team
import com.example.laligainsight.modelo.User
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileScreen(
    teams: List<Team>,
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
    val repo = remember { UserRepository() }

    var user by remember { mutableStateOf<User?>(null) }
    var refreshTrigger by remember { mutableStateOf(0) }

    LaunchedEffect(refreshTrigger) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser

        if (firebaseUser != null) {
            repo.createUserIfNotExists(firebaseUser.email ?: "")
            user = repo.getUser()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0D1F1A),
                        Color(0xFF07140F),
                        Color(0xFF020605)
                    )
                )
            )
            .statusBarsPadding()
    ) {
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
                ProfileAvatar(currentUser = currentUser)

                Spacer(modifier = Modifier.height(22.dp))

                Text(
                    text = currentUser.username,
                    color = Color.White,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${currentUser.friends.size} amigos",
                    color = Color(0x80FFFFFF),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

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
                        color = Color(0xFF1D9E75),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0x141D9E75)
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

                Button(
                    onClick = {
                        FirebaseAuth.getInstance().signOut()
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFB3261E)
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
                Spacer(modifier = Modifier.height(120.dp))

                Text(
                    text = "Cargando perfil...",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        AppBottomBar(
            selectedTab = "Profile",
            onHomeClick = onHomeClick,
            onRankingClick = onRankingClick,
            onCompareClick = onCompareClick,
            onProfileClick = onProfileClick
        )
    }
}

@Composable
fun ProfileAvatar(
    currentUser: User
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(110.dp)
            .clip(CircleShape)
            .background(Color(0xFF0F6E56))
    ) {
        if (currentUser.profileImageUrl.isNotEmpty()) {
            AsyncImage(
                model = currentUser.profileImageUrl,
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.isotipo),
                contentDescription = "Logo",
                modifier = Modifier.size(62.dp)
            )
        }
    }
}

@Composable
fun ProfileButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0x221D9E75)
        ),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            color = Color(0xFF1D9E75),
            fontWeight = FontWeight.Bold
        )
    }
}
