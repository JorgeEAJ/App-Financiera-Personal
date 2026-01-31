package com.example.appfinanzas.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.appfinanzas.data.firebase.UserRepository

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, onNavigateToSignUp: () -> Unit) {
    var identifier by remember { mutableStateOf("") } // Cambiado de 'email' a 'identifier'
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    val repository = UserRepository()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Mi App de Finanzas", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = identifier,
            onValueChange = { identifier = it },
            label = { Text("Usuario o Correo electrónico") }, // Etiqueta más clara
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = null)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        errorMessage?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = {
                    if (identifier.isBlank() || password.isBlank()) {
                        errorMessage = "Completa los campos"
                        return@Button
                    }
                    isLoading = true
                    errorMessage = null

                    // USAMOS LA NUEVA LÓGICA DE BÚSQUEDA
                    repository.loginWithEmailOrName(identifier, password) { success, error ->
                        isLoading = false
                        if (success) onLoginSuccess() else errorMessage = error
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Iniciar Sesión")
            }
            TextButton(onClick = onNavigateToSignUp) {
                Text("¿No tienes cuenta? Registrate")
            }
        }
    }
}