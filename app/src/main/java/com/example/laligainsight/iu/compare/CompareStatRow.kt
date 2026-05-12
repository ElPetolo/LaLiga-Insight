package com.example.laligainsight.iu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun CompareStatRow(
    title: String,
    value1: String,
    value2: String,
    number1: Double,
    number2: Double
) {
    // Sumamos ambos valores para calcular el reparto visual de la barra.
    val total = number1 + number2

    val leftWeight = if (total > 0) {
        (number1 / total).toFloat().coerceAtLeast(0.08f)
    } else {
        0.5f
    }

    val rightWeight = if (total > 0) {
        (number2 / total).toFloat().coerceAtLeast(0.08f)
    } else {
        0.5f
    }

    // El mejor valor se pinta en verde para que la comparación sea más directa.
    val color1 = if (number1 >= number2) AppColors.AccentGreen else Color.White
    val color2 = if (number2 >= number1) AppColors.AccentGreen else Color.White

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 14.dp),
        colors = CardDefaults.cardColors(
            containerColor = AppColors.CardSoftStrong
        ),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = value1,
                    color = color1,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = title.uppercase(),
                    color = Color(0x99FFFFFF),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(2f),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = value2,
                    color = color2,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .background(Color(0x22000000))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(leftWeight)
                        .background(
                            if (number1 >= number2) AppColors.AccentGreen
                            else Color(0x55FFFFFF)
                        )
                )

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(rightWeight)
                        .background(
                            if (number2 >= number1) AppColors.AccentGreen
                            else Color(0x55FFFFFF)
                        )
                )
            }
        }
    }
}
