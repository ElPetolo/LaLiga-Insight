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
fun AppBottomBar(
    selectedTab: String,
    onHomeClick: () -> Unit,
    onRankingClick: () -> Unit,
    onCompareClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    NavigationBar(
        containerColor = Color(0xFF07140F),
        windowInsets = WindowInsets(0.dp)
    ) {
        NavigationBarItem(
            selected = selectedTab == "Home",
            onClick = onHomeClick,
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                    tint = if (selectedTab == "Home") Color(0xFF1D9E75) else Color(0x99FFFFFF)
                )
            },
            label = {
                Text(
                    text = "Home",
                    color = if (selectedTab == "Home") Color(0xFF1D9E75) else Color(0x99FFFFFF)
                )
            }
        )

        NavigationBarItem(
            selected = selectedTab == "Rankings",
            onClick = onRankingClick,
            icon = {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Rankings",
                    tint = if (selectedTab == "Rankings") Color(0xFF1D9E75) else Color(0x99FFFFFF)
                )
            },
            label = {
                Text(
                    text = "Rankings",
                    color = if (selectedTab == "Rankings") Color(0xFF1D9E75) else Color(0x99FFFFFF)
                )
            }
        )

        NavigationBarItem(
            selected = selectedTab == "Compare",
            onClick = onCompareClick,
            icon = {
                Icon(
                    imageVector = Icons.Default.CompareArrows,
                    contentDescription = "Compare",
                    tint = if (selectedTab == "Compare") Color(0xFF1D9E75) else Color(0x99FFFFFF)
                )
            },
            label = {
                Text(
                    text = "Compare",
                    color = if (selectedTab == "Compare") Color(0xFF1D9E75) else Color(0x99FFFFFF)
                )
            }
        )

        NavigationBarItem(
            selected = selectedTab == "Profile",
            onClick = onProfileClick,
            icon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = if (selectedTab == "Profile") Color(0xFF1D9E75) else Color(0x99FFFFFF)
                )
            },
            label = {
                Text(
                    text = "Profile",
                    color = if (selectedTab == "Profile") Color(0xFF1D9E75) else Color(0x99FFFFFF)
                )
            }
        )
    }
}