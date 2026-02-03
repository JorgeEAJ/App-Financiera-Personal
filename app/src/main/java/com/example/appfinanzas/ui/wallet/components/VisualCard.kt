package com.example.appfinanzas.ui.wallet.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.appfinanzas.data.model.CreditCard
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Contactless
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun VisualCard(card: CreditCard, onDeleteClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(listOf(card.colorStart, card.colorEnd)))
            .padding(24.dp)
    ){
        // --- BOTÓN DE BORRAR ---
        IconButton(
            onClick = onDeleteClick,
            modifier = Modifier.align(Alignment.TopEnd).offset(x = 12.dp, y = (-12).dp)
        ) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "Borrar tarjeta",
                tint = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.size(24.dp)
            )
        }
        // Icono de Contactless (Efecto opacidad)
        Icon(
            Icons.Default.Contactless,
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(60.dp)
                .alpha(0.2f),
            tint = Color.White
        )

        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(card.bankName.uppercase(), fontSize = 10.sp, color = Color.White.copy(alpha = 0.7f), letterSpacing = 2.sp)
                    Text(card.name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                Text("•••• •••• •••• ${card.lastFourDigits}", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Medium, letterSpacing = 2.sp)
                Column(horizontalAlignment = Alignment.End) {
                    Text("EXP", fontSize = 8.sp, color = Color.White.copy(alpha = 0.6f))
                    Text(card.expiryDate, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}