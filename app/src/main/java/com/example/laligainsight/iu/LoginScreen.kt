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
import com.example.laligainsight.Auth.AuthRepository
import com.example.laligainsight.Auth.GoogleAuthClient
import com.example.laligainsight.R
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onGoToRegister: () -> Unit
) {
    val authRepository = remember { AuthRepository() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val googleAuthClient = remember { GoogleAuthClient(context) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    AuthBackground {
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
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(AppColors.AvatarBackground)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.isotipo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(58.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row {
                Text("LaLiga", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)
                Text("Insight", color = AppColors.AccentGreen, fontSize = 30.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "INICIA SESIÓN PARA CONTINUAR",
                color = Color(0x80FFFFFF),
                fontSize = 11.sp,
                letterSpacing = 1.5.sp
            )

            Spacer(modifier = Modifier.height(34.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = AppColors.CardSoft),
                shape = RoundedCornerShape(28.dp)
            ) {
                Column(modifier = Modifier.padding(22.dp)) {
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

                    Spacer(modifier = Modifier.height(6.dp))

                    TextButton(
                        onClick = {
                            scope.launch {
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
                                    error = e.localizedMessage ?: "No se pudo enviar el correo"
                                } finally {
                                    loading = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "¿Has olvidado tu contraseña?",
                            color = AppColors.AccentGreen
                        )
                    }

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
                                try {
                                    loading = true
                                    error = null
                                    authRepository.login(email.trim(), password)
                                    onLoginSuccess()
                                } catch (e: Exception) {
                                    Log.e("EMAIL_AUTH", "Error Email Sign-In", e)
                                    error = e.localizedMessage ?: "Correo o contraseña incorrectos"
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
                            containerColor = AppColors.AccentGreen,
                            disabledContainerColor = AppColors.CardSoft
                        )
                    ) {
                        Text(
                            text = if (loading) "Entrando..." else "Entrar",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                try {
                                    loading = true
                                    error = null

                                    googleAuthClient.signInWithGoogle()

                                    onLoginSuccess()
                                } catch (e: Exception) {
                                    Log.e("GOOGLE_AUTH", "Error Google Sign-In", e)
                                    error = e.localizedMessage ?: "No se pudo iniciar sesión con Google"
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
                                color = AppColors.CardSoft,
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

                    TextButton(
                        onClick = onGoToRegister,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "¿No tienes cuenta? Crear cuenta",
                            color = AppColors.AccentGreen
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun authTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    focusedBorderColor = AppColors.AccentGreen,
    unfocusedBorderColor = Color(0x33FFFFFF),
    focusedLabelColor = AppColors.AccentGreen,
    unfocusedLabelColor = Color(0x99FFFFFF),
    cursorColor = AppColors.AccentGreen
)