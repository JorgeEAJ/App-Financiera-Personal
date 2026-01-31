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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.navigation.compose.composable
import com.example.appfinanzas.ui.wallet.WalletScreen
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppFinanzasTheme {
                var currentScreen by remember { mutableStateOf("login") }
                var userName by remember { mutableStateOf("Usuario") }
                var isLoggedIn by remember {
                    mutableStateOf(FirebaseAuth.getInstance().currentUser != null)
                }

                // Cargar nombre cuando inicie sesión
                LaunchedEffect(isLoggedIn) {
                    if (isLoggedIn) {
                        val uid = FirebaseAuth.getInstance().currentUser?.uid
                        if (uid != null) {
                            FirebaseFirestore.getInstance().collection("users")
                                .document(uid)
                                .addSnapshotListener { snapshot, _ ->
                                    userName = snapshot?.getString("name") ?: "Usuario"
                                }
                        }
                    }
                }

                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    if (isLoggedIn) {
                        MainApp(
                            userName = userName,
                            onLogout = {
                                FirebaseAuth.getInstance().signOut()
                                isLoggedIn = false
                            }
                        )
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
fun MainApp(userName: String,onLogout: () -> Unit) {
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            TopAppBarCustom(userName,onLogout = onLogout)
        },
        bottomBar = {
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
                DashboardContent()
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
