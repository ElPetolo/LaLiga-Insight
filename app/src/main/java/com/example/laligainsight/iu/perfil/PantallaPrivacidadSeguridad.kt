package com.example.laligainsight.iu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth

@Composable
// Reúne acciones relacionadas con la cuenta: correo, cambio de contraseña y cierre de sesión.
fun PantallaPrivacidadSeguridad(
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val email = auth.currentUser?.email ?: "Email no disponible"
    var message by remember { mutableStateOf<String?>(null) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColoresApp.MainBackgroundBrush)
            .statusBarsPadding()
            .padding(24.dp)
    ) {
        // Botón para volver a la pantalla anterior.
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Volver",
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Privacidad y seguridad",
            color = Color.White,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Card con la información de correo y la acción de cambio de contraseña.
        Card(
            colors = CardDefaults.cardColors(
                containerColor = ColoresApp.CardSoft
            ),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                // Fila que muestra el correo vinculado a la cuenta actual.
                Row {
                    Icon(
                        imageVector = Icons.Default.Mail,
                        contentDescription = null,
                        tint = ColoresApp.AccentGreen
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Correo electrónico",
                            color = Color(0x99FFFFFF),
                            fontSize = 12.sp
                        )

                        Text(
                            text = email,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Botón para pedir el correo de restablecimiento de contraseña.
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),

                    colors = ButtonDefaults.buttonColors(
                        containerColor = ColoresApp.AccentGreen
                    ),

                    onClick = {
                        val email = FirebaseAuth.getInstance().currentUser?.email

                        if (email.isNullOrEmpty()) {
                            message = "No se ha encontrado el correo de la cuenta"
                            return@Button
                        }

                        // Firebase envía el correo y nosotros solo reflejamos el resultado en pantalla.
                        FirebaseAuth.getInstance()
                            .sendPasswordResetEmail(email)
                            .addOnSuccessListener {
                                message = "Se ha enviado un correo para cambiar la contraseña"
                            }
                            .addOnFailureListener {
                                message = "No se pudo enviar el correo"
                            }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color.White
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Cambiar contraseña",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                message?.let {
                    Spacer(modifier = Modifier.height(12.dp))
                    // Feedback del resultado del envío del correo.
                    Text(
                        text = it,
                        color = ColoresApp.AccentGreen,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Botón final aislado para remarcar la acción de cerrar sesión.
        Button(
            onClick = {
                auth.signOut()
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
    }
}
