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
        val finalCard = card.copy(id = newDoc.id, userId = userId)

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
    fun deleteCard(cardId: String, onComplete: (Boolean) -> Unit) {
        if (userId.isEmpty() || cardId.isEmpty()) return

        firestore.collection("users").document(userId)
            .collection("cards").document(cardId)
            .delete()
            .addOnCompleteListener { onComplete(it.isSuccessful) }
    }
    fun updateCard(card: CreditCard, onComplete: (Boolean) -> Unit) {
        if (userId.isEmpty() || card.id.isEmpty()) {
            onComplete(false)
            return
        }

        // Apuntamos EXACTAMENTE al documento con el ID de la tarjeta
        firestore.collection("users").document(userId)
            .collection("cards").document(card.id)
            .set(card)
            .addOnCompleteListener { onComplete(it.isSuccessful) }
    }
}