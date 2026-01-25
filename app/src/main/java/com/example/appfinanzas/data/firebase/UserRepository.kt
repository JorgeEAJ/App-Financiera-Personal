package com.example.appfinanzas.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    fun createUserIfNotExists(onComplete: (Boolean) -> Unit) {
        val currentUser = auth.currentUser ?: return
        val userRef = firestore.collection("users")
            .document(currentUser.uid)
        userRef.get()
            .addOnSuccessListener { document ->
                if (!document.exists()) {
                    val userData = hashMapOf(
                        "email" to currentUser.email,
                        "createdAt" to System.currentTimeMillis()
                    )
                    userRef.set(userData)
                        .addOnSuccessListener {
                            onComplete(true)
                        }
                        .addOnFailureListener {
                            onComplete(false)
                        }
                } else {
                    onComplete(true)
                }
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }
    fun loginUser(email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, pass)
            .addOnSuccessListener {
                createUserIfNotExists { success ->
                    if (success) onResult(true, null)
                    else onResult(false, "Error al guardar perfil")
                }
            }
            .addOnFailureListener { e ->
                onResult(false, e.localizedMessage)
            }
    }
    fun signUpUser(email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnSuccessListener {
                createUserIfNotExists { success ->
                    if (success) onResult(true, null)
                    else onResult(false, "Error al crear perfil en base de datos")
                }
            }
            .addOnFailureListener { e ->
                onResult(false, e.localizedMessage)
            }
    }
}
