package com.example.appfinanzas.ui.dashboard.components

import android.annotation.SuppressLint
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appfinanzas.ui.dashboard.SpendingSlice
import kotlin.collections.chunked
import kotlin.collections.forEach
import com.example.appfinanzas.ui.theme.PrimaryGreen
import com.example.appfinanzas.ui.theme.DarkGreen
import com.example.appfinanzas.ui.theme.LightGreenBg

@SuppressLint("DefaultLocale")
@Composable
fun SpendingBreakdownCard(total: Double, efectivo: Double, tarjetas: Map<String, Double>) {
    val slices = remember(efectivo, tarjetas) {
        val list = mutableListOf<SpendingSlice>()
        if (efectivo > 0) list.add(SpendingSlice("Efectivo", efectivo, PrimaryGreen))

        val cardColors = listOf(
        Color(0xFF2E7D32), // Green
        Color(0xFF1565C0), // Blue
        Color(0xFF6A1B9A), // Purple
        Color(0xFFEF6C00), // Orange
        Color(0xFF00838F), // Teal
        Color(0xFFC62828), // Red
        Color(0xFF4527A0), // Indigo
        Color(0xFF2E7D32)  // Fallback green
    )
        tarjetas.entries.forEachIndexed { index, entry ->
            list.add(SpendingSlice(entry.key, entry.value, cardColors.getOrElse(index) { DarkGreen }))
        }
        list
    }
    var animationPlayed by remember { mutableStateOf(false) }
    val curFactor by animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "DonaAnim"
    )
    LaunchedEffect(Unit) {
        animationPlayed = true
    }

    Card(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Título
            Row(modifier = Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text("Gastos Mensuales", fontWeight = FontWeight.Bold)
                Surface(color = PrimaryGreen.copy(alpha = 0.1f), shape = CircleShape) {
                    Text("+12% vs last month", color = PrimaryGreen, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
            // Gráfico
            Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.size(140.dp)) {
                    drawCircle(color = LightGreenBg, style = Stroke(width = 45f))

                    var currentStartAngle = -90f

                    slices.forEach { slice ->
                        // Multiplicamos el sweepAngle por el factor de animación (curFactor)
                        val totalSweep = if (total > 0) (360 * (slice.amount / total)).toFloat() else 0f
                        val animatedSweep = totalSweep * curFactor

                        drawArc(
                            color = slice.color,
                            startAngle = currentStartAngle,
                            sweepAngle = animatedSweep,
                            useCenter = false,
                            style = Stroke(width = 45f)
                        )

                        currentStartAngle += animatedSweep
                    }
                }
                // Animamos también el texto central para que aparezca con un "Fade In"
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.alpha(curFactor)) {
                    Text("$${String.format("%,.0f", total)}", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text("TOTAL", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Leyenda Categorias
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