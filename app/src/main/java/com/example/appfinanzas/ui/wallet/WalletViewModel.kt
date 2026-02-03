package com.example.appfinanzas.ui.wallet

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.appfinanzas.data.firebase.WalletRepository
import com.example.appfinanzas.data.model.CreditCard
import com.example.appfinanzas.ui.notifications.NotificationWorker
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

class WalletViewModel(application: Application) : AndroidViewModel(application) {
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
    fun addCard(card: CreditCard) {
        repository.saveCard(card) { success ->
            if (success) loadCards()
        }
    }
    fun deleteCard(card: CreditCard) {
        repository.deleteCard(card.id) { /* opcional: manejar éxito */ }
    }
    fun toggleReminders(card: CreditCard, isActive: Boolean) {
        val updatedCard = card.copy(remindersActive = isActive)
        repository.updateCard(updatedCard) { /* opcional: manejar éxito */ }
    }
    fun scheduleNotification(card: CreditCard) {

        val today = LocalDate.now()
        var dueDate = today.withDayOfMonth(card.dueDay.coerceIn(1, today.lengthOfMonth()))
        if (today.isAfter(dueDate)) {
            val nextMonth = today.plusMonths(1)
            dueDate = nextMonth.withDayOfMonth(card.dueDay.coerceIn(1, nextMonth.lengthOfMonth()))
        }
        val reminderDate = dueDate.minusDays(2)
        val reminderDateTime = LocalDateTime.of(reminderDate, LocalTime.of(9, 0))
        val now = LocalDateTime.now()
        val delayInSeconds = if (now.isAfter(reminderDateTime)) {
            10L
        } else {
            Duration.between(now, reminderDateTime).seconds
        }
        val data = workDataOf(
            "cardId" to card.id,
            "cardName" to card.name,
            "dueDay" to card.dueDay
        )
        val notificationRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(delayInSeconds, TimeUnit.SECONDS)
            .setInputData(data)
            .addTag(card.id)
            .build()

        WorkManager.getInstance(getApplication()).enqueueUniqueWork(
            card.id,
            ExistingWorkPolicy.REPLACE,
            notificationRequest
        )
    }

    fun cancelNotification(cardId: String) {
        WorkManager.getInstance(getApplication()).cancelAllWorkByTag(cardId)
    }
}