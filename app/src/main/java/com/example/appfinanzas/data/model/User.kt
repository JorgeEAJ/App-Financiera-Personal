package com.example.appfinanzas.data.model

data class User(
    val id: String = "",
    val email: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
