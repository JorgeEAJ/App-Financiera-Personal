package com.example.appfinanzas.ui.dashboard.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@SuppressLint("DefaultLocale")
@Composable
fun MonthlySummary(amount: Double) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text("RESUMEN MENSUAL", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
        Text("$${String.format("%,.2f", amount)}", fontSize = 40.sp, fontWeight = FontWeight.Bold)
    }
}