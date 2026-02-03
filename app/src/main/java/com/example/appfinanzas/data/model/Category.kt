package com.example.appfinanzas.data.model

data class Category(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val type: String = "expense", // "expense" o "income"
    val iconName: String = "Payments" // Guardamos el nombre del icono
)