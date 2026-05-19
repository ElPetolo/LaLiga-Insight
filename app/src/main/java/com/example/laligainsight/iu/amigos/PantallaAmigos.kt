package com.example.laligainsight.iu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.laligainsight.autenticacion.RepositorioUsuario
import com.example.laligainsight.modelo.Usuario
import kotlinx.coroutines.launch

@Composable
// Pantalla para buscar usuarios, revisar solicitudes y gestionar la lista de amigos.
fun PantallaAmigos(
    onBack: () -> Unit,
    onUserClick: (String) -> Unit
) {
    val repo = remember { RepositorioUsuario() }
    val scope = rememberCoroutineScope()

    var searchText by remember { mutableStateOf("") }
    var results by remember { mutableStateOf<List<Usuario>>(emptyList()) }
    var friends by remember { mutableStateOf<List<Usuario>>(emptyList()) }
    var receivedRequests by remember { mutableStateOf<List<Usuario>>(emptyList()) }
    var sentRequests by remember { mutableStateOf<List<Usuario>>(emptyList()) }
    var message by remember { mutableStateOf<String?>(null) }
    var friendToRemove by remember { mutableStateOf<Usuario?>(null) }

    // Reutilizamos esta recarga para no duplicar llamadas después de aceptar, rechazar o borrar.
    fun refreshData() {
        scope.launch {
            friends = repo.getFriends()
            receivedRequests = repo.getReceivedRequests()
            sentRequests = repo.getSentRequests()
        }
    }

    LaunchedEffect(Unit) {
        // Primera carga de datos al entrar en la pantalla.
        friends = repo.getFriends()
        receivedRequests = repo.getReceivedRequests()
        sentRequests = repo.getSentRequests()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColoresApp.MainBackgroundBrush)
            .statusBarsPadding()
            .padding(24.dp)
    ) {
        // Flecha superior para volver al perfil.
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Volver",
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Amigos",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Busca usuarios y gestiona solicitudes",
            color = Color(0x80FFFFFF),
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Input para escribir el username que queremos buscar.
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text("Buscar por username") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(
                    onClick = {
                        scope.launch {
                            results = repo.searchUsersByUsername(searchText)
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = ColoresApp.AccentGreen
                    )
                }
            },
            colors = authTextFieldColors()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Botón explícito por si el usuario prefiere confirmar la búsqueda en vez de usar el icono.
        Button(
            onClick = {
                scope.launch {
                    results = repo.searchUsersByUsername(searchText)
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = ColoresApp.AccentGreen
            ),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text(
                text = "Buscar",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        message?.let {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = it,
                color = ColoresApp.AccentGreen,
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            if (receivedRequests.isNotEmpty()) {
                item {
                    // Sección para atender primero las solicitudes entrantes.
                    SectionTitle("Solicitudes recibidas")
                }

                items(receivedRequests) { user ->
                    RequestRow(
                        user = user,
                        onAccept = {
                            scope.launch {
                                repo.acceptFriendRequest(user.uid)
                                message = "Solicitud aceptada"
                                refreshData()
                            }
                        },
                        onReject = {
                            scope.launch {
                                repo.rejectFriendRequest(user.uid)
                                message = "Solicitud rechazada"
                                refreshData()
                            }
                        }
                    )
                }
            }

            if (results.isNotEmpty()) {
                item {
                    // Resultados de la búsqueda manual.
                    SectionTitle("Resultados")
                }

                items(results) { user ->
                    // Aquí decidimos qué acción mostrar según la relación actual con ese usuario.
                    val alreadyFriend = friends.any { it.uid == user.uid }
                    val alreadySent = sentRequests.any { it.uid == user.uid }
                    val alreadyReceived = receivedRequests.any { it.uid == user.uid }

                    UserRow(
                        user = user,
                        actionText = when {
                            alreadyFriend -> "Amigo"
                            alreadySent -> "Pendiente"
                            alreadyReceived -> "Responder"
                            else -> "Enviar"
                        },
                        actionEnabled = !alreadyFriend && !alreadySent && !alreadyReceived,
                        onActionClick = {
                            scope.launch {
                                repo.sendFriendRequest(user.uid)
                                results = emptyList()
                                searchText = ""
                                message = "Solicitud enviada"
                                refreshData()
                            }
                        },
                        onUserClick = { onUserClick(user.uid) }

                    )
                }
            }

            if (sentRequests.isNotEmpty()) {
                item {
                    // Bloque para ver a quién ya se le ha enviado petición.
                    SectionTitle("Solicitudes enviadas")
                }

                items(sentRequests) { user ->
                    UserRow(
                        user = user,
                        actionText = "Pendiente",
                        actionEnabled = false,
                        onActionClick = {},
                        onUserClick = { onUserClick(user.uid) }
                    )
                }
            }

            item {
                // Sección fija con la lista actual de amistades.
                SectionTitle("Mis amigos")
            }

            if (friends.isEmpty()) {
                item {
                    Text(
                        text = "Aún no tienes amigos añadidos",
                        color = Color(0x80FFFFFF),
                        fontSize = 14.sp
                    )
                }
            } else {
                items(friends) { friend ->
                    UserRow(
                        user = friend,
                        actionText = "Eliminar",
                        actionEnabled = true,
                        onActionClick = {
                            friendToRemove = friend
                        },
                        onUserClick = { onUserClick(friend.uid) }
                    )
                }
            }


            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
        if (friendToRemove != null) {
            // Diálogo de confirmación para evitar borrar amigos por error al pulsar.
            AlertDialog(
                onDismissRequest = { friendToRemove = null },
                title = {
                    Text("Eliminar amigo")
                },
                text = {
                    Text("¿Seguro que quieres eliminar a ${friendToRemove!!.username}?")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            scope.launch {
                                repo.removeFriend(friendToRemove!!.uid)
                                message = "Amigo eliminado"
                                refreshData()
                                friendToRemove = null
                            }
                        }
                    ) {
                        Text("Eliminar", color = ColoresApp.AccentGreen)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            friendToRemove = null
                        }
                    ) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
// Título simple para separar las distintas secciones de la lista.
fun SectionTitle(
    text: String
) {
    Text(
        text = text,
        color = Color.White,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
// Fila usada para mostrar una solicitud recibida con aceptar o rechazar.
fun RequestRow(
    user: Usuario,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    // Card de una solicitud recibida con accesos rápidos de aceptar y rechazar.
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = ColoresApp.CardSoft
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar de quien ha enviado la solicitud.
            UserAvatar(user = user)

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Nombre + texto auxiliar para explicar la acción pendiente.
                Text(
                    text = user.username,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Quiere ser tu amigo",
                    color = ColoresApp.TextSecondary,
                    fontSize = 13.sp
                )
            }

            IconButton(onClick = onAccept) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Aceptar",
                    tint = ColoresApp.AccentGreen
                )
            }

            IconButton(onClick = onReject) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Rechazar",
                    tint = Color(0xFFFF6B6B)
                )
            }
        }
    }
}

