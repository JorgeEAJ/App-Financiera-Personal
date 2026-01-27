package com.example.appfinanzas.ui.wallet

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.appfinanzas.data.firebase.WalletRepository
import com.example.appfinanzas.data.model.CreditCard

class WalletViewModel : ViewModel() {
    private val repository = WalletRepository()

    var cards by mutableStateOf<List<CreditCard>>(emptyList())
        private set

    init {
        loadCards()
    }

    private fun loadCards() {
        repository.getCards { list ->
            cards = list
        }
    }
}