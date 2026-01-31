package com.example.appfinanzas.ui.wallet.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appfinanzas.data.model.CreditCard

// Colores sugeridos para las tarjetas
val cardColorOptions = listOf(
    Pair(Color(0xFF1B5E20), Color(0xFF4CAF50)), // Verde
    Pair(Color(0xFF0D47A1), Color(0xFF42A5F5)), // Azul
    Pair(Color(0xFF311B92), Color(0xFF7E57C2)), // Morado
    Pair(Color(0xFFB71C1C), Color(0xFFEF5350)), // Rojo
    Pair(Color(0xFF212121), Color(0xFF757575))  // Negro
)

@Composable
fun AddCardDialog(
    onDismiss: () -> Unit,
    onConfirm: (CreditCard) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var bank by remember { mutableStateOf("") }
    var lastFour by remember { mutableStateOf("") }
    var expiry by remember { mutableStateOf("") }
    var cutDay by remember { mutableStateOf("") }
    var dueDay by remember { mutableStateOf("") }
    var selectedColors by remember { mutableStateOf(cardColorOptions[0]) }

    // Validaciones
    val cutDayInt = cutDay.toIntOrNull() ?: 0
    val dueDayInt = dueDay.toIntOrNull() ?: 0
    val isFormValid = name.isNotBlank() &&
            bank.isNotBlank() &&
            lastFour.length == 4 &&
            expiry.length == 4 && // 4 números (MMYY)
            cutDayInt in 1..31 &&
            dueDayInt in 1..31

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Tarjeta", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Apodo (Ej: Nómina)") })
                OutlinedTextField(value = bank, onValueChange = { bank = it }, label = { Text("Banco") })

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = lastFour,
                        onValueChange = { if (it.length <= 4) lastFour = it.filter { c -> c.isDigit() } },
                        label = { Text("Últimos 4") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = expiry,
                        onValueChange = { if (it.length <= 4) expiry = it.filter { c -> c.isDigit() } },
                        label = { Text("Vence (MMAA)") },
                        placeholder = { Text("MM/AA") },
                        modifier = Modifier.weight(1f),
                        visualTransformation = DateTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = cutDay,
                        onValueChange = { cutDay = it.filter { c -> c.isDigit() } },
                        label = { Text("Día Corte") },
                        isError = cutDay.isNotEmpty() && cutDayInt !in 1..31,
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = dueDay,
                        onValueChange = { dueDay = it.filter { c -> c.isDigit() } },
                        label = { Text("Día Pago") },
                        isError = dueDay.isNotEmpty() && dueDayInt !in 1..31,
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                Text("Selecciona un color", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    cardColorOptions.forEach { colors ->
                        Box(
                            modifier = Modifier
                                .size(35.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            colors.first,
                                            colors.second
                                        )
                                    )
                                )
                                .border(
                                    width = if (selectedColors == colors) 2.dp else 0.dp,
                                    color = Color.Black,
                                    shape = CircleShape
                                )
                                .clickable { selectedColors = colors }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val newCard = CreditCard(
                        name = name,
                        bankName = bank,
                        lastFourDigits = lastFour.toInt(),
                        expiryDate = expiry.chunked(2).joinToString("/"), // Lo guarda como MM/AA
                        cutOffDay = cutDayInt,
                        dueDay = dueDayInt,
                        colorStartInt = selectedColors.first.value.toLong(),
                        colorEndInt = selectedColors.second.value.toLong(),
                    )
                    onConfirm(newCard)
                },
                enabled = isFormValid
            ) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
class DateTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length >= 4) text.text.substring(0..3) else text.text
        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            if (i == 1) out += "/"
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 1) return offset
                if (offset <= 4) return offset + 1
                return 5
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 2) return offset
                if (offset <= 5) return offset - 1
                return 4
            }
        }
        return TransformedText(AnnotatedString(out), offsetMapping)
    }
}