@Composable
// Fila genérica de usuario; cambia la acción según el contexto en el que se pinte.
fun UserRow(
    user: Usuario,
    actionText: String,
    actionEnabled: Boolean,
    onActionClick: () -> Unit,
    onUserClick: () -> Unit
) {
        // Esta card sirve tanto para amigos como para resultados y solicitudes enviadas.
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onUserClick() },
        colors = CardDefaults.cardColors(
            containerColor = ColoresApp.CardSoft
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar a la izquierda para reconocer rápido a cada usuario.
            UserAvatar(user = user)

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Nombre del usuario y equipo favorito como contexto adicional.
                Text(
                    text = user.username,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = user.favoriteTeam.ifEmpty { "Sin equipo favorito" },
                    color = Color(0x991D9E75),
                    fontSize = 13.sp
                )
            }

            if (actionText.isNotEmpty()) {
                if (actionEnabled) {
                    // Si hay una acción válida, la mostramos como icono para ocupar menos espacio.
                    IconButton(onClick = onActionClick) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = actionText,
                            tint = ColoresApp.AccentGreen
                        )
                    }
                } else {
                    // Si no se puede pulsar, dejamos el estado escrito para que se entienda el motivo.
                    Text(
                        text = actionText,
                        color = Color(0x80FFFFFF),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
// Avatar reutilizable para amigos, búsquedas y solicitudes.
fun UserAvatar(user: Usuario) {
    if (user.profileImageUrl.isNotEmpty()) {
        // Cuando existe foto, se pinta recortada en círculo.
        AsyncImage(
            model = user.profileImageUrl,
            contentDescription = user.username,
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop
        )
    } else {
        // Si no hay imagen, usamos la inicial del username como placeholder.
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(ColoresApp.AvatarBackground),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = user.username.take(1).uppercase(),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
