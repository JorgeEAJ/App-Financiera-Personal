package com.example.appfinanzas.data.firebase

import com.example.appfinanzas.data.model.Category
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CategoryRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    fun getCategories(onUpdate: (List<Category>) -> Unit) {
        if (userId.isEmpty()) return
        firestore.collection("users").document(userId).collection("categories")
            .addSnapshotListener { snapshot, _ ->
                val list = snapshot?.toObjects(Category::class.java) ?: emptyList()
                onUpdate(list)
            }
    }

    fun addCategory(category: Category) {
        if (userId.isEmpty()) return
        val doc = firestore.collection("users").document(userId).collection("categories").document()
        doc.set(category.copy(id = doc.id, userId = userId))
    }

    fun deleteCategory(id: String) {
        if (userId.isEmpty()) return
        firestore.collection("users").document(userId).collection("categories").document(id).delete()
    }
}