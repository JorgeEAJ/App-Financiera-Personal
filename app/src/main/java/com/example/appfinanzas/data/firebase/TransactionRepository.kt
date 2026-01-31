package com.example.appfinanzas.data.firebase

import com.example.appfinanzas.data.model.Transaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TransactionRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    fun addTransaction(transaction: Transaction, onComplete: (Boolean) -> Unit) {

        // Creamos un documento con ID automático
        val newDoc = firestore.collection("transactions").document()
        val finalTransaction = transaction.copy(id = newDoc.id, userId = userId)

        newDoc.set(finalTransaction)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }
    fun getTransactions(onUpdate: (List<Transaction>) -> Unit) {
        firestore.collection("transactions")
            .whereEqualTo("userId", userId)
            // .orderBy("date", Query.Direction.DESCENDING) // Necesitarás crear un índice en Firebase para esto
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                val list = snapshot?.documents?.mapNotNull { it.toObject(Transaction::class.java) } ?: emptyList()
                onUpdate(list)
            }
    }
    fun deleteTransaction(transactionId: String, onComplete: (Boolean) -> Unit) {
        firestore.collection("transactions").document(transactionId)
            .delete()
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }
}