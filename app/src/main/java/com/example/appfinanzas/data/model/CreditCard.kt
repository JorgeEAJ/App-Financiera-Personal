package com.example.appfinanzas.data.model

import androidx.compose.ui.graphics.Color

data class CreditCard(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val bankName: String = "",
    val lastFourDigits: Int = 0,
    val expiryDate: String = "",
    val cutOffDay: Int = 1,
    val dueDay: Int = 1,
    val colorStartInt: Long = 0xFF1B5E20,
    val colorEndInt: Long = 0xFF4CAF50,
    val remindersActive: Boolean = true
) {
    // Propiedades auxiliares para usar en Compose sin romper Firebase
    val colorStart: Color get() = Color(colorStartInt)
    val colorEnd: Color get() = Color(colorEndInt)
}