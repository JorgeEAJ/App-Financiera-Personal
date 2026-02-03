package com.example.appfinanzas.ui.dashboard

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import com.example.appfinanzas.data.firebase.CategoryRepository
import com.example.appfinanzas.data.firebase.TransactionRepository
import com.example.appfinanzas.data.firebase.WalletRepository
import com.example.appfinanzas.data.model.Category
import com.example.appfinanzas.data.model.CreditCard
import com.example.appfinanzas.data.model.Transaction
import java.util.Calendar

class DashboardViewModel : ViewModel() {
    private val repository = TransactionRepository()
    private val repositoryCards = WalletRepository()
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
    var categories by mutableStateOf<List<Category>>(emptyList())
    private val repo = CategoryRepository()

    init {
        loadTransactions()
        loadUserCards()
        repo.getCategories { list ->
            if (list.isEmpty()) {
                setupDefaultCategories()
            } else {
                categories = list
            }
        }
    }

    private fun loadTransactions() {
        repository.getTransactions { list ->
            transactions = list.sortedByDescending { it.date }
            totalAmount = list.sumOf { it.amount }
            creditAmount = list.filter { it.method == "tarjeta" }.sumOf { it.amount }

            // Calculamos sumas por tipo
            totalIncomes = list.filter { it.type == "income" }.sumOf { it.amount }
            totalExpenses = list.filter { it.type == "expense" }.sumOf { it.amount }

            // El balance neto
            balance = totalIncomes - totalExpenses

            // Filtramos solo gastos para la gráfica de dona
            val expenseList = list.filter { it.type == "expense" }
            regularAmount = expenseList.filter { it.method == "efectivo" }.sumOf { it.amount }

            gastosPorTarjeta = expenseList
                .filter { it.method == "tarjeta" && it.creditCardId != null }
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
            categoryId = category,
            method = method.lowercase(),
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
    private fun setupDefaultCategories() {
        val defaults = listOf(
            Category(name = "Comida", iconName = "Restaurant"),
            Category(name = "Transporte", iconName = "DirectionsCar"),
            Category(name = "Compras", iconName = "ShoppingBag")
        )
        defaults.forEach { repo.addCategory(it) }
    }
    fun addCategory(name: String) {
        repo.addCategory(Category(name = name, iconName = "Payments"))
    }
    fun removeCategory(category: Category) {
        repo.deleteCategory(category.id)
    }
    // Función auxiliar para convertir el nombre de Firebase al icono real
    fun getIconFromName(name: String): ImageVector {
        return when (name) {
            "Comida" -> Icons.Default.Restaurant
            "Transporte" -> Icons.Default.DirectionsCar
            "Compras" -> Icons.Default.ShoppingBag
            "Casa" -> Icons.Default.House
            else -> Icons.Default.Payments
        }
    }
    var selectedMonth by mutableStateOf(Calendar.getInstance()) // Mes actual por defecto

    fun changeMonth(delta: Int) {
        val newMonth = selectedMonth.clone() as Calendar
        newMonth.add(Calendar.MONTH, delta)
        selectedMonth = newMonth
        transactions = emptyList()

        loadTransactionsForMonth()
    }
    fun loadTransactionsForMonth() {
        val calendar = selectedMonth.clone() as Calendar

        // Configurar al primer día del mes a las 00:00:00
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val startOfMonth = calendar.timeInMillis

        // Configurar al último día del mes a las 23:59:59
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        val endOfMonth = calendar.timeInMillis

        // Llamar al repositorio con el rango de fechas
        repository.getTransactionsByRange(startOfMonth, endOfMonth) { list ->
            transactions = list.sortedByDescending { it.date }
        }
    }}