package com.example.appfinanzas.data.model

data class Category(
    val id: String = "",
    val userId: String = "", // null si es una categoría por defecto
    val name: String = "",
    val type: String = "expense",
    val icon: String = "default" // Para mostrar en la UI
)