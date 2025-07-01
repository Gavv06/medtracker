package com.example.medtracker

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class NotificationWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {
    override fun doWork(): Result {
        val code = inputData.getString("code") ?: ""
        android.widget.Toast.makeText(context, "NotificationWorker déclenché pour $code", android.widget.Toast.LENGTH_LONG).show()
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, "medtracker_reminder")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Rappel de médication")
            .setContentText("Il est l'heure de prendre le médicament $code. Veuillez scanner le code DataMatrix pour valider la prise.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(code.hashCode(), notification)
        return Result.success()
    }
} 