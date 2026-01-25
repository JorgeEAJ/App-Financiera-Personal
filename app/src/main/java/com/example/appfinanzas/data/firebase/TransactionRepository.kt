package com.example.appfinanzas.data.firebase

import com.example.appfinanzas.data.model.Transaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TransactionRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun addTransaction(transaction: Transaction, onComplete: (Boolean) -> Unit) {
        val uid = auth.currentUser?.uid ?: return

        // Creamos un documento con ID automático
        val newDoc = firestore.collection("transactions").document()
        val finalTransaction = transaction.copy(id = newDoc.id, userId = uid)

        newDoc.set(finalTransaction)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }
    fun getTransactions(onUpdate: (List<Transaction>) -> Unit) {
        val uid = auth.currentUser?.uid ?: return

        firestore.collection("transactions")
            .whereEqualTo("userId", uid)
            // .orderBy("date", Query.Direction.DESCENDING) // Necesitarás crear un índice en Firebase para esto
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                val list = snapshot?.documents?.mapNotNull { it.toObject(Transaction::class.java) } ?: emptyList()
                onUpdate(list)
            }
    }
}