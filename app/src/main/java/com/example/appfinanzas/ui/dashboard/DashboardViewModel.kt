package com.example.appfinanzas.ui.dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.appfinanzas.data.firebase.TransactionRepository
import com.example.appfinanzas.data.firebase.WalletRepository
import com.example.appfinanzas.data.model.CreditCard
import com.example.appfinanzas.data.model.Transaction

class DashboardViewModel : ViewModel() {
    private val repository = TransactionRepository()
    private val repositoryCards = WalletRepository()

    // Estados para la UI
    var isLoading by mutableStateOf(false)
    var transactionSuccess by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var totalAmount by mutableDoubleStateOf(0.0)
    var regularAmount by mutableDoubleStateOf(0.0)
    var creditAmount by mutableDoubleStateOf(0.0)
    var gastosPorTarjeta by mutableStateOf(emptyMap<String, Double>())
    var transactions by mutableStateOf(emptyList<Transaction>())
    var balance by mutableDoubleStateOf(0.0)
    var totalIncomes by mutableDoubleStateOf(0.0)
    var totalExpenses by mutableDoubleStateOf(0.0)
    var walletCards by mutableStateOf<List<CreditCard>>(emptyList())
        private set

    init {
        loadTransactions()
        loadUserCards()
    }

    private fun loadTransactions() {
        repository.getTransactions { list ->
            transactions = list.sortedByDescending { it.date }
            totalAmount = list.sumOf { it.amount }
            creditAmount = list.filter { it.paymentMethod == "tarjeta" }.sumOf { it.amount }

            // Calculamos sumas por tipo
            totalIncomes = list.filter { it.type == "income" }.sumOf { it.amount }
            totalExpenses = list.filter { it.type == "expense" }.sumOf { it.amount }

            // El balance neto
            balance = totalIncomes - totalExpenses

            // Filtramos solo gastos para la gráfica de dona
            val expenseList = list.filter { it.type == "expense" }
            regularAmount = expenseList.filter { it.paymentMethod == "efectivo" }.sumOf { it.amount }

            gastosPorTarjeta = expenseList
                .filter { it.paymentMethod == "tarjeta" && it.creditCardId != null }
                .groupBy { it.creditCardId!! }
                .mapValues { it.value.sumOf { amount -> amount.amount } }
        }
    }

    fun addExpense(amountStr: String, category: String, method: String, cardId: String?,type: String) {
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
            type = type
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
    fun deleteTransaction(id: String) {
        repository.deleteTransaction(id) { success ->
            if (!success) errorMessage = "No se pudo eliminar el gasto"
        }
    }
    private fun loadUserCards() {
        repositoryCards.getCards { list ->
            walletCards = list
        }
    }
}