package com.example.appfinanzas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.appfinanzas.ui.auth.LoginScreen
import com.example.appfinanzas.ui.auth.SignUpScreen
import com.example.appfinanzas.ui.dashboard.DashboardScreen
import com.example.appfinanzas.ui.theme.AppFinanzasTheme
import com.google.firebase.auth.FirebaseAuth

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
                        DashboardScreen(onLogout = {
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