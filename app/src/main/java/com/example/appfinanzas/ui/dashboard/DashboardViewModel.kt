package com.example.appfinanzas.ui.dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.appfinanzas.data.firebase.TransactionRepository
import com.example.appfinanzas.data.model.Transaction

class DashboardViewModel : ViewModel() {
    private val repository = TransactionRepository()

    // Estados para la UI
    var isLoading by mutableStateOf(false)
    var transactionSuccess by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var totalAmount by mutableDoubleStateOf(0.0)
    var regularAmount by mutableDoubleStateOf(0.0)
    var creditAmount by mutableDoubleStateOf(0.0)
    var gastosPorTarjeta by mutableStateOf(emptyMap<String, Double>())

    init {
        loadTransactions()
    }

    private fun loadTransactions() {
        repository.getTransactions { list ->
            // Calculamos los totales
            totalAmount = list.sumOf { it.amount }
            regularAmount = list.filter { it.paymentMethod == "efectivo" }.sumOf { it.amount }
            creditAmount = list.filter { it.paymentMethod == "tarjeta" }.sumOf { it.amount }
            gastosPorTarjeta = list
                .filter { it.paymentMethod == "tarjeta" && it.creditCardId != null }
                .groupBy { it.creditCardId!! }
                .mapValues { entry -> entry.value.sumOf { it.amount } }
        }
    }

    fun addExpense(amountStr: String, category: String, method: String, cardId: String?) {
        val amountValue = amountStr.toDoubleOrNull() ?: 0.0

        if (amountValue <= 0.0) {
            errorMessage = "El monto debe ser mayor a 0"
            return
        }

        isLoading = true
        errorMessage = null

        val newTransaction = Transaction(
            amount = amountValue,
            categoryId = category, // Aquí usamos el nombre por ahora
            paymentMethod = method.lowercase(),
            creditCardId = if (method == "Tarjeta") cardId else null,
            type = "expense"
        )

        repository.addTransaction(newTransaction) { success ->
            isLoading = false
            if (success) {
                transactionSuccess = true
                // Podríamos limpiar el mensaje de éxito después de un tiempo
            } else {
                errorMessage = "Error al guardar en la base de datos"
            }
        }
    }

    fun resetStatus() {
        transactionSuccess = false
        errorMessage = null
    }
}