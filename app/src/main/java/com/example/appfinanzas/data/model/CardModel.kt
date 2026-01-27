package com.example.appfinanzas.data.model
data class CreditCard(
    val id: String = "",
    val name: String,
    val bankName: String,
    val lastFourDigits: Int,
    val expiryDate: String,
    val cutOffDay: Int,      // Día de corte (1-31)
    val dueDay: Int,         // Día de pago (1-31)
    val colorStart: androidx.compose.ui.graphics.Color,
    val colorEnd: androidx.compose.ui.graphics.Color,
    val remindersActive: Boolean = true
)