package com.example.laligainsight.iu

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.laligainsight.autenticacion.RepositorioAutenticacion
import com.example.laligainsight.autenticacion.ClienteAutenticacionGoogle
import com.example.laligainsight.R
import kotlinx.coroutines.launch

@Composable
// Pantalla de acceso principal: email/contraseña, recuperación y acceso con Google.
fun PantallaLogin(
    onLoginSuccess: () -> Unit,
    onGoToRegister: () -> Unit
) {
    val authRepository = remember { RepositorioAutenticacion() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val googleAuthClient = remember { ClienteAutenticacionGoogle(context) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    FondoAutenticacion {
        // Columna principal que centra todo el contenido del login.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Círculo superior con el isotipo de la aplicación.
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(ColoresApp.AvatarBackground)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.isotipo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(58.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Nombre de la app dividido en dos colores para reforzar la identidad visual.
            Row {
                Text("LaLiga", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)
                Text("Insight", color = ColoresApp.AccentGreen, fontSize = 30.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "INICIA SESIÓN PARA CONTINUAR",
                color = Color(0x80FFFFFF),
                fontSize = 11.sp,
                letterSpacing = 1.5.sp
            )

            Spacer(modifier = Modifier.height(34.dp))

            // Card central que agrupa todas las acciones de autenticación.
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = ColoresApp.CardSoft),
                shape = RoundedCornerShape(28.dp)
            ) {
                Column(modifier = Modifier.padding(22.dp)) {
                    // Título corto para que el usuario sepa en qué flujo está.
                    Text(
                        text = "Bienvenido",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Accede a tu cuenta",
                        color = Color(0x99FFFFFF),
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(22.dp))

                    // Input del correo.
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correo electrónico") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = authTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Input de contraseña ocultando el texto.
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        colors = authTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Enlace auxiliar para iniciar el reseteo de contraseña.
                    TextButton(
                        onClick = {
                            scope.launch {
                                // Si no hay correo no tiene sentido pedir el reseteo.
                                if (email.isBlank()) {
                                    error = "Introduce tu correo para recuperar la contraseña"
                                    return@launch
                                }

                                try {
                                    loading = true
                                    error = null
                                    authRepository.resetPassword(email.trim())
                                    error = "Te hemos enviado un correo para restablecer la contraseña"
                                } catch (e: Exception) {
                                    Log.e("RESET_PASSWORD", "Error reset password", e)
                                    error = getFriendlyAuthError(e)
                                } finally {
                                    loading = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "¿Has olvidado tu contraseña?",
                            color = ColoresApp.AccentGreen
                        )
                    }

                    error?.let {
                        Spacer(modifier = Modifier.height(10.dp))
                        // Este texto reutiliza el mismo hueco tanto para errores como para avisos del reset.
                        Text(
                            text = it,
                            color = Color(0xFFFF6B6B),
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    // Botón principal del login tradicional.
                    Button(
                        onClick = {
                            scope.launch {
                                // Validaciones básicas antes de llamar a Firebase.
                                if (email.isBlank() && password.isBlank()) {
                                    error = "Introduce tu correo y contraseña"
                                    return@launch
                                }

                                if (email.isBlank()) {
                                    error = "Introduce tu correo electrónico"
                                    return@launch
                                }

                                if (password.isBlank()) {
                                    error = "Introduce tu contraseña"
                                    return@launch
                                }

                                try {
                                    loading = true
                                    error = null
                                    authRepository.login(email.trim(), password)
                                    onLoginSuccess()
                                } catch (e: Exception) {
                                    Log.e("EMAIL_AUTH", "Error Email Sign-In", e)
                                    error = getFriendlyAuthError(e)
                                } finally {
                                    loading = false
                                }
                            }
                        },
                        enabled = !loading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ColoresApp.AccentGreen,
                            disabledContainerColor = ColoresApp.CardSoft
                        )
                    ) {
                        Text(
                            text = if (loading) "Entrando..." else "Entrar",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Alternativa de acceso con Google.
                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                try {
                                    loading = true
                                    error = null

                                    // El cliente se encarga de pedir la credencial y abrir sesión en Firebase.
                                    googleAuthClient.signInWithGoogle()

                                    onLoginSuccess()
                                } catch (e: Exception) {
                                    Log.e("GOOGLE_AUTH", "Error Google Sign-In", e)
                                    error = "No se pudo iniciar sesión con Google. Inténtalo de nuevo"
                                } finally {
                                    loading = false
                                }
                            }
                        },
                        enabled = !loading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .border(
                                width = 1.dp,
                                color = ColoresApp.CardSoft,
                                shape = RoundedCornerShape(18.dp)
                            ),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color(0x0DFFFFFF),
                            contentColor = Color.White,
                            disabledContentColor = Color(0x66FFFFFF)
                        )
                    ) {
                        Text(
                            text = "Continuar con Google",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Enlace para saltar a la pantalla de registro.
                    TextButton(
                        onClick = onGoToRegister,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "¿No tienes cuenta? Crear cuenta",
                            color = ColoresApp.AccentGreen
                        )
                    }
                }
            }
        }
    }
}

@Composable
// Colores comunes para no repetir la configuración visual de todos los inputs de auth.
fun authTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    focusedBorderColor = ColoresApp.AccentGreen,
    unfocusedBorderColor = Color(0x33FFFFFF),
    focusedLabelColor = ColoresApp.AccentGreen,
    unfocusedLabelColor = Color(0x99FFFFFF),
    cursorColor = ColoresApp.AccentGreen
)
