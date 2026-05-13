package com.example.laligainsight.iu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.laligainsight.Auth.UserRepository
import com.example.laligainsight.modelo.User
import kotlinx.coroutines.launch

@Composable
fun UserProfileScreen(
    userId: String,
    onBack: () -> Unit
) {
    val repo = remember { UserRepository() }
    val scope = rememberCoroutineScope()

    var user by remember { mutableStateOf<User?>(null) }
    var currentUser by remember { mutableStateOf<User?>(null) }

    var refreshTrigger by remember { mutableStateOf(0) }

    LaunchedEffect(userId, refreshTrigger) {
        user = repo.getUserById(userId)
        currentUser = repo.getUser()
    }

    Column(

        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.MainBackgroundBrush)
            .statusBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally

    )
    {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.White
                )
            }
        }

        user?.let { u ->

            // FOTO
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(AppColors.AvatarBackground)
            ) {
                if (u.profileImageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = u.profileImageUrl,
                        contentDescription = u.username,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = u.username.take(1).uppercase(),
                        color = Color.White,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // USERNAME
            Text(
                text = u.username,
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "${u.friends.size} amigos",
                color = Color(0x80FFFFFF),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Spacer(modifier = Modifier.height(8.dp))

            // EQUIPO
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (u.favoriteTeamCrest.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = u.favoriteTeamCrest,
                            contentDescription = u.favoriteTeam,
                            modifier = Modifier.size(20.dp),
                            contentScale = ContentScale.Fit
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))
                }

                Text(
                    text = u.favoriteTeam.ifEmpty { "Sin equipo favorito" },
                    color = AppColors.AccentGreen,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            currentUser?.let { me ->

                val isFriend = me.friends.contains(u.uid)
                val sent = me.sentRequests.contains(u.uid)
                val received = me.receivedRequests.contains(u.uid)

                when {
                    isFriend -> {
                        Button(
                            onClick = {
                                scope.launch {
                                    repo.removeFriend(u.uid)
                                    refreshTrigger++
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AppColors.Danger
                            )
                        ) {
                            Text("Eliminar amigo")
                        }
                    }

                    sent -> {
                        Text("Solicitud enviada", color = Color.Gray)
                    }

                    received -> {
                        Row {
                            Button(onClick = {
                                scope.launch {
                                    repo.acceptFriendRequest(u.uid)
                                    refreshTrigger++
                                }
                            }) {
                                Text("Aceptar")
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Button(onClick = {
                                scope.launch {
                                    repo.rejectFriendRequest(u.uid)
                                    refreshTrigger++
                                }
                            }) {
                                Text("Rechazar")
                            }
                        }
                    }

                    else -> {
                        Button(onClick = {
                            scope.launch {
                                repo.sendFriendRequest(u.uid)
                                refreshTrigger++
                            }
                        }) {
                            Text("Añadir amigo")
                        }
                    }
                }
            }
        }
    }
}