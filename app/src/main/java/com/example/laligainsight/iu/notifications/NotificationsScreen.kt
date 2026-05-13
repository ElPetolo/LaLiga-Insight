package com.example.laligainsight.iu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.laligainsight.Auth.UserRepository
import com.example.laligainsight.modelo.User
import kotlinx.coroutines.launch

@Composable
fun NotificationsScreen(
    onBack: () -> Unit
) {
    val repo = remember { UserRepository() }
    val scope = rememberCoroutineScope()

    var requests by remember { mutableStateOf<List<User>>(emptyList()) }
    var message by remember { mutableStateOf<String?>(null) }

    fun refresh() {
        scope.launch {
            requests = repo.getReceivedRequests()
        }
    }

    LaunchedEffect(Unit) {
        refresh()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.MainBackgroundBrush)
            .statusBarsPadding()
            .padding(24.dp)
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Volver",
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Notificaciones",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Solicitudes y actividad reciente",
            color = Color(0x99FFFFFF),
            fontSize = 14.sp
        )

        message?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = it,
                color = AppColors.AccentGreen,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (requests.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = AppColors.CardSoft
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text(
                    text = "No tienes notificaciones nuevas",
                    color = Color(0x99FFFFFF),
                    fontSize = 15.sp,
                    modifier = Modifier.padding(20.dp)
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(requests) { user ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = AppColors.CardSoft
                        ),
                        shape = RoundedCornerShape(22.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            UserAvatar(user = user)

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = user.username,
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = "quiere ser tu amigo",
                                    color = Color(0x99FFFFFF),
                                    fontSize = 13.sp
                                )
                            }

                            IconButton(
                                onClick = {
                                    scope.launch {
                                        repo.acceptFriendRequest(user.uid)
                                        message = "Solicitud aceptada"
                                        refresh()
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Aceptar",
                                    tint = AppColors.AccentGreen
                                )
                            }

                            IconButton(
                                onClick = {
                                    scope.launch {
                                        repo.rejectFriendRequest(user.uid)
                                        message = "Solicitud rechazada"
                                        refresh()
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Rechazar",
                                    tint = Color(0xFFFF6B6B)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}