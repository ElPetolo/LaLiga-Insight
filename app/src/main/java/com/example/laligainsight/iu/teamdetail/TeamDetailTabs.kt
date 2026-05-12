package com.example.laligainsight.iu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TeamDetailTabs(
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {
    // Fila de pestañas del detalle del equipo.
    // Permite cambiar entre resumen, plantilla y partidos.
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        listOf("Resumen", "Plantilla", "Partidos").forEach { tab ->

            // Cada pestaña ocupa el mismo ancho gracias al weight.
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onTabSelected(tab) }
                    .padding(vertical = 16.dp)
            ) {
                // Texto de la pestaña.
                // Si está seleccionada se muestra en blanco y en negrita.
                Text(
                    text = tab,
                    color = if (tab == selectedTab) Color.White else Color.Gray,
                    fontSize = 16.sp,
                    fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Línea inferior que marca visualmente la pestaña activa.
                Box(
                    modifier = Modifier
                        .height(3.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp)
                        .background(
                            if (tab == selectedTab) AppColors.AccentGreen else Color.Transparent,
                            shape = RoundedCornerShape(50)
                        )
                )
            }
        }
    }
}