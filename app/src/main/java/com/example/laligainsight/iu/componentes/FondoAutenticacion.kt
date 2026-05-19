package com.example.laligainsight.iu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
// Fondo compartido de login y registro para mantener la misma ambientación visual.
fun FondoAutenticacion(
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ColoresApp.MainBackgroundBrush)
    ) {
        // Mancha superior para romper el fondo plano y centrar la atención en el formulario.
        Box(
            modifier = Modifier
                .size(340.dp)
                .offset(x = 35.dp, y = (-120).dp)
                .background(
                    Brush.radialGradient(
                        listOf(Color(0x551D9E75), Color.Transparent)
                    ),
                    CircleShape
                )
        )

        // Mancha inferior secundaria que equilibra visualmente la composición.
        Box(
            modifier = Modifier
                .size(280.dp)
                .offset(x = (-120).dp, y = 520.dp)
                .background(
                    Brush.radialGradient(
                        listOf(ColoresApp.CardSoft, Color.Transparent)
                    ),
                    CircleShape
                )
        )

        // Aquí se dibuja el contenido real de login o registro encima del fondo decorativo.
        content()
    }
}
