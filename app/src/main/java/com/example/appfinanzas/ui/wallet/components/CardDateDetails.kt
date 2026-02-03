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
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
fun CardDateDetails(
    card: CreditCard,
    onReminderChange: (Boolean) -> Unit
) {
    val today = LocalDate.now()
    val currentYear = today.year
    val currentMonthValue = today.monthValue

// Intentamos crear la fecha de pago para el mes actual
    val dueDateThisMonth = try {
        today.withDayOfMonth(card.dueDay.coerceIn(1, today.lengthOfMonth()))
    } catch (e: Exception) {
        today.withDayOfMonth(today.lengthOfMonth())
    }

// Si la fecha de pago ya pasó este mes, calculamos la del próximo mes
    val nextDueDate = if (today.isAfter(dueDateThisMonth)) {
        val nextMonth = today.plusMonths(1)
        nextMonth.withDayOfMonth(card.dueDay.coerceIn(1, nextMonth.lengthOfMonth()))
    } else {
        dueDateThisMonth
    }

// Calculamos la diferencia exacta en días
    val daysUntilDue = ChronoUnit.DAYS.between(today, nextDueDate).toInt()
    val isUrgent = daysUntilDue <= 3
    val dynamicThemeColor = card.colorStart

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            // Fecha de Corte - Ahora usa el color de la tarjeta
            DateRow(
                icon = Icons.Default.CalendarToday,
                iconBg = dynamicThemeColor.copy(alpha = 0.1f),
                iconTint = dynamicThemeColor,
                label = "Fecha de Corte",
                sublabel = "Día ${card.cutOffDay} de cada mes",
                value = "$currentMonthValue/ ${card.cutOffDay}"
            )

            Divider(modifier = Modifier.padding(horizontal = 16.dp), color = Color.LightGray.copy(alpha = 0.05f))

            // Fecha de Pago - Rojo si es urgente, sino usa el color de la tarjeta
            DateRow(
                icon = Icons.Default.EventBusy,
                iconBg = if (isUrgent) Color.Red.copy(alpha = 0.1f) else dynamicThemeColor.copy(alpha = 0.1f),
                iconTint = if (isUrgent) Color.Red else dynamicThemeColor,
                label = "Límite de Pago",
                sublabel = if (daysUntilDue == 0) "Vence hoy" else "Vence en $daysUntilDue días",
                value = "$currentMonthValue/ ${card.dueDay}",
                isUrgent = isUrgent
            )

            Divider(modifier = Modifier.padding(horizontal = 16.dp), color = Color.LightGray.copy(alpha = 0.05f))

            // SECCIÓN DE RECORDATORIOS CON SWITCH
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Recordatorios", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Text(
                        if (card.remindersActive) "Notificaciones activas" else "Notificaciones apagadas",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Switch(
                    checked = card.remindersActive,
                    onCheckedChange = { onReminderChange(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = dynamicThemeColor,
                        uncheckedThumbColor = Color.LightGray,
                        uncheckedTrackColor = Color.LightGray.copy(alpha = 0.3f)
                    )
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
        Text(value, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = if (isUrgent) Color.Red else Color.White)
    }
}