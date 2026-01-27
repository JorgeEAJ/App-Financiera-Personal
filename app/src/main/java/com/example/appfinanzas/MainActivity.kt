package com.example.appfinanzas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.appfinanzas.ui.auth.LoginScreen
import com.example.appfinanzas.ui.auth.SignUpScreen
import com.example.appfinanzas.ui.dashboard.DashboardContent
import com.example.appfinanzas.ui.theme.AppFinanzasTheme
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.compose.composable
import com.example.appfinanzas.BottomNavBar
import com.example.appfinanzas.Screen
import com.example.appfinanzas.ui.wallet.WalletScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppFinanzasTheme {
                var currentScreen by remember { mutableStateOf("login") }
                var isLoggedIn by remember {
                    mutableStateOf(FirebaseAuth.getInstance().currentUser != null)
                }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (isLoggedIn) {
                        MainApp(onLogout = {
                            FirebaseAuth.getInstance().signOut()
                            isLoggedIn = false
                        })
                    } else {
                        when (currentScreen) {
                            "login" -> LoginScreen(
                                onLoginSuccess = { isLoggedIn = true },
                                onNavigateToSignUp = { currentScreen = "signup" }
                            )
                            "signup" -> SignUpScreen(
                                onSignUpSuccess = { isLoggedIn = true },
                                onBackToLogin = { currentScreen = "login" }
                            )
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun MainApp(onLogout: () -> Unit) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            // El NavController DEBE pasarse aquí
            BottomNavBar(navController)
        }
    ) { innerPadding ->
        // El NavHost conecta el NavController con las pantallas
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardContent(onLogout = {
                    FirebaseAuth.getInstance().signOut()
                })
            }
            composable(Screen.Wallet.route) {
                WalletScreen()
            }
            composable(Screen.Analytics.route) {
                Surface { Text("Pantalla de Analytics") }
            }
            composable(Screen.Settings.route) {
                Surface { Text("Pantalla de Configuración") }
            }
        }
    }
}
