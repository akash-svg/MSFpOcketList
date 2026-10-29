package com.msfpocketlist.syncutil

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.msfpocketlist.R
import com.msfpocketlist.util.Session
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

/** Daily background download of every dataset, with progress in a foreground notification. */
class SyncService : Service() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var job: Job? = null
    private val sync: SyncRepository by inject()
    private val session: Session by inject()
    private val channelId by lazy { "$packageName.SyncService" }

    override fun onBind(intent: Intent?) = null

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, getString(R.string.data_sync_service_name), NotificationManager.IMPORTANCE_LOW)
            channel.description = getString(R.string.complete_data_backup_service_notification_channel_desc)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
        startForeground(NOTIFICATION_ID, notification(0))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (job?.isActive != true) job = scope.launch { syncAll() }
        return START_NOT_STICKY
    }

    @SuppressLint("MissingPermission") // progress updates are best-effort
    private suspend fun syncAll() {
        var allOk = true
        sync.tasks.forEachIndexed { i, task ->
            val ok = try {
                task.run()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                false
            }
            allOk = allOk && ok
            NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, notification((i + 1) * 100 / sync.tasks.size))
        }
        // Only a complete sync counts; otherwise the next app start tries again.
        if (allOk) session.markSynced()
        stopSelf()
    }

    private fun notification(progress: Int) = NotificationCompat.Builder(this, channelId)
        .setOngoing(true)
        .setSmallIcon(R.drawable.ic_notification_icon)
        .setContentTitle(getString(R.string.app_name))
        .setContentText(if (progress == 0) "Syncing" else "$progress%")
        .setProgress(100, progress, false)
        .build()

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    private companion object {
        const val NOTIFICATION_ID = 2
    }
}
