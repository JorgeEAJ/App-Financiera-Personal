package com.example.appfinanzas.data.model

data class Transaction(
    val id: String = "",
    val userId: String = "",
    val categoryId: String = "",
    val amount: Double = 0.0,
    val type: String = "expense", // "income" o "expense"
    val date: Long = System.currentTimeMillis(),
    val description: String = "",
    val paymentMethod: String = "cash", // "cash", "debit", "credit"
    val creditCardId: String? = null,
)