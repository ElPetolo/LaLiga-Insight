package com.example.laligainsight.iu

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.laligainsight.R
import com.example.laligainsight.autenticacion.RepositorioAutenticacion
import kotlinx.coroutines.launch

@Composable
fun PantallaRegistro(
    onRegisterSuccess: () -> Unit,
    onGoToLogin: () -> Unit
) {
    val authRepository = remember { RepositorioAutenticacion() }
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    FondoAutenticacion {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(86.dp)
                    .clip(CircleShape)
                    .background(ColoresApp.AvatarBackground)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.isotipo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(52.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Crear cuenta",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Únete a LaLigaInsight",
                color = Color(0x99FFFFFF),
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = ColoresApp.CardSoft
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                Column(
                    modifier = Modifier.padding(22.dp)
                ) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correo electrónico") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = authTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        colors = authTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirmar contraseña") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        colors = authTextFieldColors()
                    )

                    error?.let {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = it,
                            color = Color(0xFFFF6B6B),
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    Button(
                        onClick = {
                            scope.launch {
                                if (email.isBlank() && password.isBlank() && confirmPassword.isBlank()) {
                                    error = "Introduce tus datos para crear la cuenta"
                                    return@launch
                                }

                                if (email.isBlank()) {
                                    error = "Introduce tu correo electrónico"
                                    return@launch
                                }

                                if (password.isBlank()) {
                                    error = "Introduce una contraseña"
                                    return@launch
                                }

                                if (confirmPassword.isBlank()) {
                                    error = "Repite la contraseña"
                                    return@launch
                                }

                                if (password != confirmPassword) {
                                    error = "Las contraseñas no coinciden"
                                    return@launch
                                }

                                if (password.length < 6) {
                                    error = "La contraseña debe tener mínimo 6 caracteres"
                                    return@launch
                                }

                                try {
                                    loading = true
                                    error = null
                                    authRepository.register(email.trim(), password)
                                    onRegisterSuccess()
                                } catch (e: Exception) {
                                    error = getFriendlyAuthError(e)
                                } finally {
                                    loading = false
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ColoresApp.AccentGreen
                        )
                    ) {
                        Text(
                            text = if (loading) "Creando..." else "Crear cuenta",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(
                        onClick = onGoToLogin,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "¿Ya tienes cuenta? Iniciar sesión",
                            color = ColoresApp.AccentGreen
                        )
                    }
                }
            }
        }
    }
}