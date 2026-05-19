package com.example.laligainsight.iu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
// Cabecera reutilizable para dar contexto visual a las pantallas principales.
fun CabeceraPantalla(
    title: String,
    subtitle: String,
    badge: String,
    icon: ImageVector,
    actionIcon: ImageVector? = null,
    onActionClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 14.dp),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = ColoresApp.CardSoftStrong
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        // Esta Box actúa como lienzo de la cabecera: fondo, icono decorativo y textos.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            ColoresApp.CardDark,
                            ColoresApp.AccentBlue.copy(alpha = 0.45f),
                            ColoresApp.CardSoftStrong
                        )
                    )
                )
                .padding(horizontal = 28.dp, vertical = 30.dp)
        ) {
            // Icono grande de fondo solo decorativo para reforzar el tema de la pantalla.
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ColoresApp.AccentGreen.copy(alpha = 0.10f),
                modifier = Modifier
                    .size(110.dp)
                    .align(Alignment.BottomEnd)
            )

            // Columna principal con badge, título y subtítulo.
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        // Badge pequeño para situar rápido el contexto de la pantalla.
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(ColoresApp.AccentGreen.copy(alpha = 0.16f))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = badge.uppercase(),
                                color = ColoresApp.AccentGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = title,
                            color = ColoresApp.TextPrimary,
                            fontSize = 31.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 34.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = subtitle,
                            color = ColoresApp.TextSecondary,
                            fontSize = 14.sp,
                            lineHeight = 19.sp
                        )
                    }

                    if (actionIcon != null && onActionClick != null) {
                        // Botón opcional de acción rápida, por ejemplo notificaciones.
                        IconButton(
                            onClick = onActionClick,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(ColoresApp.CardDark)
                        ) {
                            Icon(
                                imageVector = actionIcon,
                                contentDescription = null,
                                tint = ColoresApp.TextPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}
