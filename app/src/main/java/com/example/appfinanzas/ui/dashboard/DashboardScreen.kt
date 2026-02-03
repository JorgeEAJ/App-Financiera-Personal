package com.example.appfinanzas.ui.dashboard

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appfinanzas.ui.dashboard.components.SpendingBreakdownCard
import com.example.appfinanzas.ui.dashboard.components.QuickAddExpenseCard
import com.example.appfinanzas.ui.dashboard.components.TransactionItem
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

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

            Spacer(modifier = Modifier.height(24.dp))
        }
}
@SuppressLint("DefaultLocale")
@Composable
fun MonthlySummary(amount: Double) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text("RESUMEN MENSUAL", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
        Text("$${String.format("%,.2f", amount)}", fontSize = 40.sp, fontWeight = FontWeight.Bold)
    }
}