package com.example.appfinanzas.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appfinanzas.TopAppBarCustom
import com.example.appfinanzas.ui.dashboard.components.MonthlySummary
import com.example.appfinanzas.ui.dashboard.components.SpendingBreakdownCard
import com.example.appfinanzas.ui.dashboard.components.QuickAddExpenseCard
import com.example.appfinanzas.ui.dashboard.components.TransactionItem
import com.example.appfinanzas.ui.dashboard.components.AlertItem

@Composable
fun DashboardContent(viewModel: DashboardViewModel = viewModel()) {
    Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            MonthlySummary(amount = viewModel.balance)

            SpendingBreakdownCard(
                total = viewModel.totalExpenses,
                efectivo = viewModel.regularAmount,
                tarjetas = viewModel.gastosPorTarjeta
            )

            QuickAddExpenseCard(viewModel = viewModel)

            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Transacciones Recientes", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    if (viewModel.transactions.isEmpty()) {
                        Text("No hay gastos aún", modifier = Modifier.padding(16.dp), color = Color.Gray)
                    } else {
                        viewModel.transactions.take(5).forEach { item ->
                            TransactionItem(item, onDelete = { viewModel.deleteTransaction(item.id) })
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sección de Alertas
            Column(modifier = Modifier.padding(bottom = 16.dp)) {
                AlertItem(
                    title = "Budget Limit Alert",
                    subtitle = "You've used 85% of your food budget",
                    amount = "$20 left",
                    color = Color(0xFFE67E22),
                    icon = Icons.Default.Warning
                )

                // Nueva Alerta de Pago agregada del HTML
                AlertItem(
                    title = "Payment Reminder",
                    subtitle = "Credit Card due in 3 days",
                    amount = "$850.00",
                    color = Color(0xFF3498DB),
                    icon = Icons.Default.CreditCard
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
}