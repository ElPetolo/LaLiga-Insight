package com.example.laligainsight.iu

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
// Barra inferior común para movernos entre las cuatro secciones principales.
fun BarraInferiorApp(
    selectedTab: String,
    onHomeClick: () -> Unit,
    onRankingClick: () -> Unit,
    onCompareClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    NavigationBar(
        containerColor = ColoresApp.BottomBar
    ) {
        // Acceso directo a la pantalla principal de equipos.
        NavigationBarItem(
            selected = selectedTab == "Home",
            onClick = onHomeClick,
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                    tint = if (selectedTab == "Home") ColoresApp.AccentGreen else ColoresApp.TextSecondary
                )
            },
            label = {
                Text(
                    text = "Home",
                    color = if (selectedTab == "Home") ColoresApp.AccentGreen else ColoresApp.TextSecondary
                )
            }
        )

        // Entrada a clasificación y rankings.
        NavigationBarItem(
            selected = selectedTab == "Rankings",
            onClick = onRankingClick,
            icon = {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Rankings",
                    tint = if (selectedTab == "Rankings") ColoresApp.AccentGreen else ColoresApp.TextSecondary
                )
            },
            label = {
                Text(
                    text = "Rankings",
                    color = if (selectedTab == "Rankings") ColoresApp.AccentGreen else ColoresApp.TextSecondary
                )
            }
        )

        // Entrada al comparador de jugadores.
        NavigationBarItem(
            selected = selectedTab == "Compare",
            onClick = onCompareClick,
            icon = {
                Icon(
                    imageVector = Icons.Default.CompareArrows,
                    contentDescription = "Compare",
                    tint = if (selectedTab == "Compare") ColoresApp.AccentGreen else ColoresApp.TextSecondary
                )
            },
            label = {
                Text(
                    text = "Compare",
                    color = if (selectedTab == "Compare") ColoresApp.AccentGreen else ColoresApp.TextSecondary
                )
            }
        )

        // Entrada al perfil del usuario.
        NavigationBarItem(
            selected = selectedTab == "Profile",
            onClick = onProfileClick,
            icon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = if (selectedTab == "Profile") ColoresApp.AccentGreen else ColoresApp.TextSecondary
                )
            },
            label = {
                Text(
                    text = "Profile",
                    color = if (selectedTab == "Profile") ColoresApp.AccentGreen else ColoresApp.TextSecondary
                )
            }
        )
    }
}
