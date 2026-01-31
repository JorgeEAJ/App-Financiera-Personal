package com.example.appfinanzas.data.firebase

import com.example.appfinanzas.data.model.CreditCard
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class WalletRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""


    // Guardar una tarjeta nueva
    fun saveCard(card: CreditCard, onComplete: (Boolean) -> Unit) {
        if (userId.isEmpty()) {
            onComplete(false)
            return
        }
        val cardsCollection = firestore.collection("users").document(userId).collection("cards")
        val newDoc = cardsCollection.document()
        val finalCard = card.copy(id = newDoc.id)

        newDoc.set(finalCard)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    // Escuchar tarjetas en tiempo real
    fun getCards(onUpdate: (List<CreditCard>) -> Unit) {
        if (userId.isEmpty()) return
        firestore.collection("users").document(userId).collection("cards")
            .addSnapshotListener { snapshot, _ ->
                val cards = snapshot?.toObjects(CreditCard::class.java) ?: emptyList()
                onUpdate(cards)
            }
    }
}