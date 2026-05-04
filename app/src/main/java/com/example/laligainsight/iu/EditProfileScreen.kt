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
import com.example.laligainsight.Auth.UserRepository
import kotlinx.coroutines.launch
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import java.io.File
import java.io.FileOutputStream


fun bitmapToUri(context: Context, bitmap: Bitmap): Uri {
    val file = File(context.cacheDir, "temp_profile.jpg")
    val stream = FileOutputStream(file)
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
    stream.flush()
    stream.close()
    return Uri.fromFile(file)
}

@Composable
fun EditProfileScreen(
    currentUsername: String,
    currentProfileImageUrl: String,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val repo = remember { UserRepository() }
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
        val user = repo.getUser()
        if (user != null) {
            username = user.username
            currentImageUrl = user.profileImageUrl
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        imageUri = uri
        cameraBitmap = null
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        cameraBitmap = bitmap
        imageUri = null
    }

    if (showImageOptions) {
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
            .padding(24.dp)
    ) {
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

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(124.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0F6E56))
                    .clickable { showImageOptions = true }
            ) {
                when {
                    imageUri != null -> {
                        AsyncImage(
                            model = imageUri,
                            contentDescription = "Nueva foto",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    cameraBitmap != null -> {
                        AsyncImage(
                            model = cameraBitmap,
                            contentDescription = "Nueva foto",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    currentImageUrl.isNotEmpty() -> {
                        AsyncImage(
                            model = currentImageUrl,
                            contentDescription = "Foto actual",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    else -> {
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
                containerColor = Color(0xFF1D9E75)
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