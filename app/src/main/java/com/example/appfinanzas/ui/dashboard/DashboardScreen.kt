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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.appfinanzas.ui.dashboard.components.TransactionHistoryCard

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

            TransactionHistoryCard(viewModel = viewModel)
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