package com.example.appfinanzas.ui.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class NotificationWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        val cardId = inputData.getString("cardId") ?: return Result.failure()
        val cardName = inputData.getString("cardName") ?: "Tarjeta"
        val dueDay = inputData.getInt("dueDay", 1)

        // Mostrar la notificación actual
        showNotification("¡Recordatorio de Pago!", "Tu tarjeta $cardName vence en 2 días.")

        // Auto-programar la del próximo mes
        scheduleNextMonth(cardId, cardName, dueDay)

        return Result.success()
    }

    private fun scheduleNextMonth(id: String, name: String, dueDay: Int) {
        // Calculamos el próximo mes (30 días después aproximadamente o mes exacto)
        val nextMonthDate = LocalDate.now().plusMonths(1)
        val nextDueDate = nextMonthDate.withDayOfMonth(dueDay.coerceIn(1, nextMonthDate.lengthOfMonth()))
        val nextReminder = nextDueDate.minusDays(2).atTime(9, 0)

        val delay = Duration.between(LocalDateTime.now(), nextReminder).seconds

        val nextData = workDataOf(
            "cardId" to id,
            "cardName" to name,
            "dueDay" to dueDay
        )

        val nextRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(delay, TimeUnit.SECONDS)
            .setInputData(nextData)
            .addTag(id)
            .build()

        // Se encola a sí mismo para el próximo mes
        WorkManager.getInstance(applicationContext).enqueueUniqueWork(
            id,
            ExistingWorkPolicy.REPLACE,
            nextRequest
        )
    }

    private fun showNotification(title: String, message: String) {
        val channelId = "payment_reminders"
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Crear Canal
        val channel =
            NotificationChannel(channelId, "Pagos", NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(Random.nextInt(), notification)
    }
}