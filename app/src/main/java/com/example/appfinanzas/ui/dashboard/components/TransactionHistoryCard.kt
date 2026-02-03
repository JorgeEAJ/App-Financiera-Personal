package com.example.appfinanzas.ui.dashboard.components

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appfinanzas.ui.dashboard.DashboardViewModel
import com.example.appfinanzas.ui.theme.PrimaryGreen
import java.text.SimpleDateFormat
import java.util.Locale

@SuppressLint("DefaultLocale")
@Composable
fun TransactionHistoryCard(viewModel: DashboardViewModel) {
    // Estado local para controlar si la lista está expandida o no
    var isExpanded by remember { mutableStateOf(false) }
    val monthYearLabel = remember(viewModel.selectedMonth) {
        val sdf = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        sdf.format(viewModel.selectedMonth.time).replaceFirstChar { it.uppercase() }
    }

    val totalIncomes = viewModel.transactions.filter { it.type == "income" }.sumOf { it.amount }
    val totalExpenses = viewModel.transactions.filter { it.type == "expense" }.sumOf { it.amount }
    val balance = totalIncomes - totalExpenses
    val balanceColor = if (balance >= 0) PrimaryGreen else Color.Red

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Selector de Mes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.changeMonth(-1) }) {
                    Icon(Icons.Default.ArrowBackIosNew, null, modifier = Modifier.size(16.dp))
                }
                Text(monthYearLabel, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                IconButton(onClick = { viewModel.changeMonth(1) }) {
                    Icon(Icons.Default.ArrowForwardIos, null, modifier = Modifier.size(16.dp))
                }
            }

            // Resumen (Ingresos/Gastos)
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                SummaryColumn("Ingresos", "+$${String.format("%.2f", totalIncomes)}", PrimaryGreen)
                SummaryColumn("Gastos", "-$${String.format("%.2f", totalExpenses)}", Color.Red.copy(0.7f))
                SummaryColumn("Balance", "${if (balance >= 0) "+" else ""}$${String.format("%.2f", balance)}", balanceColor)
            }
            Spacer(modifier = Modifier.height(8.dp))

            if (viewModel.transactions.isEmpty()) {
                Box(Modifier.fillMaxWidth().height(80.dp), contentAlignment = Alignment.Center) {
                    Text("No hay registros", color = Color.Gray, fontSize = 14.sp)
                }
            } else {
                // LÓGICA DE EXPANSIÓN
                val displayList = if (isExpanded) viewModel.transactions else viewModel.transactions.take(5)

                displayList.forEach { item ->
                    TransactionItem(
                        transaction = item,
                        onDelete = { viewModel.deleteTransaction(item.id) },
                        getIcon = { viewModel.getIconFromName(it) }
                    )
                }

                // Botón dinámico que cambia de texto según el estado
                if (viewModel.transactions.size > 4) {
                    androidx.compose.material3.TextButton(
                        onClick = { isExpanded = !isExpanded }, // Cambia el estado
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                            text = if (isExpanded) "Mostrar menos" else "Ver todas (${viewModel.transactions.size})",
                            color = PrimaryGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryColumn(label: String, amount: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 11.sp, color = Color.Gray)
        Text(amount, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = color)
    }
}