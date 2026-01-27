package com.example.appfinanzas.ui.dashboard.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appfinanzas.data.model.Transaction
import com.example.appfinanzas.ui.theme.DarkGreen
import com.example.appfinanzas.ui.theme.LightGreenBg
import com.example.appfinanzas.ui.theme.PrimaryGreen


@SuppressLint("DefaultLocale")
@Composable
fun TransactionItem(transaction: Transaction, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icono según categoría (puedes mejorar esto luego)
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(LightGreenBg, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = DarkGreen, modifier = Modifier.size(20.dp))
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(transaction.categoryId, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(transaction.paymentMethod.replaceFirstChar { it.uppercase() }, fontSize = 12.sp, color = Color.Gray)
        }

        val isIncome = transaction.type == "income"

        Text(
            text = "${if (isIncome) "+" else "-"}$${String.format("%.2f", transaction.amount)}",
            color = if (isIncome) PrimaryGreen else Color.Red.copy(alpha = 0.7f),
            fontWeight = FontWeight.Bold
        )

        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, contentDescription = "Borrar", tint = Color.Gray.copy(alpha = 0.5f), modifier = Modifier.size(20.dp))
        }
    }
}