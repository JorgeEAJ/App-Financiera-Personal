package com.example.appfinanzas.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    fun loginWithEmailOrName(
        identifier: String,
        pass: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        if (identifier.contains("@")) {
            // Es un correo, login directo
            performAuthLogin(identifier, pass, onResult)
        } else {
            // Es un nombre, buscamos el correo en Firestore
            firestore.collection("users")
                .whereEqualTo("name", identifier)
                .get()
                .addOnSuccessListener { query ->
                    if (!query.isEmpty) {
                        val email = query.documents[0].getString("email") ?: ""
                        performAuthLogin(email, pass, onResult)
                    } else {
                        onResult(false, "Usuario no encontrado")
                    }
                }
                .addOnFailureListener { onResult(false, it.localizedMessage) }
        }
    }

    private fun performAuthLogin(
        email: String,
        pass: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, pass)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { onResult(false, it.localizedMessage) }
    }

    fun signUpUser(
        email: String,
        pass: String,
        name: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        // 1. Primero verificamos si el nombre ya existe en la colección de usuarios
        firestore.collection("users")
            .whereEqualTo("name", name)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    onResult(false, "El nombre de usuario ya está en uso")
                } else {
                    // 2. El nombre está libre, procedemos a crear la cuenta en Auth
                    auth.createUserWithEmailAndPassword(email, pass)
                        .addOnSuccessListener { result ->
                            val uid = result.user?.uid ?: ""
                            val userData = hashMapOf(
                                "id" to uid,
                                "email" to email,
                                "name" to name,
                                "createdAt" to System.currentTimeMillis()
                            )
                            // 3. Guardamos los datos en Firestore
                            firestore.collection("users").document(uid).set(userData)
                                .addOnSuccessListener { onResult(true, null) }
                                .addOnFailureListener { onResult(false, "Error al guardar perfil") }
                        }
                        .addOnFailureListener { e ->
                            onResult(false, e.localizedMessage)
                        }
                }
            }
            .addOnFailureListener { e ->
                onResult(false, "Error de conexión: ${e.localizedMessage}")
            }
    }
}
