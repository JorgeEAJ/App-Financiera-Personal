package com.example.appfinanzas.ui.wallet.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appfinanzas.data.model.CreditCard
import com.example.appfinanzas.ui.theme.DarkGreen
import com.example.appfinanzas.ui.theme.PrimaryGreen
import androidx.compose.ui.graphics.Color

import java.util.Calendar

@Composable
fun CardDateDetails(card: CreditCard) {
    // LÓGICA DINÁMICA DE FECHAS
    val calendar = Calendar.getInstance()
    val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
    val currentMonth = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, java.util.Locale.getDefault())

    // Cálculo simple de días restantes para el pago
    val daysUntilDue = if (card.dueDay >= currentDay) {
        card.dueDay - currentDay
    } else {
        // Si ya pasó el día en este mes, calculamos para el siguiente (aproximado)
        (30 - currentDay) + card.dueDay
    }

    val isUrgent = daysUntilDue <= 3

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            // Fecha de Corte
            DateRow(
                icon = Icons.Default.CalendarToday,
                iconBg = PrimaryGreen.copy(alpha = 0.1f),
                iconTint = DarkGreen,
                label = "Fecha de Corte",
                sublabel = "Día ${card.cutOffDay} de cada mes",
                value = "$currentMonth ${card.cutOffDay}"
            )

            Divider(modifier = Modifier.padding(horizontal = 16.dp), color = Color.LightGray.copy(alpha = 0.05f))

            // Fecha de Pago Dinámica
            DateRow(
                icon = Icons.Default.EventBusy,
                iconBg = if (isUrgent) Color.Red.copy(alpha = 0.1f) else Color.Gray.copy(alpha = 0.1f),
                iconTint = if (isUrgent) Color.Red else Color.Gray,
                label = "Límite de Pago",
                sublabel = if (daysUntilDue == 0) "Vence hoy" else "Vence en $daysUntilDue días",
                value = "$currentMonth ${card.dueDay}",
                isUrgent = isUrgent
            )

            // INDICADOR DE NOTIFICACIONES (Nuevo)
            Divider(modifier = Modifier.padding(horizontal = 16.dp), color = Color.LightGray.copy(alpha = 0.05f))

            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Recordatorios", fontSize = 14.sp, color = Color.Gray)
                Text(
                    text = if (card.remindersActive) "ACTIVOS" else "DESACTIVADOS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (card.remindersActive) DarkGreen else Color.Gray
                )
            }
        }
    }
}


@Composable
fun DateRow(icon: ImageVector, iconBg: Color, iconTint: Color, label: String, sublabel: String, value: String, isUrgent: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).background(iconBg, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = iconTint, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Text(sublabel, fontSize = 12.sp, color = if (isUrgent) Color.Red else Color.Gray)
            }
        }
        Text(value, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = if (isUrgent) Color.Red else Color.Black)
    }
}