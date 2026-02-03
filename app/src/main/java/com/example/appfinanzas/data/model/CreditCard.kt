package com.example.appfinanzas.data.model

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt

data class CreditCard(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val bankName: String = "",
    val lastFourDigits: Int = 0,
    val expiryDate: String = "",
    val cutOffDay: Int = 1,
    val dueDay: Int = 1,
    val colorStartHex: String = "#FF1B5E20",
    val colorEndHex: String = "#FF4CAF50",
    val remindersActive: Boolean = true
) {
    // Propiedades auxiliares para usar en Compose sin romper Firebase
    val colorStart: Color get() = Color(colorStartHex.toColorInt())
    val colorEnd: Color get() = Color(colorEndHex.toColorInt())
}