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
fun FondoAutenticacion(
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ColoresApp.MainBackgroundBrush)
    ) {
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

        content()
    }
}