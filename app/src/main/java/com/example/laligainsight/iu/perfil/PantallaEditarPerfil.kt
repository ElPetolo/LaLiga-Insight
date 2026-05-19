package com.example.laligainsight.iu

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.laligainsight.autenticacion.RepositorioUsuario
import kotlinx.coroutines.launch
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import java.io.File
import java.io.FileOutputStream


// Convierte una foto capturada como Bitmap en un archivo temporal para poder subirla a Firebase Storage.
fun bitmapToUri(context: Context, bitmap: Bitmap): Uri {
    val file = File(context.cacheDir, "temp_profile.jpg")
    val stream = FileOutputStream(file)
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
    stream.flush()
    stream.close()
    return Uri.fromFile(file)
}

@Composable
// Pantalla de edición del perfil con cambio de username y foto.
fun PantallaEditarPerfil(
    currentUsername: String,
    currentProfileImageUrl: String,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val repo = remember { RepositorioUsuario() }
    val scope = rememberCoroutineScope()

    var username by remember { mutableStateOf(currentUsername) }
    var currentImageUrl by remember { mutableStateOf(currentProfileImageUrl) }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var cameraBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showImageOptions by remember { mutableStateOf(false) }
    val context = LocalContext.current


    LaunchedEffect(Unit) {
        // Cargamos los datos reales del usuario para no depender de valores pasados vacíos.
        val user = repo.getUser()
        if (user != null) {
            username = user.username
            currentImageUrl = user.profileImageUrl
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        // Si se elige imagen de galería, limpiamos la posible foto de cámara previa.
        imageUri = uri
        cameraBitmap = null
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        // Si se usa la cámara, anulamos la imagen de galería.
        cameraBitmap = bitmap
        imageUri = null
    }

    if (showImageOptions) {
        // Diálogo para que el usuario elija de dónde sale la nueva foto de perfil.
        AlertDialog(
            onDismissRequest = { showImageOptions = false },
            title = {
                Text("Cambiar foto")
            },
            text = {
                Text("Elige cómo quieres cambiar tu foto de perfil")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showImageOptions = false
                        galleryLauncher.launch("image/*")
                    }
                ) {
                    Text("Galería")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showImageOptions = false
                        cameraLauncher.launch(null)
                    }
                ) {
                    Text("Cámara")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColoresApp.MainBackgroundBrush)
            .statusBarsPadding()
            .padding(24.dp)
    ) {
        // Botón para volver sin guardar.
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Volver",
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Editar perfil",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Zona central de la foto de perfil editable.
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Avatar pulsable que abre las opciones de cambio de imagen.
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(124.dp)
                    .clip(CircleShape)
                    .background(ColoresApp.AvatarBackground)
                    .clickable { showImageOptions = true }
            ) {
                when {
                    imageUri != null -> {
                        // Previsualización de la imagen elegida desde galería.
                        AsyncImage(
                            model = imageUri,
                            contentDescription = "Nueva foto",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    cameraBitmap != null -> {
                        // Previsualización de la imagen sacada con cámara.
                        AsyncImage(
                            model = cameraBitmap,
                            contentDescription = "Nueva foto",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    currentImageUrl.isNotEmpty() -> {
                        // Si todavía no se ha cambiado nada, mostramos la foto actual.
                        AsyncImage(
                            model = currentImageUrl,
                            contentDescription = "Foto actual",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    else -> {
                        // Fallback si el usuario aún no tiene foto.
                        Text(
                            text = "Foto",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Toca la foto para cambiarla",
            color = Color(0x80FFFFFF),
            fontSize = 13.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Campo del username editable.
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = authTextFieldColors()
        )

        error?.let {
            Spacer(modifier = Modifier.height(10.dp))
            Text(it, color = Color(0xFFFF6B6B))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Acción principal para guardar los cambios del perfil.
        Button(
            onClick = {
                scope.launch {
                    val clean = username.trim()

                    if (clean.length < 3) {
                        error = "El username debe tener mínimo 3 caracteres"
                        return@launch
                    }

                    try {
                        loading = true
                        error = null

                        try {
                            // Primero actualizamos el username, porque es la validación más sensible.
                            repo.updateUsername(clean)
                        } catch (e: Exception) {
                            error = e.message
                            return@launch
                        }


                        val finalUri = when {
                            imageUri != null -> imageUri
                            imageBitmap != null -> bitmapToUri(context, imageBitmap!!)
                            else -> null
                        }

                        if (finalUri != null) {
                            // Si hay imagen nueva, la subimos y guardamos la URL definitiva.
                            val url = repo.uploadProfileImage(finalUri)
                            repo.updateProfileImage(url)
                        }

                        // Cámara: de momento muestra la foto en pantalla.
                        // Para subirla a Storage hay que convertir Bitmap a Uri/archivo.
                        // Lo hacemos en el siguiente paso si quieres cámara 100% funcional.

                        onSaved()
                    } catch (e: Exception) {
                        error = e.localizedMessage ?: "Error al guardar"
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
                containerColor = ColoresApp.AccentGreen
            )
        ) {
            Text(
                text = if (loading) "Guardando..." else "Guardar cambios",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
