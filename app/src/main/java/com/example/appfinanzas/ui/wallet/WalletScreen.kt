package com.example.appfinanzas.ui.wallet

import android.os.Build
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
import com.example.appfinanzas.ui.theme.PrimaryGreen
import com.example.appfinanzas.ui.wallet.components.AddCardDialog
import com.example.appfinanzas.ui.wallet.components.CardDateDetails
import com.example.appfinanzas.ui.wallet.components.VisualCard
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun WalletScreen(viewModel: WalletViewModel = viewModel()) {
    val cards = viewModel.cards
    var showDialog by remember { mutableStateOf(false) }
    val notificationPermissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(android.Manifest.permission.POST_NOTIFICATIONS)
    } else {
        null
    }

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
                        VisualCard(
                            card = card,
                            onDeleteClick = { viewModel.deleteCard(card) }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        CardDateDetails(
                            card = card,
                            onReminderChange = { isActive ->
                                if (isActive) {
                                    // Intentar pedir permiso (Android 13+)
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        if (notificationPermissionState?.status?.isGranted == false) {
                                            notificationPermissionState.launchPermissionRequest()
                                        }
                                    }
                                    //Programar la notificación
                                    viewModel.scheduleNotification(card)
                                } else {
                                    viewModel.cancelNotification(card.id)
                                }
                                viewModel.toggleReminders(card, isActive)
                            }
                        )
                    }
                }
            }
        }
    }
}