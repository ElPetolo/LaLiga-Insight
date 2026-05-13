package com.example.laligainsight.iu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.laligainsight.modelo.RatingSummary

@Composable
fun RatingStars(
    summary: RatingSummary,
    onRatingSelected: (Int) -> Unit
) {

    Column {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            for (i in 1..5) {

                Icon(
                    imageVector =
                        if (i <= summary.userRating) {
                            Icons.Filled.Star
                        } else {
                            Icons.Outlined.Star
                        },

                    contentDescription = null,

                    tint =
                        if (i <= summary.userRating) {
                            Color(0xFFFFC107)
                        } else {
                            Color(0x55FFFFFF)
                        },

                    modifier = Modifier
                        .size(34.dp)
                        .clickable {
                            onRatingSelected(i)
                        }
                )

                Spacer(modifier = Modifier.width(4.dp))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text =
                if (summary.count == 0) {
                    "Sin valoraciones"
                } else {
                    "%.1f ★ · ${summary.count} valoraciones"
                        .format(summary.average)
                },

            color = Color(0x99FFFFFF),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}