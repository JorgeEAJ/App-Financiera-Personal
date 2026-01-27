package com.example.appfinanzas.data.model

data class Category(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val type: String = "expense",
    val icon: String = "default"
)