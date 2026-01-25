package com.example.appfinanzas.ui.dashboard

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

val PrimaryGreen = Color(0xFF19E65E)
val DarkGreen = Color(0xFF078829)
val LightGreenBg = Color(0xFFE7F3EB)

@Composable
fun DashboardScreen(
    onLogout: () -> Unit,
    viewModel: DashboardViewModel = viewModel()
) {
    Scaffold(
        bottomBar = { BottomNavBar() }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            TopAppBarCustom(onLogout)

            MonthlySummary(amount = viewModel.totalAmount)

            SpendingBreakdownCard(
                total = viewModel.totalAmount,
                efectivo = viewModel.regularAmount,
                tarjetas = viewModel.gastosPorTarjeta
            )

            QuickAddExpenseCard(viewModel = viewModel)

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
}

@Composable
fun TopAppBarCustom(onLogout: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(40.dp).background(PrimaryGreen.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text("Bienvenido", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }

        // Grupo de botones a la derecha
        Row {
            IconButton(onClick = { /* Notificaciones */ }) {
                Icon(Icons.Outlined.Notifications, contentDescription = null)
            }
            IconButton(onClick = onLogout) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar Sesión", tint = Color.Red.copy(alpha = 0.7f))
            }
        }
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
data class SpendingSlice(
    val name: String,
    val amount: Double,
    val color: Color
)
@SuppressLint("DefaultLocale")
@Composable
fun SpendingBreakdownCard(total: Double, efectivo: Double, tarjetas: Map<String, Double>) {
    // 1. Preparamos los datos de la gráfica
    val slices = remember(efectivo, tarjetas) {
        val list = mutableListOf<SpendingSlice>()
        if (efectivo > 0) list.add(SpendingSlice("Efectivo", efectivo, PrimaryGreen))

        val cardColors = listOf(DarkGreen, Color(0xFF004D40), Color(0xFF1B5E20))
        tarjetas.entries.forEachIndexed { index, entry ->
            list.add(SpendingSlice(entry.key, entry.value, cardColors.getOrElse(index) { DarkGreen }))
        }
        list
    }

    Card(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Título
            Row(modifier = Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text("Desglose de gastos mensual", fontWeight = FontWeight.Bold)
                Surface(color = PrimaryGreen.copy(alpha = 0.1f), shape = CircleShape) {
                    Text("+12% vs last mo", color = PrimaryGreen, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Gráfico
            Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.size(140.dp)) {
                    drawCircle(color = LightGreenBg, style = Stroke(width = 45f))
                    var currentStartAngle = -90f

                    slices.forEach { slice ->
                        val sweepAngle = if (total > 0) (360 * (slice.amount / total)).toFloat() else 0f
                        drawArc(
                            color = slice.color,
                            startAngle = currentStartAngle,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            style = Stroke(width = 45f)
                        )
                        currentStartAngle += sweepAngle
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Formato de moneda para el centro
                    Text("$${String.format("%,.0f", total)}", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text("TOTAL", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Leyenda Dinámica (Nu, Stori, Efectivo...)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                slices.chunked(3).forEach { rowSlices ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        rowSlices.forEach { slice ->
                            LegendItem(slice.name, "$${String.format("%.0f", slice.amount)}", slice.color)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LegendItem(label: String, amount: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).background(color, CircleShape))
            Spacer(modifier = Modifier.width(4.dp))
            Text(label.uppercase(), fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
        }
        Text(amount, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun QuickAddExpenseCard(viewModel: DashboardViewModel) {
    var amount by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Food") }
    var paymentMethod by remember { mutableStateOf("Efectivo") }
    val savedCards = listOf("Visa •••• 4242", "MasterCard •••• 8812", "Amex •••• 7005")
    var selectedCard by remember { mutableStateOf(savedCards[0]) }
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
    Card(
        modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Agregar Gastos", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            TextField(
                value = amount,
                onValueChange = { newValue ->
                    if (newValue.all { it.isDigit() || it == '.' }) {
                        amount = newValue
                    }
                }, textStyle = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                placeholder = { Text("0.00", fontSize = 24.sp, fontWeight = FontWeight.Bold) },
                prefix = { Text("$ ", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = PrimaryGreen
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

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
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    savedCards.forEach { card ->
                        InputChip(
                            selected = selectedCard == card,
                            onClick = { selectedCard = card },
                            label = { Text(card, fontSize = 12.sp) }
                        )
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

            Button(
                onClick = {
                    viewModel.addExpense(
                    amountStr = amount,
                    category = selectedCategory,
                    method = paymentMethod,
                    cardId = if (paymentMethod == "Tarjeta") selectedCard else null
                )
                },
                enabled = !viewModel.isLoading,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
            ) {
                Text("Añadir Gastos", color = Color(0xFF112116), fontWeight = FontWeight.Bold, fontSize = 16.sp)
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

@Composable
fun AlertItem(title: String, subtitle: String, amount: String, color: Color, icon: ImageVector) {
    Surface(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp).fillMaxWidth(),
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).background(color.copy(alpha = 0.2f), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = color)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1F)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(subtitle, fontSize = 12.sp, color = Color.Gray)
            }
            Text(amount, fontWeight = FontWeight.Bold, color = color, fontSize = 14.sp)
        }
    }
}

@Composable
fun BottomNavBar() {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)) {
        NavigationBarItem(selected = true, onClick = {}, icon = { Icon(Icons.Default.GridView, null) }, label = { Text("DASHBOARD") })
        NavigationBarItem(selected = false, onClick = {}, icon = { Icon(Icons.Default.AccountBalanceWallet, null) }, label = { Text("ACCOUNTS") })
        NavigationBarItem(selected = false, onClick = {}, icon = { Icon(Icons.Default.InsertChart, null) }, label = { Text("ANALYTICS") })
        NavigationBarItem(selected = false, onClick = {}, icon = { Icon(Icons.Default.Settings, null) }, label = { Text("SETTINGS") })
    }
}