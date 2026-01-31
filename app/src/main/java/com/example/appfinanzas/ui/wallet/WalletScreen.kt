package com.example.appfinanzas.ui.wallet

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appfinanzas.data.model.CreditCard
import com.example.appfinanzas.ui.theme.DarkGreen
import com.example.appfinanzas.ui.theme.PrimaryGreen
import com.example.appfinanzas.ui.wallet.components.AddCardDialog
import com.example.appfinanzas.ui.wallet.components.CardDateDetails
import com.example.appfinanzas.ui.wallet.components.VisualCard

@Composable
fun WalletScreen(viewModel: WalletViewModel = viewModel()) {
    val cards = viewModel.cards
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true},
                containerColor = PrimaryGreen
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black)
            }
        }
    ) { padding ->
        if (showDialog) {
            AddCardDialog(
                onDismiss = { showDialog = false },
                onConfirm = { card ->
                    viewModel.addCard(card)
                    showDialog = false
                }
            )
        }
        if (cards.isEmpty()) {
            // Mensaje si no hay tarjetas
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No tienes tarjetas registradas", color = Color.Gray)
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
                item {
                    Text("Mis Tarjetas", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(modifier = Modifier.height(20.dp))
                }

                items(cards) { card ->
                    Column(modifier = Modifier.padding(bottom = 24.dp)) {
                        VisualCard(card)
                        Spacer(modifier = Modifier.height(12.dp))
                        CardDateDetails(card)
                    }
                }
            }
        }
    }
}