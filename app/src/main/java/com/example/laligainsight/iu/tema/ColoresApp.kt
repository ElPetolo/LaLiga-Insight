package com.example.laligainsight.iu

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object ColoresApp {

    val BackgroundTop = Color(0xFF23375F)
    val BackgroundMiddle = Color(0xFF182A4A)
    val BackgroundBottom = Color(0xFF0B1426)

    val CardDark = Color(0xFF1D2D50)
    val CardSoft = Color(0x66243A63)
    val CardSoftStrong = Color(0x99243A63)

    val AccentGreen = Color(0xFF35D6A3)
    val AccentBlue = Color(0xFF4D75C9)

    val TextPrimary = Color.White
    val TextSecondary = Color(0xCCFFFFFF)

    val BottomBar = Color(0xFF172747)

    val MainBackgroundBrush = Brush.verticalGradient(
        listOf(
            BackgroundTop,
            BackgroundMiddle,
            BackgroundBottom
        )
    )
    val ButtonPrimary = Color(0xFF2D4374)
    val ButtonSecondary = Color(0xFF243A63)
    val Danger = Color(0xFFC93C3C)
    val AvatarBackground = Color(0xFF2D4374)
    //cesar ,marica
}