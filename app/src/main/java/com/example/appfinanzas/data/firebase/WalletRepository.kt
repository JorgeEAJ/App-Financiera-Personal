package com.example.appfinanzas.data.firebase

import com.example.appfinanzas.data.model.CreditCard
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class WalletRepository {
    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // Guardar una tarjeta nueva
    fun saveCard(card: CreditCard, onComplete: (Boolean) -> Unit) {
        if (userId.isEmpty()) return
        db.collection("users").document(userId).collection("cards")
            .add(card)
            .addOnCompleteListener { onComplete(it.isSuccessful) }
    }

    // Escuchar tarjetas en tiempo real
    fun getCards(onUpdate: (List<CreditCard>) -> Unit) {
        if (userId.isEmpty()) return
        db.collection("users").document(userId).collection("cards")
            .addSnapshotListener { snapshot, _ ->
                val cards = snapshot?.toObjects(CreditCard::class.java) ?: emptyList()
                onUpdate(cards)
            }
    }
}