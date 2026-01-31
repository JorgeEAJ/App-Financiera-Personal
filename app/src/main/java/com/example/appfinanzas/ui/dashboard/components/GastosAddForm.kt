package com.example.appfinanzas.ui.dashboard.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appfinanzas.ui.dashboard.DashboardViewModel
import com.example.appfinanzas.ui.theme.PrimaryGreen


@Composable
fun QuickAddExpenseCard(viewModel: DashboardViewModel) {
    var amount by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Comida") }
    var paymentMethod by remember { mutableStateOf("Efectivo") }
    val walletCards = viewModel.walletCards
    var selectedCardId by remember { mutableStateOf("") }
    var transactionType by remember { mutableStateOf("expense") }

    val categories = listOf(
        "Comida" to Icons.Default.Restaurant,
        "Transporte" to Icons.Default.DirectionsCar,
        "Compras" to Icons.Default.ShoppingBag,
        "Renta" to Icons.Default.House
    )

    LaunchedEffect(viewModel.transactionSuccess) {
        if (viewModel.transactionSuccess) {
            amount = ""
            viewModel.resetStatus()
        }
    }
    LaunchedEffect(walletCards) {
        if (walletCards.isNotEmpty() && selectedCardId.isEmpty()) {
            selectedCardId = walletCards[0].id
        }
    }

    Card(
        modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            TabRow(
                selectedTabIndex = if (transactionType == "expense") 0 else 1,
                containerColor = Color.Transparent,
                divider = {},
                indicator = { tabPositions ->
                    SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[if (transactionType == "expense") 0 else 1]),
                        color = if (transactionType == "expense") Color.Red else PrimaryGreen
                    )
                }
            ) {
                Tab(
                    selected = transactionType == "expense",
                    onClick = { transactionType = "expense" },
                    text = { Text("Gasto") }
                )
                Tab(
                    selected = transactionType == "income",
                    onClick = { transactionType = "income" },
                    text = { Text("Ingreso") }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            //Caja de dinero
            TextField(
                value = amount,
                onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) amount = it },
                textStyle = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                placeholder = { Text("0.00", fontSize = 24.sp, fontWeight = FontWeight.Bold) },
                prefix = { Text("$ ", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = if (transactionType == "expense") Color.Red else PrimaryGreen
                ),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            //Tab Gastos
            if (transactionType == "expense") {

                Text("Método de Pago", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Efectivo", "Tarjeta").forEach { method ->
                        FilterChip(
                            selected = paymentMethod == method,
                            onClick = { paymentMethod = method },
                            label = { Text(method) },
                            leadingIcon = {
                                Icon(if(method == "Efectivo") Icons.Default.Payments else Icons.Default.CreditCard, null, modifier = Modifier.size(18.dp))
                            }
                        )
                    }
                }

                if (paymentMethod == "Tarjeta") {
                    Text("Seleccionar Tarjeta", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)

                    if (walletCards.isEmpty()) {
                        Text("No tienes tarjetas registradas", fontSize = 12.sp, modifier = Modifier.padding(8.dp))
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            walletCards.forEach { card ->
                                val isSelected = selectedCardId == card.id
                                InputChip(
                                    selected = isSelected,
                                    onClick = { selectedCardId = card.id },
                                    label = { Text("${card.bankName} •••• ${card.lastFourDigits}", fontSize = 12.sp) }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text("Categoría", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)

                // Chips de Categorías
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { (name, icon) ->
                        CategoryChip(
                            label = name,
                            icon = icon,
                            selected = selectedCategory == name,
                            onClick = { selectedCategory = name }
                        )
                    }
                }
            }

            // BOTÓN DE ACCIÓN
            Button(
                onClick = {
                    viewModel.addExpense(
                        amountStr = amount,
                        category = if (transactionType == "expense") selectedCategory else "Ingreso",
                        method = if (transactionType == "expense") paymentMethod else "Depósito",
                        cardId = if (transactionType == "expense" && paymentMethod == "Tarjeta") selectedCardId else null,
                        type = transactionType
                    )
                },
                enabled = amount.isNotEmpty() && !viewModel.isLoading,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (transactionType == "expense") Color.Black else PrimaryGreen,
                    contentColor = if (transactionType == "expense") Color.White else Color(0xFF112116)
                )
            ) {
                Text(
                    text = if (transactionType == "expense") "Añadir Gasto" else "Añadir Ingreso",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun CategoryChip(label: String, icon: ImageVector, selected: Boolean = false, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        color = if (selected) PrimaryGreen else MaterialTheme.colorScheme.background,
        shape = CircleShape,
        border = if (!selected) androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f)) else null
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = if (selected) Color.Black else MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.width(8.dp))
            Text(label, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (selected) Color.Black else MaterialTheme.colorScheme.onSurface)
        }
    }
}