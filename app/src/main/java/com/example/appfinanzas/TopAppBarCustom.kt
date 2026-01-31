package com.example.appfinanzas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Notifications
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
import com.example.appfinanzas.ui.theme.PrimaryGreen


@Composable
fun TopAppBarCustom(userName: String,onLogout: () -> Unit) {
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
            Text(userName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
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